package org.sazontotal.project.theme

import androidx.compose.ui.graphics.Color

// paleta de la app. usar estas constantes en vez de literales hex.
object Colores {
    // grises
    val TextoTenue = Color(0xFF8E8E93)         // texto e iconos secundarios
    val Superficie = Color(0xFF1C1C1E)         // fondo de tarjeta y de campo de texto
    val GrisMedio = Color(0xFF2C2C2E)          // fondo elevado, borde, track de switch
    val GrisDeshabilitado = Color(0xFF3A3A3C) // switch apagado, boton apagado

    // verdes
    val VerdePrimario = Color(0xFF30D158)      // acento verde principal
    val VerdeBoton = Color(0xFF5DBF3E)         // fondo de boton de accion y FAB
    val VerdeOscuro = Color(0xFF0D3B20)        // fondo del filtro seleccionado
    val VerdeAcento = Color(0xFF22C55E)        // cursor de escritura y palomita

    // acentos calidos
    val Naranja = Color(0xFFFF9F0A)            // advertencia
    val Rojo = Color(0xFFFF453A)               // error, eliminar
    val Ambar = Color(0xFFFFB340)              // acento calido
}