package org.sazontotal.project.screens.screensAdmin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.sazontotal.project.components.BuscadorTextField
import org.sazontotal.project.components.FiltroButton
import org.sazontotal.project.components.StatCardCentrado
import org.sazontotal.project.data.db.PedidoEntity
import org.sazontotal.project.data.repository.SazonRepository
import org.sazontotal.project.theme.Colores
import org.sazontotal.project.utils.moneda
import org.sazontotal.project.utils.textoHora
import org.sazontotal.project.enums.EstadoTodosLosPedidos

@Composable
fun TodosLosPedidosScreen(
    backTodosLosPedidos: () -> Unit,
    repositorio: SazonRepository? = null
){

    var textoBusqueda by remember { mutableStateOf("") }

    val pedidosEnVivo = repositorio?.observarPedidos()
    val pedidos = pedidosEnVivo?.collectAsState(initial = emptyList())?.value ?: emptyList()

    // solo para poder poner el nombre del mesero en cada card
    val empleadosEnVivo = repositorio?.observarEmpleados()
    val empleados = empleadosEnVivo?.collectAsState(initial = emptyList())?.value ?: emptyList()

    var filtroEstado by remember { mutableStateOf("Todos") }
    val enCurso = pedidos.count { it.estado == "PENDIENTE" || it.estado == "LISTO" }
    val entregados = pedidos.count { it.estado == "ENTREGADO" }
    val anulados = pedidos.count { it.estado == "ANULADO" }
    val scope = rememberCoroutineScope()

    val filtrados = pedidos.filter { pedido ->
        val coincideTexto = textoBusqueda.isBlank() ||
            pedido.mesa.contains(textoBusqueda, ignoreCase = true) ||
            pedido.meseroID.contains(textoBusqueda, ignoreCase = true)
        val coincideFiltro = filtroEstado == "Todos" ||
            (filtroEstado == "Pendientes" && pedido.estado == "PENDIENTE") ||
            (filtroEstado == "Listos" && pedido.estado == "LISTO") ||
            (filtroEstado == "Entregados" && pedido.estado == "ENTREGADO") ||
            (filtroEstado == "Anulados" && pedido.estado == "ANULADO")
        coincideTexto && coincideFiltro
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 20.dp, end = 20.dp, top = 60.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        )
        {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(
                    onClick = { backTodosLosPedidos() },
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
                    text = "Todos los pedidos",
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
            ){
                StatCardCentrado(
                    textoSuperior = "${pedidos.size}",
                    textoInferior = "Hoy",
                    colorSuperior = Color.White,
                    modifier = Modifier.weight(1f)
                )
                StatCardCentrado(
                    textoSuperior = "$enCurso",
                    textoInferior = "En Curso",
                    colorSuperior = Colores.Naranja,
                    modifier = Modifier.weight(1f)
                )
                StatCardCentrado(
                    textoSuperior = "$entregados",
                    textoInferior = "Entregado",
                    colorSuperior = Color.Green,
                    modifier = Modifier.weight(1f)
                )
                StatCardCentrado(
                    textoSuperior = "$anulados",
                    textoInferior = "Anulado",
                    colorSuperior = Color.Red,
                    modifier = Modifier.weight(1f)
                )
            }

            BuscadorTextField(
                nombreBuscado = textoBusqueda,
                onNombreChanged = { nuevoTexto ->
                    textoBusqueda = nuevoTexto },
                texto = "Buscar por mesa o mesero"
            )


            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                FiltroButton(
                    texto = "Todos",
                    activo = filtroEstado == "Todos",
                    onClick = { filtroEstado = "Todos" }
                )
                FiltroButton(
                    texto = "Pendientes",
                    activo = filtroEstado == "Pendientes",
                    onClick = { filtroEstado = "Pendientes" }
                )
                FiltroButton(
                    texto = "Listos",
                    activo = filtroEstado == "Listos",
                    onClick = { filtroEstado = "Listos" }
                )
                FiltroButton(
                    texto = "Entregados",
                    activo = filtroEstado == "Entregados",
                    onClick = { filtroEstado = "Entregados" }
                )
                FiltroButton(
                    texto = "Anulados",
                    activo = filtroEstado == "Anulados",
                    onClick = { filtroEstado = "Anulados" }
                )
            }

            // lista con scroll: un card por cada pedido del filtro.
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                filtrados.forEach { pedido ->
                    PedidoEnAdmin(
                        pedido = pedido,
                        repositorio = repositorio,
                        nombreMesero = empleados.find { it.id == pedido.meseroID }?.nombre ?: pedido.meseroID,
                        onAnular = { id ->
                            scope.launch {
                                repositorio?.cambiarEstadoPedido(id, "ANULADO")
                            }
                        }
                    )
                }
            }



        }
    }
}
@Composable
private fun PedidoEnAdmin(
    pedido: PedidoEntity,
    repositorio: SazonRepository?,
    nombreMesero: String,
    onAnular: (Long) -> Unit
) {
    val itemsEnVivo = repositorio?.observarItems(pedido.id)
    val items = itemsEnVivo?.collectAsState(initial = emptyList())?.value ?: emptyList()

    // LISTO sigue en curso hasta que alguien lo entregue (eso viene despues)
    val insignia = when (pedido.estado) {
        "ENTREGADO" -> EstadoTodosLosPedidos.ENTREGADO
        "ANULADO" -> EstadoTodosLosPedidos.ANULADO
        else -> EstadoTodosLosPedidos.PREPARANDO
    }

    CardPedido(
        estadoTodosLosPedidos = insignia,
        titulo = "Mesa ${pedido.mesa} • #${pedido.id}",
        detalle = "Mesero: $nombreMesero • ${items.size} items",
        hora = textoHora(pedido.fecha),
        total = moneda(pedido.total),
        // solo se puede anular lo que aun no se entrego ni se anulo.
        sePuedeAnular = pedido.estado == "PENDIENTE" || pedido.estado == "LISTO",
        onAnular = { onAnular(pedido.id) }
    )
}

