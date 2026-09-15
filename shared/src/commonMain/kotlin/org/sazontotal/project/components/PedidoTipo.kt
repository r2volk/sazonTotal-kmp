package org.sazontotal.project.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material3.DividerDefaults.color
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun PedidoTipo(
    icon: ImageVector,
    alimento: String,
    cantidad: Int
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White
        )

        Text(
            text = alimento,
            color = Color.White
        )

        Text(
            text = "x$cantidad",
            color = Color.White
        )
    }
}