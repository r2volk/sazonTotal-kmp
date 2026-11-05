package org.sazontotal.project.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

/** Device theme shared by screens, controls and navigation. */
val LocalTemaOscuro = staticCompositionLocalOf { true }

enum class UsoColor { TEXTO, FONDO, BORDE }

/** Adapts presentation colors without changing layout, data or functional states. */
fun colorDeTema(color: Color, oscuro: Boolean, uso: UsoColor = UsoColor.TEXTO): Color {
    if (color == Color.Unspecified || color.alpha == 0f) return color
    if (oscuro) {
        if (uso == UsoColor.FONDO || (uso == UsoColor.TEXTO && color.copy(alpha = 1f) == Color.Black)) return color
        // Secondary labels and icons must remain readable even on disabled surfaces.
        val fondo = if (uso == UsoColor.BORDE) Colores.GrisMedio else Colores.GrisDeshabilitado
        val minimo = if (uso == UsoColor.BORDE) 3f else 4.5f
        var tinta = color
        repeat(48) {
            if ((tinta.luminance() + 0.05f) / (fondo.luminance() + 0.05f) >= minimo) return tinta
            tinta = Color(tinta.red + (1f - tinta.red) * 0.06f,
                tinta.green + (1f - tinta.green) * 0.06f,
                tinta.blue + (1f - tinta.blue) * 0.06f, color.alpha)
        }
        return tinta
    }
    val maximo = maxOf(color.red, color.green, color.blue)
    val minimo = minOf(color.red, color.green, color.blue)
    val neutro = maximo - minimo < 0.035f
    if (uso == UsoColor.FONDO) {
        if (maximo == 0f || color.copy(alpha = 1f) == Color(0xFF121212)) return Color.White.copy(alpha = color.alpha)
        if (neutro && maximo < 0.5f) {
            val superficie = when {
                maximo < 0.095f -> Color(0xFFFAFAFC)
                maximo < 0.15f -> Color(0xFFF0F1F4)
                maximo < 0.22f -> Color(0xFFE3E6EB)
                else -> Color(0xFFD6D6DF)
            }
            return superficie.copy(alpha = color.alpha)
        }
        if (!neutro && maximo < 0.52f) {
            return Color(
                0.87f + 0.13f * color.red / maximo,
                0.87f + 0.13f * color.green / maximo,
                0.87f + 0.13f * color.blue / maximo,
                color.alpha
            )
        }
        return color
    }
    if (uso == UsoColor.BORDE && neutro) return Color(0xFF77818E).copy(alpha = color.alpha)
    if (neutro && maximo > 0.92f) return Color(0xFF1B1B1F).copy(alpha = color.alpha)
    if (neutro && maximo > 0f) return Color(0xFF50545D).copy(alpha = color.alpha)
    // Keep hue; only darken foreground accents that would disappear on white.
    val contrasteMinimo = if (uso == UsoColor.BORDE) 3f else 6.5f
    var resultado = color
    repeat(32) {
        if (1.05f / (resultado.luminance() + 0.05f) >= contrasteMinimo) return resultado
        resultado = Color(resultado.red * 0.92f, resultado.green * 0.92f, resultado.blue * 0.92f, color.alpha)
    }
    return resultado
}

/** Explicit foreground/background pairs for the persistent bottom navigation. */
data class ColoresNavegacion(
    val superficie: Color,
    val borde: Color,
    val seleccion: Color,
    val sobreSeleccion: Color,
    val inactivo: Color
)

fun coloresNavegacion(oscuro: Boolean) = if (oscuro) ColoresNavegacion(
    superficie = Color(0xFF202327), borde = Color(0xFF78828E),
    seleccion = Colores.VerdePrimario, sobreSeleccion = Color.Black,
    inactivo = Color(0xFFC2CAD3)
) else ColoresNavegacion(
    superficie = Color(0xFFE9ECF0), borde = Color(0xFF77818E),
    seleccion = Color(0xFF166534), sobreSeleccion = Color.White,
    inactivo = Color(0xFF424C59)
)

@Composable
fun SazonTheme(oscuro: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colores = if (oscuro) darkColorScheme(
        primary = Colores.VerdePrimario, onPrimary = Color.Black,
        primaryContainer = Colores.VerdeOscuro, onPrimaryContainer = Colores.VerdePrimario,
        secondary = Colores.VerdePrimario, onSecondary = Color.Black,
        background = Color.Black, onBackground = Color.White,
        surface = Colores.Superficie, onSurface = Color.White,
        surfaceVariant = Colores.GrisMedio, onSurfaceVariant = colorDeTema(Colores.TextoTenue, true),
        surfaceContainer = Colores.Superficie, surfaceContainerLow = Colores.Superficie,
        surfaceContainerHigh = Colores.Superficie, surfaceContainerHighest = Colores.GrisMedio,
        outline = colorDeTema(Colores.GrisDeshabilitado, true, UsoColor.BORDE), error = colorDeTema(Colores.Rojo, true),
        errorContainer = Color(0xFF3A1414), onErrorContainer = colorDeTema(Colores.Rojo, true)
    ) else lightColorScheme(
        primary = colorDeTema(Colores.VerdePrimario, false), onPrimary = Color.White,
        primaryContainer = Color(0xFFE1F5E6), onPrimaryContainer = Color(0xFF145C2C),
        secondary = colorDeTema(Colores.VerdePrimario, false), onSecondary = Color.White,
        background = Color.White, onBackground = Color(0xFF1B1B1F),
        surface = Color(0xFFF5F5F7), onSurface = Color(0xFF1B1B1F),
        surfaceVariant = Color(0xFFECECF0), onSurfaceVariant = Color(0xFF50545D),
        surfaceContainer = Color(0xFFF5F5F7), surfaceContainerLow = Color(0xFFFAFAFC),
        surfaceContainerHigh = Color(0xFFF0F0F4), surfaceContainerHighest = Color(0xFFE3E6EB),
        outline = Color(0xFF77818E), error = Color(0xFFB3261E),
        errorContainer = Color(0xFFFCE5E2), onErrorContainer = Color(0xFF8C1D18)
    )
    CompositionLocalProvider(LocalTemaOscuro provides oscuro) {
        MaterialTheme(colorScheme = colores, content = content)
    }
}
