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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
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
import org.sazontotal.project.data.db.EmpleadoEntity
import org.sazontotal.project.data.db.PedidoEntity
import org.sazontotal.project.data.repository.SazonRepository
import org.sazontotal.project.theme.Colores
import org.sazontotal.project.utils.ahoraMillis
import org.sazontotal.project.utils.esHoy
import org.sazontotal.project.utils.textoHora

@Composable
fun CocinaPedidosScreen(
    repositorio: SazonRepository? = null,
    empleado: EmpleadoEntity? = null
) {
    // cocina recibe pedidos, empleados y platos desde los flujos del repositorio.
    val pedidosEnVivo = repositorio?.observarPedidos()
    val pedidos = pedidosEnVivo?.collectAsState(initial = emptyList())?.value ?: emptyList()

    val empleadosEnVivo = repositorio?.observarEmpleados()
    val empleados = empleadosEnVivo?.collectAsState(initial = emptyList())?.value ?: emptyList()
    val platosEnVivo = repositorio?.observarPlatos()
    val platos = platosEnVivo?.collectAsState(initial = emptyList())?.value ?: emptyList()
    val categoriaPorPlato = platos.associate { it.id to it.categoria }

    // en cocina solo importa lo que esta pendiente o listo
    var filtro by remember { mutableStateOf("Por preparar") }
    val ahora = ahoraMillis()
    val porPreparar = pedidos.count { it.estado == "PENDIENTE" }
    val listosHoy = pedidos.count { it.estado == "LISTO" && esHoy(it.fecha, ahora) }
    val scope = rememberCoroutineScope()

    // nombre corto e iniciales del que entro ("Joaquin Gutierrez" -> "Joaquin" / "JG").
    val primerNombre = empleado?.nombre?.split(" ")?.firstOrNull() ?: "María"
    val iniciales = empleado?.nombre
        ?.split(" ")
        ?.mapNotNull { palabra -> palabra.firstOrNull()?.uppercase() }
        ?.take(2)
        ?.joinToString("") ?: "MR"

    val filtrados = pedidos.filter { pedido ->
        (filtro == "Por preparar" && pedido.estado == "PENDIENTE") ||
            (filtro == "Listos" && pedido.estado == "LISTO")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(start = 20.dp, end = 20.dp, top = 60.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
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
                    text = "Hola $primerNombre · Turno tarde",
                    color = Colores.TextoTenue,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal
                )
            }

            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Colores.GrisMedio),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = iniciales,
                    color = Colores.TextoTenue,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCardCentrado(
                textoSuperior = "$porPreparar",
                textoInferior = "Por preparar",
                colorSuperior = Colores.Naranja,
                modifier = Modifier.weight(1f)
            )
            StatCardCentrado(
                textoSuperior = "$listosHoy",
                textoInferior = "Listos hoy",
                colorSuperior = Color(0xFF7DD3FC),
                modifier = Modifier.weight(1f)
            )
            StatCardCentrado(
                textoSuperior = "—",
                textoInferior = "Prom. prep.",
                colorSuperior = Color.White,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FiltroButton(
                texto = "Por preparar · $porPreparar",
                activo = filtro == "Por preparar",
                onClick = { filtro = "Por preparar" },
                textoColor = Colores.Naranja,
                fondoTextoColor = Color(0xFF3A2600)
            )
            FiltroButton(
                texto = "Listos",
                activo = filtro == "Listos",
                onClick = { filtro = "Listos" }
            )
        }

        // lista: un card por cada pedido real del filtro elegido.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            filtrados.forEach { pedido ->
                PedidoEnCocina(
                    pedido = pedido,
                    repositorio = repositorio,
                    nombreMesero = empleados.find { it.id == pedido.meseroID }?.nombre ?: pedido.meseroID,
                    categoriaPorPlato = categoriaPorPlato,
                    onListoClick = { id ->
                        scope.launch {
                            // cocina guarda en room que termino la preparacion.
                            repositorio?.cambiarEstadoPedido(id, "LISTO")
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun PedidoEnCocina(
    pedido: PedidoEntity,
    repositorio: SazonRepository?,
    nombreMesero: String,
    categoriaPorPlato: Map<Long, String>,
    onListoClick: (Long) -> Unit
) {
    val itemsEnVivo = repositorio?.observarItems(pedido.id)
    val items = itemsEnVivo?.collectAsState(initial = emptyList())?.value ?: emptyList()

    // cada renglon se vuelve su dibujito con emoji segun su categoria
    val visuales = items.map { item ->
        val (emoji, fondo) = emojiYFondo(categoriaPorPlato[item.platoId] ?: "Platos")
        CocinaPlatoVisual(
            nombre = item.nombrePlato,
            cantidad = item.cantidad,
            emoji = emoji,
            fondoIcono = fondo
        )
    }

    // la primera nota que haya, si hay
    val nota = items.firstOrNull { it.nota.isNotBlank() }?.nota ?: ""

    CocinaPedidoCard(
        mesa = pedido.mesa,
        numeroPedido = pedido.id.toString().padStart(4, '0'),
        mesero = nombreMesero,
        tiempo = textoHora(pedido.fecha),
        platos = visuales,
        nota = nota,
        onListoClick = { onListoClick(pedido.id) },
        yaListo = pedido.estado == "LISTO"
    )
}

private fun emojiYFondo(categoria: String): Pair<String, Color> = when (categoria) {
    "Bebidas" -> "🥤" to Color(0xFF1E4A3A)
    "Postres" -> "🍰" to Color(0xFF3D1A2E)
    else -> "🍽️" to Color(0xFF3B2A18)
}

@Preview
@Composable
fun CocinaPedidosScreenPreview() {
    CocinaPedidosScreen()
}
