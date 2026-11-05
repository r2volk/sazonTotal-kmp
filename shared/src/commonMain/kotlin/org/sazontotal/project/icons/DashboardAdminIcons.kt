package org.sazontotal.project.icons

import org.sazontotal.project.theme.LocalTemaOscuro
import org.sazontotal.project.theme.UsoColor
import org.sazontotal.project.theme.colorDeTema
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp



@Composable
fun PersonalIcon() {
    val temaOscuro = LocalTemaOscuro.current
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(colorDeTema(Color(0xFF0B3820), temaOscuro, UsoColor.FONDO)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.People,
            contentDescription = null,
            tint = colorDeTema(Color(0xFF00E676), temaOscuro, UsoColor.TEXTO),
            modifier = Modifier.size(26.dp)
        )
    }
}

@Composable
fun PedidosIcon() {
    val temaOscuro = LocalTemaOscuro.current
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(colorDeTema(Color(0xFF092D40), temaOscuro, UsoColor.FONDO)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.Assignment,
            contentDescription = null,
            tint = colorDeTema(Color(0xFF29B6F6), temaOscuro, UsoColor.TEXTO),
            modifier = Modifier.size(26.dp)
        )
    }
}

@Composable
fun ReportesIcon() {
    val temaOscuro = LocalTemaOscuro.current
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(colorDeTema(Color(0xFF3D2905), temaOscuro, UsoColor.FONDO)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.BarChart,
            contentDescription = null,
            tint = colorDeTema(Color(0xFFFFA000), temaOscuro, UsoColor.TEXTO),
            modifier = Modifier.size(26.dp)
        )
    }
}

@Composable
fun MenuIcon() {
    val temaOscuro = LocalTemaOscuro.current
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(colorDeTema(Color(0xFF291B3D), temaOscuro, UsoColor.FONDO)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Restaurant,
            contentDescription = null,
            tint = colorDeTema(Color(0xFFB388FF), temaOscuro, UsoColor.TEXTO),
            modifier = Modifier.size(26.dp)
        )
    }
}

