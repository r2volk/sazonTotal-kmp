package org.sazontotal.project.screens.screensMesero

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
import org.sazontotal.project.theme.Colores
import org.sazontotal.project.utils.ahoraMillis
import org.sazontotal.project.utils.moneda
import org.sazontotal.project.utils.partesFecha
import org.sazontotal.project.utils.textoHora

@Composable
@Preview
fun MeseroHistorialScreen(
    repositorio: SazonRepository? = null
) {
    var textoBusqueda by remember { mutableStateOf("") }

    val pedidosEnVivo = repositorio?.observarPedidos()
    val pedidos = pedidosEnVivo?.collectAsState(initial = emptyList())?.value ?: emptyList()

    // aca solo los ya completados, y que pasen buscador y filtro de tiempo.
    // "hoy" compara dia/mes/ano; semana y mes miden hacia atras.
    var filtroTiempo by remember { mutableStateOf("Hoy") }
    val ahora = remember { ahoraMillis() }
    val DIA = 86_400_000L
    val completados = pedidos.filter { pedido ->
        pedido.estado == "ENTREGADO" &&
            pedido.fecha > 0 &&
            (textoBusqueda.isBlank() || pedido.mesa.contains(textoBusqueda, ignoreCase = true)) &&
            when (filtroTiempo) {
                "Semana" -> ahora - pedido.fecha < 7 * DIA
                "Mes" -> ahora - pedido.fecha < 30 * DIA
                else -> partesFecha(pedido.fecha) == partesFecha(ahora)
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(start = 20.dp, end = 20.dp, top = 60.dp, bottom = 12.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // titulo
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "Historial",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Pedidos completados",
                color = Colores.TextoTenue,
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
                activo = filtroTiempo == "Hoy",
                onClick = { filtroTiempo = "Hoy" }
            )
            FiltroButton(
                texto = "Semana",
                activo = filtroTiempo == "Semana",
                onClick = { filtroTiempo = "Semana" }
            )
            FiltroButton(
                texto = "Mes",
                activo = filtroTiempo == "Mes",
                onClick = { filtroTiempo = "Mes" }
            )
        }

        Text(
            text = "COMPLETADOS · ${completados.size} PEDIDOS",
            color = Colores.TextoTenue,
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
    val itemsEnVivo = repositorio?.observarItems(pedido.id)
    val items = itemsEnVivo?.collectAsState(initial = emptyList())?.value ?: emptyList()

    val detalle = items.joinToString(", ") { "${it.nombrePlato} x${it.cantidad}" }

    HistorialCard(
        mesa = "Mesa ${pedido.mesa}",
        hora = textoHora(pedido.fecha),
        detalle = detalle,
        precio = moneda(pedido.total)
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
            .background(Colores.Superficie)
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
                tint = Colores.VerdePrimario,
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
                    color = Colores.TextoTenue,
                    fontSize = 13.sp
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = detalle,
                    color = Colores.TextoTenue,
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
