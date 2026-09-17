package org.sazontotal.project.screens.screensAdmin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.sazontotal.project.components.BuscadorTextField
import org.sazontotal.project.components.FiltroButton
import org.sazontotal.project.components.StatCardCentrado
import org.sazontotal.project.components.SwitchButton

@Composable
@Preview
fun GestionPersonalScreen(){

    var nombreBuscado by remember { mutableStateOf("") }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)

    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 60.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(
                    onClick = { },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Atrás",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Text(
                    text = "Gestionar personal",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCardCentrado(
                    textoSuperior = "14",
                    textoInferior = "Total",
                    colorSuperior = Color.White,
                    modifier = Modifier.weight(1f)
                )
                StatCardCentrado(
                    textoSuperior = "11",
                    textoInferior = "Activos",
                    colorSuperior = Color(0xFF30D158),
                    modifier = Modifier.weight(1f)
                )
                StatCardCentrado(
                    textoSuperior = "3",
                    textoInferior = "Inactivos",
                    colorSuperior = Color(0xFF8E8E93),
                    modifier = Modifier.weight(1f)
                )
            }
            BuscadorTextField(
                nombreBuscado = nombreBuscado,
                onNombreChanged = { nuevoTexto ->
                    nombreBuscado = nuevoTexto },
                texto = "Buscar por nombre"
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                FiltroButton(
                    texto = "Todos",
                    activo = true,
                    onClick = {}
                )
                FiltroButton(
                    texto = "Meseros",
                    activo = false,
                    onClick = {}
                )
                FiltroButton(
                    texto = "Cocina",
                    activo = false,
                    onClick = {}
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                EmpleadoCard(
                    nombre = "Jose Paredes",
                    trabajo = "Mesero",
                    isActive = true,
                    id = "1212"
                )
            }
        }

        // FAB
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 20.dp)
                .size(52.dp)
                .clip(CircleShape)
                .background(Color(0xFF5DBF3E))
                .clickable {  },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "+",
                color = Color.Black,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun EmpleadoCard(
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
        SwitchButton(isActive = isActive)
    }
}
