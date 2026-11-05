package org.sazontotal.project

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.compositeOver
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.sazontotal.project.theme.paletaAcento
import org.sazontotal.project.theme.Colores
import org.sazontotal.project.theme.UsoColor
import org.sazontotal.project.theme.coloresNavegacion
import org.sazontotal.project.theme.colorDeTema

class TemaColorTest {
    private fun contraste(a: Color, b: Color): Float =
        (maxOf(a.luminance(), b.luminance()) + 0.05f) /
            (minOf(a.luminance(), b.luminance()) + 0.05f)

    @Test
    fun familiasDeColorTienenSuperficiesVisiblesYAccionesLegibles() {
        val familias = listOf(Colores.VerdePrimario, Color(0xFF64A8F5),
            Color(0xFFB39DFF), Colores.Ambar, Colores.Rojo, Color(0xFFF59BCC))
        familias.forEach { acento -> listOf(false, true).forEach { oscuro ->
            val p = paletaAcento(acento, oscuro)
            assertTrue(contraste(p.tinta, p.superficie) >= 4.5f, "Texto de $acento, oscuro=$oscuro")
            assertTrue(contraste(p.sobreFuerte, p.fuerte) >= 4.5f, "Acción de $acento, oscuro=$oscuro")
            val fondo = if (oscuro) Color.Black else Color.White
            assertTrue(contraste(p.superficie, fondo) >= 1.3f, "Superficie de $acento casi invisible")
        } }
        listOf(Colores.VerdePrimario, Color(0xFF64A8F5)).forEach { acento ->
            listOf(false, true).forEach { oscuro ->
                val p = paletaAcento(acento, oscuro)
                val hero = if (oscuro) p.superficie else p.fuerte
                assertTrue(contraste(Color.White, hero) >= 4.5f)
                assertTrue(contraste(Color.White, Color.White.copy(alpha = 0.12f).compositeOver(hero)) >= 4.5f)
            }
        }
    }

    @Test
    fun oscuroConservaFondosYBotonesPeroHaceLegiblesLosTextosSecundarios() {
        listOf(Color.Black, Colores.Superficie, Colores.GrisMedio, Colores.VerdePrimario,
            Colores.Naranja).forEach { assertEquals(it, colorDeTema(it, true, UsoColor.FONDO)) }
        assertEquals(Color.Black, colorDeTema(Color.Black, true))
        listOf(Colores.TextoTenue, Color(0xFF6B7280), Color(0xFF5A5A5E),
            Color(0xFF3E6B4F), Colores.Rojo).forEach {
            assertTrue(contraste(colorDeTema(it, true), Colores.GrisDeshabilitado) >= 4.5f)
        }
    }

    @Test
    fun barraInferiorDistingueSeleccionIconosYContornoEnAmbosTemas() {
        listOf(false, true).forEach { oscuro ->
            val nav = coloresNavegacion(oscuro)
            assertTrue(contraste(nav.sobreSeleccion, nav.seleccion) >= 4.5f)
            assertTrue(contraste(nav.inactivo, nav.superficie) >= 4.5f)
            assertTrue(contraste(nav.borde, nav.superficie) >= 3f)
            assertTrue(contraste(nav.seleccion, nav.superficie) >= 3f)
        }
    }

    @Test
    fun camposYFiltrosConservanContornosVisibles() {
        listOf(false, true).forEach { oscuro ->
            val superficie = colorDeTema(Colores.Superficie, oscuro, UsoColor.FONDO)
            val borde = colorDeTema(Colores.GrisMedio, oscuro, UsoColor.BORDE)
            assertTrue(contraste(borde, superficie) >= 3f)
            val barra = colorDeTema(Colores.GrisDeshabilitado, oscuro, UsoColor.BORDE)
            assertTrue(contraste(barra, superficie) >= 3f)
            val filtroFondo = colorDeTema(Colores.VerdeOscuro, oscuro, UsoColor.FONDO)
            assertTrue(contraste(colorDeTema(Color.Green, oscuro), filtroFondo) >= 4.5f)
            assertTrue(contraste(Color.Black, colorDeTema(Colores.VerdeAcento, oscuro, UsoColor.FONDO)) >= 4.5f)
        }
        val carrito = colorDeTema(Color(0xFF2B2113), false, UsoColor.FONDO)
        assertTrue(carrito.red > carrito.blue, "El acceso al carrito debe conservar su tono cálido")
        assertEquals(paletaAcento(Colores.Ambar, false).superficie,
            colorDeTema(Color(0xFF4A2620), false, UsoColor.FONDO))
    }

    @Test
    fun claroDistingueFondoSuperficiesYBordes() {
        assertEquals(Color.White, colorDeTema(Color.Black, false, UsoColor.FONDO))
        val superficie = colorDeTema(Colores.Superficie, false, UsoColor.FONDO)
        val borde = colorDeTema(Colores.GrisMedio, false, UsoColor.BORDE)
        assertTrue(superficie.luminance() > 0.8f)
        assertTrue(borde.luminance() < superficie.luminance())
        assertTrue(colorDeTema(Color.White, false).luminance() < 0.05f)
    }

    @Test
    fun textoYEstadosTienenContrasteEnFondosClaros() {
        val tintas = listOf(Color.White, Colores.TextoTenue, Colores.VerdePrimario,
            Colores.Ambar, Colores.Naranja, Colores.Rojo, Color(0xFF64A8F5))
        val fondos = listOf(Color.Black, Colores.Superficie, Colores.GrisMedio)
        fondos.forEach { fondo ->
            val claro = colorDeTema(fondo, false, UsoColor.FONDO)
            tintas.forEach { tinta ->
                val texto = colorDeTema(tinta, false)
                val contraste = (claro.luminance() + 0.05f) / (texto.luminance() + 0.05f)
                assertTrue(contraste >= 4.5f, "Contraste $contraste: $tinta sobre $fondo")
            }
        }
    }

    @Test
    fun transparenciasYBotonesDeAcentoSeConservan() {
        assertEquals(Color.Transparent, colorDeTema(Color.Transparent, false, UsoColor.FONDO))
        assertEquals(Colores.VerdeBoton, colorDeTema(Colores.VerdeBoton, false, UsoColor.FONDO))
        assertEquals(Color.Black, colorDeTema(Color.Black, false))
        val original = Color(0xFF143D2B).copy(alpha = 0.2f)
        assertEquals(original.alpha, colorDeTema(original, false, UsoColor.FONDO).alpha)
    }
}
