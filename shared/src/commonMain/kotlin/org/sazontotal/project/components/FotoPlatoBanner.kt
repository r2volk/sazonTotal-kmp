package org.sazontotal.project.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.outlined.EmojiFoodBeverage
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun FotoPlatoBanner(
    onCameraClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF4A2620)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.EmojiFoodBeverage,
            contentDescription = "Foto del plato",
            tint = Color(0xFFE8935A),
            modifier = Modifier.size(40.dp)
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-12).dp, y = (-12).dp)
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFF1C1C1E))
                .clickable { onCameraClick() }
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.PhotoCamera,
                contentDescription = "Cambiar foto",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
