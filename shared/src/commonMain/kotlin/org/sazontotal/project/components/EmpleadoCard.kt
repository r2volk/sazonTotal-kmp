package org.sazontotal.project.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EmpleadoCard(
    nombre: String,
    imagen: Painter? = null,
    isActive: Boolean,
    trabajo: String,
    id: String,
    modifier: Modifier = Modifier
) {
    val isCocina = trabajo.equals("Cocina", ignoreCase = true)
    val badgeBg = when {
        !isActive -> Color(0xFF2C2C2E)
        isCocina -> Color(0xFF1A365D)
        else -> Color(0xFF143D2B)
    }
    val badgeText = when {
        !isActive -> Color(0xFF636366)
        isCocina -> Color(0xFF64A8F5)
        else -> Color(0xFF30D158)
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1C1C1E))
            .padding(
                horizontal = 14.dp,
                vertical = 12.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Imagen / placeholder
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFF2C2C2E)),
            contentAlignment = Alignment.Center
        ) {
            if (imagen != null) {
                androidx.compose.foundation.Image(
                    painter = imagen,
                    contentDescription = nombre,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )
            } else {
                Text(
                    text = "ツ",
                    color = Color(0xFF9A9A9A),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.size(12.dp))

        // Información
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = nombre,
                color = if (isActive) {
                    Color(0xFFE8E8E8)
                } else {
                    Color(0xFF777777)
                },
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(badgeBg)
                        .padding(
                            horizontal = 8.dp,
                            vertical = 2.dp
                        )
                ) {
                    Text(
                        text = trabajo,
                        color = badgeText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.size(6.dp))

                Text(
                    text = "ID: $id",
                    color = if (isActive) {
                        Color(0xFF5F5F5F)
                    } else {
                        Color(0xFF444444)
                    },
                    fontSize = 12.sp
                )
            }
        }

        // Switch
        Switch(
            checked = isActive,
            onCheckedChange = null,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xFF000000),
                checkedTrackColor = Color(0xFF30D158),

                uncheckedThumbColor = Color(0xFF000000),
                uncheckedTrackColor = Color(0xFF3A3A3C),

                checkedBorderColor = Color.Transparent,
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}

@Preview
@Composable
fun EmpleadoCardPreview(){
    EmpleadoCard(
        nombre = "Jose Paredes",
        trabajo = "Mesero",
        isActive = true,
        id = "1212"

    )
}