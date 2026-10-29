package org.sazontotal.project.data.maps

import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.round
import kotlin.math.sin
import kotlin.math.sqrt

// error con mensaje amable cuando falla algun servicio de Google Maps.
class ErrorMaps(mensaje: String) : Exception(mensaje)

// verde de la marca para el pin de la sede en el mapa estatico (0xRRGGBB).
private const val VERDE_PIN = "0x30D158"

// un punto geografico (latitud y longitud, coordenadas WGS84).
data class Coordenada(val latitud: Double, val longitud: Double)

// resultado del calculo de distancia: texto listo para mostrar.
data class ResultadoDistancia(
    val kmTexto: String,
    val minutosTexto: String
)

/**
 * cliente de los servicios de Google Maps Platform, mismo patron del
 * GeminiService: HTTP puro con Ktor, sin SDKs nativos.
 *
 * servicios usados:
 *  - Geocoding API: direccion escrita -> coordenadas
 *  - Places API (New) Text Search: sugerencias mientras se escribe
 *  - Distance Matrix API: km y minutos entre la sede y el cliente
 *  - Maps Static API: URL de la imagen del mapa con los dos pines
 *
 * MODO STUB: esta version devuelve datos de ejemplo para que las pantallas
 * se pueda construir y compilar sin llamar a Google. Cuando exista la API key
 * se reemplazan los cuerpos por las llamadas HTTP reales (sin cambiar las
 * firmas, para que la UI no se toque).
 */
class GoogleMapsService(
    private val apiKey: String,
    private val sede: Coordenada = SEDE_COORDENADA
) {

    // sugerencias de direcciones mientras es el mesero (max 5, Places Text Search).
    suspend fun autocompletar(textoParcial: String): List<String> {
        if (apiKey.isBlank() || apiKey.startsWith("PEGA_")) return listaDemo
        return listaDemo.filter { contieneAlgunaPalabra(it, textoParcial) }
    }

    // convierte la direccion elegida en coordenadas (Geocoding API).
    suspend fun geocodificar(direccion: String): Coordenada {
        return mapaDemo.entries
            .firstOrNull { it.key.lowercase() == direccion.trim().lowercase() }
            ?.value
            ?: Coordenada(latitud = SEDE_COORDENADA.latitud + 0.005, longitud = SEDE_COORDENADA.longitud + 0.004)
    }

    // distancia y tiempo en moto desde la sede hasta el cliente (Distance Matrix API).
    suspend fun distancia(hasta: Coordenada): ResultadoDistancia {
        val km = haversineKm(sede, hasta)
        val minutos = (km / 22.0 * 60).toInt().coerceAtLeast(2) // moto promedio en Lima
        // redondeo a 1 decimal con aritmetica entera, multiplataforma.
        val kmRedondeado = round(km * 10.0).toInt() / 10.0
        return ResultadoDistancia(
            kmTexto = "$kmRedondeado km",
            minutosTexto = "~$minutos min en moto"
        )
    }

    // URL de la imagen del mapa estatico: un pin verde en la sede y uno rojo
    // en el cliente, unidos por una linea punteada (Maps Static API).
    // devuelve "" si no hay key: la pantalla muestra un cuadro placeholder.
    fun urlMapaEstatico(cliente: Coordenada, zoom: Int = 13): String {
        if (apiKey.isBlank() || apiKey.startsWith("PEGA_")) return ""

        val local = pinDe(sede, VERDE_PIN, "S") // S de la sede SazonTotal
        val destino = pinDe(cliente, "0xFFFF453A", "C")
        val linea = "path=weight:3|color:$VERDE_PIN" +
            "|${sede.latitud},${sede.longitud}|${cliente.latitud},${cliente.longitud}"
        return "https://maps.googleapis.com/maps/api/staticmap" +
            "?size=600x300&scale=2&zoom=$zoom" +
            "&markers=$local&markers=$destino&$linea" +
            "&key=$apiKey"
    }

    // abre la navegacion guiada en Google Maps o Waze (deep links universales).
    fun urlRutaGoogleMaps(hasta: Coordenada): String =
        "https://www.google.com/maps/dir/?api=1&origin=${sede.latitud},${sede.longitud}" +
            "&destination=${hasta.latitud},${hasta.longitud}&travelmode=driving"

    fun urlRutaWaze(hasta: Coordenada): String =
        "https://waze.com/ul?ll=${hasta.latitud},${hasta.longitud}&navigate=yes"

    private fun pinDe(coordenada: Coordenada, colorHex: String, letra: String): String =
        "color:$colorHex|label:$letra|${coordenada.latitud},${coordenada.longitud}"

    // demo: direcciones de Lima tipicas de una zona de entrega.
    private val listaDemo = listOf(
        "Parque Kennedy, Av. José Larco 900, Miraflores",
        "Calle Berlín 500, Miraflores",
        "Av. Arequipa 2500, Lince",
        "Jr. de la Unión 1000, Lima Centro",
        "Av. Oscar R. Benavides 2000, Cercado de Lima"
    )

    private val mapaDemo = mapOf(
        "Parque Kennedy, Av. José Larco 900, Miraflores" to Coordenada(latitud = -12.1207, longitud = -77.0297),
        "Calle Berlín 500, Miraflores" to Coordenada(latitud = -12.1199, longitud = -77.0359),
        "Av. Arequipa 2500, Lince" to Coordenada(latitud = -12.0900, longitud = -77.0321),
        "Jr. de la Unión 1000, Lima Centro" to Coordenada(latitud = -12.0463, longitud = -77.0297),
        "Av. Oscar R. Benavides 2000, Cercado de Lima" to Coordenada(latitud = -12.0523, longitud = -77.0846)
    )
}

// distancia en km entre dos coordenadas (formula de haversine).
// funciones de kotlin.math: iguales en Android, iOS y demas plataformas.
private fun haversineKm(a: Coordenada, b: Coordenada): Double {
    val tierra = 6371.0
    val dLat = (b.latitud - a.latitud) * PI / 180.0
    val dLng = (b.longitud - a.longitud) * PI / 180.0
    val h = sin(dLat / 2) * sin(dLat / 2) +
        cos(a.latitud * PI / 180.0) * cos(b.latitud * PI / 180.0) *
        sin(dLng / 2) * sin(dLng / 2)
    return 2 * tierra * asin(sqrt(h))
}

private fun contieneAlgunaPalabra(texto: String, parcial: String): Boolean {
    val abajo = parcial.trim().lowercase()
    if (abajo.isEmpty()) return false
    return texto.lowercase().split(" ", ",").any { palabra ->
        palabra.startsWith(abajo.take(4))
    } || texto.lowercase().contains(abajo)
}
