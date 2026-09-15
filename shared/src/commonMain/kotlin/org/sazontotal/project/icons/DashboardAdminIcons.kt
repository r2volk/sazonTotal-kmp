package org.sazontotal.project.icons

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
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF0B3820)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.People,
            contentDescription = null,
            tint = Color(0xFF00E676),
            modifier = Modifier.size(26.dp)
        )
    }
}

@Composable
fun PedidosIcon() {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF092D40)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.Assignment,
            contentDescription = null,
            tint = Color(0xFF29B6F6),
            modifier = Modifier.size(26.dp)
        )
    }
}

@Composable
fun ReportesIcon() {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF3D2905)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.BarChart,
            contentDescription = null,
            tint = Color(0xFFFFA000),
            modifier = Modifier.size(26.dp)
        )
    }
}

@Composable
fun MenuIcon() {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF291B3D)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Restaurant,
            contentDescription = null,
            tint = Color(0xFFB388FF),
            modifier = Modifier.size(26.dp)
        )
    }
}

