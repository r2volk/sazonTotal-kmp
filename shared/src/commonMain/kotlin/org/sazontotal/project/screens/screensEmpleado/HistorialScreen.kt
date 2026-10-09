package org.sazontotal.project.screens.screensEmpleado

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import org.sazontotal.project.components.BuscadorTextField
import org.sazontotal.project.components.FiltroButton
import org.sazontotal.project.data.db.PedidoEntity
import org.sazontotal.project.data.repository.SazonRepository

@Composable
@Preview
fun HistorialScreen(
    repositorio: SazonRepository? = null
) {
    var textoBusqueda by remember { mutableStateOf("") }

    // 1. Pedimos la lista viva de pedidos a la base.
    val pedidosEnVivo = repositorio?.observarPedidos()

    // 2. La convertimos en lista dibujable (o vacía si no hay base).
    val pedidos = pedidosEnVivo?.collectAsState(initial = emptyList())?.value ?: emptyList()

    // 3. Nos quedamos solo con los entregados (los completados),
    // que además pasen el buscador por mesa.
    val completados = pedidos.filter { pedido ->
        pedido.estado == "ENTREGADO" &&
            (textoBusqueda.isBlank() || pedido.mesa.contains(textoBusqueda, ignoreCase = true))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(start = 20.dp, end = 20.dp, top = 60.dp, bottom = 12.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Título
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "Historial",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Pedidos completados",
                color = Color(0xFF8E8E93),
                fontSize = 14.sp
            )
        }

        BuscadorTextField(
            nombreBuscado = textoBusqueda,
            onNombreChanged = { textoBusqueda = it },
            texto = "Buscar por mesa"
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FiltroButton(
                texto = "Hoy",
                activo = true,
                onClick = {}
            )
            FiltroButton(
                texto = "Semana",
                activo = false,
                onClick = {}
            )
            FiltroButton(
                texto = "Mes",
                activo = false,
                onClick = {}
            )
        }

        Text(
            text = "COMPLETADOS · ${completados.size} PEDIDOS",
            color = Color(0xFF8E8E93),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 1.sp
        )

        completados.forEach { pedido ->
            HistorialDePedido(
                pedido = pedido,
                repositorio = repositorio
            )
        }
    }
}

@Composable
private fun HistorialDePedido(
    pedido: PedidoEntity,
    repositorio: SazonRepository?
) {
    // 1. Pedimos los renglones de ESTE pedido (se buscan por su número).
    val itemsEnVivo = repositorio?.observarItems(pedido.id)

    // 2. Los convertimos en lista dibujable (o vacía).
    val items = itemsEnVivo?.collectAsState(initial = emptyList())?.value ?: emptyList()

    // 3. Armamos el detalle ("Lomo saltado x2, Pisco sour x1").
    val detalle = items.joinToString(", ") { "${it.nombrePlato} x${it.cantidad}" }

    HistorialCard(
        mesa = "Mesa ${pedido.mesa}",
        hora = "hoy",
        detalle = detalle,
        precio = "S/ ${pedido.total}"
    )
}

@Composable
private fun HistorialCard(
    mesa: String,
    hora: String,
    detalle: String,
    precio: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1C1C1E))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color(0xFF143D2B)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = Color(0xFF30D158),
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.size(12.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = mesa,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = hora,
                    color = Color(0xFF8E8E93),
                    fontSize = 13.sp
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = detalle,
                    color = Color(0xFF8E8E93),
                    fontSize = 14.sp,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )
                Text(
                    text = precio,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
