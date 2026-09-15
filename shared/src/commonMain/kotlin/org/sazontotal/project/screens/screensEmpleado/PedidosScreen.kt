package org.sazontotal.project.screens.screensEmpleado

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
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
import org.sazontotal.project.components.PedidoCard
import org.sazontotal.project.components.StatCardEmpleado
import org.sazontotal.project.screens.DashboardEmpleado
import org.sazontotal.project.enums.EstadoPedido

@Composable
fun PedidosScreen(
    onNuevoPedidoClick: () -> Unit = {},
    onCarritoClick: () -> Unit = {},
    onAsistenteClick: () -> Unit = {}
){

    var estado by remember {
        mutableStateOf(EstadoPedido.PENDIENTE)
    }
    var estadoMesa7 by remember {
        mutableStateOf(EstadoPedido.LISTO)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000))
    ) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 20.dp, end = 20.dp, top = 60.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ){
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Column {
                Text(
                    text = "Cocina · Turno tarde",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Hola Ricardo · 12 pedidos hoy",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF8E8E93)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2C2C2E)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "RS",
                        color = Color(0xFF7DD3FC),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ){
            StatCardEmpleado(
                textoSuperior = "2",
                textoInferior = "Pendientes",
                colorSuperior = Color(0xFFFF9F0A),
                modifier = Modifier.weight(1f)
            )
            StatCardEmpleado(
                textoSuperior = "1",
                textoInferior = "En prep.",
                colorSuperior = Color(0xFFB39DFF),
                modifier = Modifier.weight(1f)
            )
            StatCardEmpleado(
                textoSuperior = "9",
                textoInferior = "Completado",
                colorSuperior = Color(0xFF30D158),
                modifier = Modifier.weight(1f)
            )
        }


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF2B2113))
                            .border(1.dp, Color(0xFF8A6A2F), RoundedCornerShape(16.dp))
                            .clickable { onCarritoClick() }
                            .padding(horizontal = 12.dp, vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Ir al carrito",
                                tint = Color(0xFFFFC266),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Carrito",
                                color = Color(0xFFFFD791),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .offset(x = (-12).dp, y = (-8).dp)
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFF9F0A)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "2",
                            color = Color.Black,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF2A2350))
                        .border(1.dp, Color(0xFF6B5FC7), RoundedCornerShape(16.dp))
                        .clickable { onAsistenteClick() }
                        .padding(horizontal = 12.dp, vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Ir al asistente",
                            tint = Color(0xFFB39DFF),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Asistente IA",
                            color = Color(0xFFD6C9FF),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF2C2C2E))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Todos",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, Color(0xFF2C2C2E), RoundedCornerShape(20.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Pendientes",
                    color = Color(0xFF8E8E93),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, Color(0xFF2C2C2E), RoundedCornerShape(20.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Listos",
                    color = Color(0xFF8E8E93),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = true)
                .clip(RoundedCornerShape(16.dp))
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PedidoCard(
                numeroMesa = "4",
                esUrgente = true,
                tiempo = "12 min",
                nota = "sin cebolla",
                estado = estado,
                onCambiarEstado = {
                    estado = when (estado) {
                        EstadoPedido.PENDIENTE -> EstadoPedido.LISTO
                        EstadoPedido.LISTO -> EstadoPedido.LISTO
                    }
                }
            )

            PedidoCard(
                numeroMesa = "4",
                esUrgente = true,
                tiempo = "12 min",
                nota = "sin cebolla",
                estado = estado,
                onCambiarEstado = {
                    estado = when (estado) {
                        EstadoPedido.PENDIENTE -> EstadoPedido.LISTO
                        EstadoPedido.LISTO -> EstadoPedido.LISTO
                    }
                }
            )
            PedidoCard(
                numeroMesa = "4",
                esUrgente = true,
                tiempo = "12 min",
                nota = "sin cebolla",
                estado = estado,
                onCambiarEstado = {
                    estado = when (estado) {
                        EstadoPedido.PENDIENTE -> EstadoPedido.LISTO
                        EstadoPedido.LISTO -> EstadoPedido.LISTO
                    }
                }
            )

            PedidoCard(
                numeroMesa = "7",
                esUrgente = false,
                tiempo = "6 min",
                nota = "",
                estado = estadoMesa7,
                onCambiarEstado = {
                    estadoMesa7 = when (estadoMesa7) {
                        EstadoPedido.PENDIENTE -> EstadoPedido.LISTO
                        EstadoPedido.LISTO -> EstadoPedido.LISTO
                    }
                }
            )

            Spacer(modifier = Modifier.height(72.dp))
        }
    }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 16.dp)
                .size(52.dp)
                .clip(CircleShape)
                .background(Color(0xFF5DBF3E))
                .clickable { onNuevoPedidoClick() },
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


@Preview
@Composable
fun PedidosPreview(){
    DashboardEmpleado()
}
