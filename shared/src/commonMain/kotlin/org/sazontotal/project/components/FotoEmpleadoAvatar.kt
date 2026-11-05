package org.sazontotal.project.components

import org.sazontotal.project.theme.LocalTemaOscuro
import org.sazontotal.project.theme.UsoColor
import org.sazontotal.project.theme.colorDeTema
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import org.sazontotal.project.theme.Colores

@Composable
fun FotoEmpleadoAvatar(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val temaOscuro = LocalTemaOscuro.current
    Box(
        modifier = modifier
            .size(80.dp)
            .drawBehind {
                drawCircle(
                    color = colorDeTema(Colores.GrisDeshabilitado, temaOscuro, UsoColor.TEXTO),
                    style = Stroke(
                        width = 2f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                    )
                )
            }
            .clip(CircleShape)
            .background(colorDeTema(Colores.Superficie.copy(alpha = 0.6f), temaOscuro, UsoColor.FONDO))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.PhotoCamera,
            contentDescription = "Foto del empleado",
            tint = colorDeTema(Colores.TextoTenue, temaOscuro, UsoColor.TEXTO),
            modifier = Modifier.size(24.dp)
        )
    }
}