@Composable
private fun CardPedido(
    estadoTodosLosPedidos: EstadoTodosLosPedidos,
    titulo: String,
    detalle: String,
    hora: String,
    total: String,
    sePuedeAnular: Boolean = false,
    onAnular: () -> Unit = {}
) {
    val (textoEstado, colorFondo, colorTexto) = when (estadoTodosLosPedidos) {
        EstadoTodosLosPedidos.PREPARANDO -> Triple(
            "Preparando",
            Color(0xFF3A2600),
            Color(0xFFFFA500)
        )

        EstadoTodosLosPedidos.ENTREGADO -> Triple(
            "Entregado",
            Color(0xFF063D20),
            Color(0xFF00C853)
        )

        EstadoTodosLosPedidos.ANULADO -> Triple(
            "Anulado",
            Color(0xFF4A0D0D),
            Color(0xFFFF3B30)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Colores.Superficie,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(
                start = 16.dp,
                end = 16.dp,
                top = 12.dp,
                bottom = 12.dp
            ),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = titulo,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = textoEstado,
                color = colorTexto,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .background(
                        color = colorFondo,
                        shape = RoundedCornerShape(50)
                    )
                    .padding(
                        horizontal = 12.dp,
                        vertical = 5.dp
                    )
            )
        }

        Text(
            text = detalle,
            color = Color.Gray,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = hora,
                color = Color.Gray,
                fontSize = 15.sp,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = total,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // solo los pedidos en curso se pueden anular.
        if (sePuedeAnular) {
            Text(
                text = "Anular pedido",
                color = Colores.Rojo,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAnular() }
                    .padding(vertical = 4.dp)
            )
        }
    }
}

@Composable
@Preview
fun TodosLosPedidosScreenPreview(){
    TodosLosPedidosScreen(
        backTodosLosPedidos = {}
    )
}