package org.sazontotal.project.screens.screensMesero.carrito

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.sazontotal.project.data.db.PedidoEntity
import org.sazontotal.project.data.repository.SazonRepository
import org.sazontotal.project.utils.moneda
import org.sazontotal.project.theme.Colores

@Composable
fun PedidoConcretadoScreen(
    volverDashboard: () -> Unit,
    otroPedido: () -> Unit,
    pedidoId: Long = 0L,
    repositorio: SazonRepository? = null
) {
    // el pedido no cambia, asi que se lee una sola vez al abrir
    var pedido by remember { mutableStateOf<PedidoEntity?>(null) }
    LaunchedEffect(pedidoId) {
        pedido = repositorio?.buscarPedidoPorId(pedidoId)
    }

    val itemsEnVivo = repositorio?.observarItems(pedidoId)
    val items = itemsEnVivo?.collectAsState(initial = emptyList())?.value ?: emptyList()
    // Estructura: la parte de arriba scrollea, los botones quedan FIJOS abajo.
    // Asi nunca se pueden salir de la pantalla, por mas platos que tenga el ticket.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(start = 20.dp, end = 20.dp, top = 60.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF143D2B)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = Colores.VerdePrimario,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Pedido enviado",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Ticket #${pedidoId.toString().padStart(4, '0')} · Mesa ${pedido?.mesa ?: "–"}",
                color = Colores.TextoTenue,
                fontSize = 14.sp
            )
        }

        // progreso del pedido
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // enviado (completado)
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Colores.VerdePrimario),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .padding(horizontal = 6.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Colores.VerdePrimario)
                )
                // preparando (actual)
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF3D2905))
                        .border(2.dp, Colores.Ambar, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Schedule,
                        contentDescription = null,
                        tint = Colores.Ambar,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .padding(horizontal = 6.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Colores.GrisDeshabilitado)
                )
                // listo (pendiente)
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .border(2.dp, Colores.GrisDeshabilitado, CircleShape)
                )
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Enviado",
                    color = Colores.VerdePrimario,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Preparando",
                    color = Colores.Ambar,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Listo",
                    color = Colores.TextoTenue,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // resumen
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Colores.Superficie)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items.forEach { item ->
                ResumenRow(
                    plato = "${item.nombrePlato} x${item.cantidad}",
                    precio = moneda(item.precioUnit * item.cantidad)
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Colores.GrisMedio)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = moneda(pedido?.total ?: 0.0),
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Colores.Superficie)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tiempo estimado",
                color = Colores.TextoTenue,
                fontSize = 15.sp,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "~15 min",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            }
        }

        // Botones: fuera de la columna que scrollea, siempre visibles.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Colores.VerdeBoton)
                .clickable {
                        volverDashboard()
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Volver al dashboard",
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, Colores.GrisMedio, RoundedCornerShape(12.dp))
                .clickable {
                    otroPedido()
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Iniciar otro pedido",
                color = Color(0xFFB0B0B0),
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun ResumenRow(
    plato: String,
    precio: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = plato,
            color = Color(0xFFB0B0B0),
            fontSize = 15.sp,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = precio,
            color = Color.White,
            fontSize = 15.sp
        )
    }
}

@Composable
@Preview
fun PedidoConcretadoPreview(){
    PedidoConcretadoScreen(
        volverDashboard = {},
        otroPedido = {}
    )
}