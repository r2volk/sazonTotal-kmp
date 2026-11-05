package org.sazontotal.project.icons

import org.sazontotal.project.theme.LocalTemaOscuro
import org.sazontotal.project.theme.UsoColor
import org.sazontotal.project.theme.colorDeTema
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.sazontotal.project.theme.Colores

@Composable
fun LogoutButton(
    onLogout: () -> Unit = {},
    size: Dp = 44.dp,
    modifier: Modifier = Modifier
) {
    val temaOscuro = LocalTemaOscuro.current
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(colorDeTema(Color(0xFF3A1A1A), temaOscuro, UsoColor.FONDO))
            .clickable { onLogout() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
            contentDescription = "Cerrar sesión",
            tint = colorDeTema(Colores.Rojo, temaOscuro, UsoColor.TEXTO),
            modifier = Modifier.size(size * 0.55f)
        )
    }
}
