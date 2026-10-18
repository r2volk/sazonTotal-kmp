package org.sazontotal.project.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cake
import androidx.compose.material.icons.outlined.DinnerDining
import androidx.compose.material.icons.outlined.LocalBar
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import org.sazontotal.project.theme.Colores

data class VisualCategoria(
    val icono: ImageVector,
    val fondo: Color,
    val tinta: Color
)

fun visualDeCategoria(categoria: String): VisualCategoria = when (categoria) {
    "Bebidas" -> VisualCategoria(
        icono = Icons.Outlined.LocalBar,
        fondo = Color(0xFF0B3820),
        tinta = Color(0xFF4ADE80)
    )

    "Postres" -> VisualCategoria(
        icono = Icons.Outlined.Cake,
        fondo = Color(0xFF3D1A2E),
        tinta = Color(0xFFF48FB1)
    )

    else -> VisualCategoria(
        icono = Icons.Outlined.DinnerDining,
        fondo = Color(0xFF3D2E0A),
        tinta = Colores.Ambar
    )
}