package org.sazontotal.project.screens.screensEmpleado

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import org.sazontotal.project.components.PedidoCard
import org.sazontotal.project.components.StatCardEmpleado
import org.sazontotal.project.screens.DashboardEmpleado
import org.sazontotal.project.enums.EstadoPedido

@Composable
fun PedidosScreen(){

    var estado by remember {
        mutableStateOf(EstadoPedido.PENDIENTE)
    }
    var estadoMesa7 by remember {
        mutableStateOf(EstadoPedido.PREPARANDO)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 60.dp),
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
                    modifier = Modifier.size(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = Color(0xFFC7C7CC),
                        modifier = Modifier.size(24.dp)
                    )
                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .align(Alignment.TopEnd)
                            .offset(x = (-6).dp, y = (6).dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFF9F0A))
                    )
                }

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
                        EstadoPedido.PENDIENTE -> EstadoPedido.PREPARANDO
                        EstadoPedido.PREPARANDO -> EstadoPedido.LISTO
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
                        EstadoPedido.PENDIENTE -> EstadoPedido.PREPARANDO
                        EstadoPedido.PREPARANDO -> EstadoPedido.LISTO
                        EstadoPedido.LISTO -> EstadoPedido.LISTO
                    }
                }
            )

            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}


@Preview
@Composable
fun PedidosPreview(){
    DashboardEmpleado()
}
