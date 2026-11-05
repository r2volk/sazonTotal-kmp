package org.sazontotal.project.data.maps

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlin.test.*

class BusquedaDireccionesTest {
    private val plaza = SugerenciaDireccion("Plaza Norte", "Avenida Tomás Valle 1400, Independencia", Coordenada(-12.006267, -77.0588136), fuente = FuenteDireccion.OSM)

    @Test fun consultaExpandeAbreviaturasYContextoLocal() {
        assertEquals("Universidad Privada del Norte Los Olivos, Lima, Perú", consultaDireccion("UPN Los Olivos"))
        assertEquals("Avenida Carlos Izaguirre 490, Lima, Perú", consultaDireccion("Av. Carlos Izaguirre 490"))
        assertEquals("Plaza Norte, Lima", consultaDireccion("Plaza Norte, Lima"))
    }

    @Test fun photonConservaNombreDireccionYCoordenadasDelLugar() {
        val json = """{"features":[{"properties":{"name":"Plaza Norte","street":"Avenida Tomás Valle","housenumber":"1400","city":"Independencia","country":"Perú"},"geometry":{"coordinates":[-77.0588136,-12.006267]}}]}"""
        val lugar = leerSugerenciasPhoton(json, "Plaza Norte").single()
        assertEquals(plaza.coordenada, lugar.coordenada)
        assertEquals("Plaza Norte", lugar.nombre)
        assertEquals("Avenida Tomás Valle 1400, Independencia, Perú", lugar.direccion)
        assertTrue(lugar.textoCompleto.startsWith("Plaza Norte,"))
        assertFalse(lugar.aproximada)
    }

    @Test fun numeroNoConfirmadoSeMarcaSinInventarDireccion() {
        val json = """{"features":[{"properties":{"name":"Avenida Carlos Alberto Izaguirre","city":"Los Olivos"},"geometry":{"coordinates":[-77.0698867,-11.9907139]}}]}"""
        val lugar = leerSugerenciasPhoton(json, "Av Carlos Izaguirre 490").single()
        assertTrue(lugar.aproximada)
        assertNull(lugar.numero)
        assertFalse(lugar.textoCompleto.contains("490"))
    }

    @Test fun descartaCoordenadasInvalidas() {
        val json = """{"features":[{"properties":{"name":"Inválido"},"geometry":{"coordinates":[-77,150]}}]}"""
        assertTrue(leerSugerenciasPhoton(json, "Inválido").isEmpty())
        assertFalse(coordenadaValida(Double.NaN, -77.0))
    }

    @Test fun googleConservaIdentificadorParaResolverLaSeleccionExacta() {
        val json = """{"suggestions":[{"placePrediction":{"placeId":"lugar-123","structuredFormat":{"mainText":{"text":"Plaza Norte"},"secondaryText":{"text":"Independencia, Perú"}}}}]}"""
        val lugar = leerSugerenciasGoogle(json).single()
        assertEquals("lugar-123", lugar.placeId)
        assertEquals("Plaza Norte, Independencia, Perú", lugar.textoCompleto)
        assertNull(lugar.coordenada)
    }

    @Test fun combinaProveedoresSinDuplicarYDaPrioridadAlNumeroConfirmado() {
        val aproximada = plaza.copy(nombre = "Avenida Carlos Izaguirre", direccion = "Los Olivos", aproximada = true)
        val exacta = aproximada.copy(direccion = "Avenida Carlos Izaguirre 490, Los Olivos", numero = "490", aproximada = false, fuente = FuenteDireccion.DISPOSITIVO)
        assertEquals(exacta, ordenarSugerencias("Av Carlos Izaguirre 490", listOf(aproximada, exacta)).first())
        assertEquals(1, ordenarSugerencias("Plaza Norte", listOf(plaza, plaza.copy(fuente = FuenteDireccion.DISPOSITIVO))).size)
    }

    @Test fun publicaProveedorRapidoAntesDeEsperarAlLento() = runBlocking {
        withTimeout(2_000) {
            val liberarLento = CompletableDeferred<Unit>()
            val publicadas = mutableListOf<List<SugerenciaDireccion>>()
            val resultado = buscarEnParalelo("Plaza Norte", listOf(
                { liberarLento.await(); emptyList() },
                { listOf(plaza) }
            ), { publicadas.add(it); if (it.isNotEmpty()) liberarLento.complete(Unit) })
            assertEquals(listOf(plaza), publicadas.first())
            assertEquals(listOf(plaza), resultado)
        }
    }

    @Test fun cancelacionNoPublicaResultadosTardios() = runBlocking {
        val iniciado = CompletableDeferred<Unit>()
        var publicaciones = 0
        val tarea = launch {
            buscarEnParalelo("Plaza Norte", listOf({ iniciado.complete(Unit); CompletableDeferred<Unit>().await(); listOf(plaza) }), { publicaciones++ })
        }
        iniciado.await()
        tarea.cancelAndJoin()
        assertEquals(0, publicaciones)
    }

    @Test fun abreviaturaNoDuplicaLaDireccionSeleccionada() {
        val lugar = SugerenciaDireccion("Avenida Carlos Izaguirre 490", "Av. Carlos Izaguirre 490, Los Olivos 15301, Perú", fuente = FuenteDireccion.DISPOSITIVO)
        assertEquals(lugar.direccion, lugar.textoCompleto)
    }

    @Test fun sedeCorrespondeAlCampusLosOlivos() {
        assertEquals(Coordenada(-11.9595307, -77.0677791), SEDE_COORDENADA)
        assertTrue(DIRECCION_SEDE.contains("6062"))
        assertTrue(DIRECCION_SEDE.contains("Los Olivos"))
    }
}
