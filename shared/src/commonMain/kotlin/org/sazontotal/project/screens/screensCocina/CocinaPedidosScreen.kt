package org.sazontotal.project.screens.screensCocina

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.sazontotal.project.components.CocinaPedidoCard
import org.sazontotal.project.components.CocinaPlatoVisual
import org.sazontotal.project.components.FiltroButton
import org.sazontotal.project.components.StatCardCentrado

@Composable
fun CocinaPedidosScreen() {
    // SOLO VISUAL: el filtro solo cambia el pill activo, no filtra nada.
    var filtro by remember { mutableStateOf("Por preparar") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(start = 20.dp, end = 20.dp, top = 60.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // HEADER
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Cocina",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Hola María · Turno tarde",
                    color = Color(0xFF8E8E93),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier.size(50.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notificaciones",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = (-8).dp, y = (8).dp)
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFF9F0A))
                    )
                }

                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2C2C2E)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "MR",
                        color = Color(0xFF8E8E93),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // STATS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCardCentrado(
                textoSuperior = "3",
                textoInferior = "Por preparar",
                colorSuperior = Color(0xFFFF9F0A),
                modifier = Modifier.weight(1f)
            )
            StatCardCentrado(
                textoSuperior = "11",
                textoInferior = "Listos hoy",
                colorSuperior = Color(0xFF7DD3FC),
                modifier = Modifier.weight(1f)
            )
            StatCardCentrado(
                textoSuperior = "9m",
                textoInferior = "Prom. prep.",
                colorSuperior = Color.White,
                modifier = Modifier.weight(1f)
            )
        }

        // FILTROS
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FiltroButton(
                texto = "Por preparar · 3",
                activo = filtro == "Por preparar",
                onClick = { filtro = "Por preparar" },
                textoColor = Color(0xFFFF9F0A),
                fondoTextoColor = Color(0xFF3A2600)
            )
            FiltroButton(
                texto = "Listos",
                activo = filtro == "Listos",
                onClick = { filtro = "Listos" }
            )
        }

        // LISTA (solo visual, datos quemados como la imagen)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CocinaPedidoCard(
                mesa = "4",
                numeroPedido = "0234",
                mesero = "José P.",
                tiempo = "14 min",
                esUrgente = true,
                platos = listOf(
                    CocinaPlatoVisual(
                        nombre = "Ceviche clásico",
                        cantidad = 1,
                        emoji = "🐟",
                        fondoIcono = Color(0xFF1E3A5F)
                    ),
                    CocinaPlatoVisual(
                        nombre = "Lomo saltado",
                        cantidad = 2,
                        emoji = "🥩",
                        fondoIcono = Color(0xFF3B2A18)
                    ),
                    CocinaPlatoVisual(
                        nombre = "Pisco sour",
                        cantidad = 1,
                        emoji = "🍸",
                        fondoIcono = Color(0xFF1E4A3A)
                    )
                ),
                nota = "Lomo sin cebolla",
                onListoClick = {}
            )

            CocinaPedidoCard(
                mesa = "7",
                numeroPedido = "0235",
                mesero = "Luis T.",
                tiempo = "6 min",
                esUrgente = false,
                platos = listOf(
                    CocinaPlatoVisual(
                        nombre = "Ají de gallina",
                        cantidad = 2,
                        emoji = "🍛",
                        fondoIcono = Color(0xFF3B2A18)
                    )
                ),
                nota = "",
                onListoClick = {}
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Preview
@Composable
fun CocinaPedidosScreenPreview() {
    CocinaPedidosScreen()
}
