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

/** Semantic pairs used by every role, including colored cards, states and actions. */
data class PaletaAcento(
    val superficie: Color,
    val tinta: Color,
    val fuerte: Color,
    val sobreFuerte: Color
)

private data class TonosAcento(
    val clara: Long, val oscura: Long, val tintaClara: Long, val tintaOscura: Long,
    val fuerteClara: Long, val fuerteOscura: Long
)

fun paletaAcento(color: Color, oscuro: Boolean): PaletaAcento {
    val mayor = maxOf(color.red, color.green, color.blue)
    val menor = minOf(color.red, color.green, color.blue)
    val delta = mayor - menor
    val matiz = if (delta < 0.035f) 140f else {
        val sector = when (mayor) {
            color.red -> (color.green - color.blue) / delta
            color.green -> (color.blue - color.red) / delta + 2f
            else -> (color.red - color.green) / delta + 4f
        }
        (sector * 60f + 360f) % 360f
    }
    // Each family has a deliberate surface/ink pair; no near-white pastel conversion.
    val (clara, oscura, tintaClara, tintaOscura, fuerteClara, fuerteOscura) = when {
        matiz < 8f || matiz >= 350f -> TonosAcento(0xFFF3C3C9, 0xFF4B1D27, 0xFF801C2D, 0xFFFFA5B0, 0xFFB42332, 0xFFB42332)
        matiz < 65f -> TonosAcento(0xFFF6D69E, 0xFF46310D, 0xFF693B08, 0xFFFFCF7C, 0xFF8C510B, 0xFFFFB340)
        matiz < 170f -> TonosAcento(0xFFBFE3CD, 0xFF113D2B, 0xFF124E2D, 0xFF75E5A3, 0xFF17683F, 0xFF30D158)
        matiz < 240f -> TonosAcento(0xFFBEDCF5, 0xFF123652, 0xFF194C76, 0xFF91CEFF, 0xFF1764A0, 0xFF79C2FC)
        matiz < 285f -> TonosAcento(0xFFD8CAF2, 0xFF32234C, 0xFF493177, 0xFFD0B6FF, 0xFF65419E, 0xFFC4A6FF)
        else -> TonosAcento(0xFFEFC6DF, 0xFF472039, 0xFF72264F, 0xFFFFAFD9, 0xFF9B3670, 0xFFF59BCC)
    }
    val fuerte = Color(if (oscuro) fuerteOscura else fuerteClara)
    return PaletaAcento(
        superficie = Color(if (oscuro) oscura else clara),
        tinta = Color(if (oscuro) tintaOscura else tintaClara),
        fuerte = fuerte,
        sobreFuerte = if (fuerte.luminance() > 0.30f) Color.Black else Color.White
    )
}

/** All existing screen colors are resolved through the same neutral/accent system. */
fun colorDeTema(color: Color, oscuro: Boolean, uso: UsoColor = UsoColor.TEXTO): Color {
    if (color == Color.Unspecified || color.alpha == 0f) return color
    val maximo = maxOf(color.red, color.green, color.blue)
    val minimo = minOf(color.red, color.green, color.blue)
    val neutro = maximo - minimo < 0.035f || (maximo > 0f && (maximo - minimo) / maximo < 0.18f)
    if (uso == UsoColor.FONDO) {
        if (!neutro && maximo < 0.52f) return paletaAcento(color, oscuro).superficie.copy(alpha = color.alpha)
        if (oscuro) return color
        if (maximo == 0f || color.copy(alpha = 1f) == Color(0xFF121212)) return Color.White.copy(alpha = color.alpha)
        if (neutro && maximo < 0.5f) {
            val superficie = when {
                maximo < 0.15f -> Color(0xFFE9EDF2)
                maximo < 0.22f -> Color(0xFFDDE3EB)
                else -> Color(0xFFCDD5DF)
            }
            return superficie.copy(alpha = color.alpha)
        }
        return color
    }
    if (!neutro) return paletaAcento(color, oscuro).tinta.copy(alpha = color.alpha)
    // Black stays black on the existing bright action buttons.
    if (uso == UsoColor.TEXTO && maximo == 0f) return color
    if (!oscuro) {
        val tinta = when {
            uso == UsoColor.BORDE -> Color(0xFF707D8D)
            maximo > 0.92f -> Color(0xFF18212B)
            else -> Color(0xFF414E5E)
        }
        return tinta.copy(alpha = color.alpha)
    }
    val fondo = if (uso == UsoColor.BORDE) Colores.GrisMedio else Colores.GrisDeshabilitado
    val contrasteMinimo = if (uso == UsoColor.BORDE) 3f else 4.5f
    var tinta = color
    repeat(48) {
        if ((tinta.luminance() + 0.05f) / (fondo.luminance() + 0.05f) >= contrasteMinimo) return tinta
        tinta = Color(tinta.red + (1f - tinta.red) * 0.06f,
            tinta.green + (1f - tinta.green) * 0.06f,
            tinta.blue + (1f - tinta.blue) * 0.06f, color.alpha)
    }
    return tinta
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
    seleccion = paletaAcento(Colores.VerdePrimario, false).fuerte, sobreSeleccion = Color.White,
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
        primary = paletaAcento(Colores.VerdePrimario, false).fuerte, onPrimary = Color.White,
        primaryContainer = Color(0xFFBFE3CD), onPrimaryContainer = Color(0xFF124E2D),
        secondary = paletaAcento(Colores.VerdePrimario, false).fuerte, onSecondary = Color.White,
        background = Color.White, onBackground = Color(0xFF1B1B1F),
        surface = Color(0xFFE9EDF2), onSurface = Color(0xFF1B1B1F),
        surfaceVariant = Color(0xFFDDE3EB), onSurfaceVariant = Color(0xFF414E5E),
        surfaceContainer = Color(0xFFE9EDF2), surfaceContainerLow = Color(0xFFF3F5F8),
        surfaceContainerHigh = Color(0xFFDDE3EB), surfaceContainerHighest = Color(0xFFCDD5DF),
        outline = Color(0xFF77818E), error = Color(0xFFB3261E),
        errorContainer = Color(0xFFF3C3C9), onErrorContainer = Color(0xFF801C2D)
    )
    CompositionLocalProvider(LocalTemaOscuro provides oscuro) {
        MaterialTheme(colorScheme = colores, content = content)
    }
}
