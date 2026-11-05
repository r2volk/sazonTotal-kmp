package org.sazontotal.project.screens.screensMesero

import org.sazontotal.project.theme.LocalTemaOscuro
import org.sazontotal.project.theme.UsoColor
import org.sazontotal.project.theme.colorDeTema
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import org.sazontotal.project.data.CarritoCompra
import org.sazontotal.project.data.esDelivery
import org.sazontotal.project.data.db.PedidoEntity
import org.sazontotal.project.data.repository.SazonRepository
import org.sazontotal.project.theme.Colores
import org.sazontotal.project.utils.ahoraMillis
import org.sazontotal.project.utils.esHoy
import org.sazontotal.project.utils.textoHora
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.sazontotal.project.components.FiltroButton
import org.sazontotal.project.components.PedidoCard
import org.sazontotal.project.components.StatCardCentrado
import org.sazontotal.project.data.db.EmpleadoEntity
import org.sazontotal.project.screens.DashboardMesero
import org.sazontotal.project.enums.EstadoPedido

@Composable
fun MeseroPedidosScreen(
    onCarritoClick: () -> Unit,
    onAsistenteClick: () -> Unit,
    onRepartoClick: () -> Unit,
    repositorio: SazonRepository? = null,
    empleado: EmpleadoEntity? = null,
    carrito: CarritoCompra? = null
){
    val temaOscuro = LocalTemaOscuro.current

    // room entrega pedidos actualizados; despues se filtran por el mesero conectado.
    val pedidosEnVivo = repositorio?.observarPedidos()
    val pedidos = pedidosEnVivo?.collectAsState(initial = emptyList())?.value ?: emptyList()

    // nombre corto del que entro ("Ricardo Sanchez" -> "Ricardo").
    val primerNombre = empleado?.nombre?.split(" ")?.firstOrNull() ?: "Ricardo"

    // iniciales para el avatar ("Ricardo Sanchez" -> "RS").
    val iniciales = empleado?.nombre
        ?.split(" ")
        ?.mapNotNull { palabra -> palabra.firstOrNull()?.uppercase() }
        ?.take(2)
        ?.joinToString("") ?: "RS"

    var filtroEstado by remember { mutableStateOf("Todos") }

    // "Mis pedidos": solo los de este mesero; sin sesion (preview) se muestran todos.
    val idMesero = empleado?.id
    val misPedidos = if (idMesero == null) pedidos else pedidos.filter { it.meseroID == idMesero }
    val ahora = ahoraMillis()
    val pedidosHoy = misPedidos.count { esHoy(it.fecha, ahora) }

    // lo ENTREGADO sale de esta lista: ya se sirvio y vive en el historial.
    val enCurso = misPedidos.filter { it.estado != "ENTREGADO" }

    val pendientes = enCurso.count { it.estado == "PENDIENTE" }
    val listos = enCurso.count { it.estado == "LISTO" }
    val entregados = misPedidos.count { it.estado == "ENTREGADO" }
    val scope = rememberCoroutineScope()

    val filtrados = enCurso.filter { pedido ->
        filtroEstado == "Todos" ||
            (filtroEstado == "Pendientes" && pedido.estado == "PENDIENTE") ||
            (filtroEstado == "Listos" && pedido.estado == "LISTO")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorDeTema(Color.Black, temaOscuro, UsoColor.FONDO))
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
                    text = "Mis pedidos",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorDeTema(Color.White, temaOscuro, UsoColor.TEXTO)
                )
                Text(
                    text = "Hola $primerNombre · $pedidosHoy pedidos hoy",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = colorDeTema(Colores.TextoTenue, temaOscuro, UsoColor.TEXTO)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(colorDeTema(Colores.GrisMedio, temaOscuro, UsoColor.FONDO)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = iniciales,
                        color = colorDeTema(Color(0xFF7DD3FC), temaOscuro, UsoColor.TEXTO),
                        fontSize = 18.sp,
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
            StatCardCentrado(
                textoSuperior = "$pendientes",
                textoInferior = "Pendientes",
                colorSuperior = Colores.Naranja,
                modifier = Modifier.weight(1f)
            )
            StatCardCentrado(
                textoSuperior = "$listos",
                textoInferior = "Listos",
                colorSuperior = Color(0xFFB39DFF),
                modifier = Modifier.weight(1f)
            )
            StatCardCentrado(
                textoSuperior = "$entregados",
                textoInferior = "Entregados",
                colorSuperior = Colores.VerdePrimario,
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
                            .background(colorDeTema(Color(0xFF2B2113), temaOscuro, UsoColor.FONDO))
                            .border(1.dp, colorDeTema(Color(0xFF8A6A2F), temaOscuro, UsoColor.BORDE), RoundedCornerShape(16.dp))
                            .clickable { onCarritoClick() }
                            .height(62.dp)
                            .padding(horizontal = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Ir al carrito",
                                tint = colorDeTema(Color(0xFFFFC266), temaOscuro, UsoColor.TEXTO),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Carrito",
                                color = colorDeTema(Color(0xFFFFD791), temaOscuro, UsoColor.TEXTO),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .offset(x = (-12).dp, y = (-8).dp)
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(colorDeTema(Colores.Naranja, temaOscuro, UsoColor.FONDO)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (carrito?.totalItems() ?: 0).toString(),
                            color = colorDeTema(Color.Black, temaOscuro, UsoColor.TEXTO),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(colorDeTema(Color(0xFF2A2350), temaOscuro, UsoColor.FONDO))
                        .border(1.dp, colorDeTema(Color(0xFF6B5FC7), temaOscuro, UsoColor.BORDE), RoundedCornerShape(16.dp))
                        .clickable { onAsistenteClick() }
                        .height(62.dp)
                        .padding(horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Ir al asistente",
                            tint = colorDeTema(Color(0xFFB39DFF), temaOscuro, UsoColor.TEXTO),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Asistente IA",
                            color = colorDeTema(Color(0xFFD6C9FF), temaOscuro, UsoColor.TEXTO),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // tarjeta de acceso al modulo de reparto (misma fila, tema verde).
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(colorDeTema(Color(0xFF0E2B18), temaOscuro, UsoColor.FONDO))
                        .border(1.dp, colorDeTema(Color(0xFF2F8A4F), temaOscuro, UsoColor.BORDE), RoundedCornerShape(16.dp))
                        .clickable { onRepartoClick() }
                        .height(62.dp)
                        .padding(horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.TwoWheeler,
                            contentDescription = "Ir al reparto",
                            tint = colorDeTema(Color(0xFF7BE382), temaOscuro, UsoColor.TEXTO),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Reparto",
                            color = colorDeTema(Color(0xFF7BE382), temaOscuro, UsoColor.TEXTO),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

        Row(
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
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = true)
                .clip(RoundedCornerShape(16.dp))
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (filtrados.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Sin pedidos por aquí",
                        color = colorDeTema(Color.White, temaOscuro, UsoColor.TEXTO),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Tus pedidos servidos aparecen en el Historial",
                        color = colorDeTema(Colores.TextoTenue, temaOscuro, UsoColor.TEXTO),
                        fontSize = 13.sp
                    )
                }
            }

            filtrados.forEach { pedido ->
                PedidoEnCocina(
                    pedido = pedido,
                    repositorio = repositorio,
                    // al marcar ENTREGADO el pedido sale de aqui y entra al historial.
                    onMarcarListo = { id ->
                        scope.launch {
                            // al servirlo, se guarda el estado entregado en room.
                            val siguiente = if (esDelivery(pedido.mesa) && pedido.estado == "LISTO") "EN_CAMINO" else "ENTREGADO"
                            repositorio?.cambiarEstadoPedido(id, siguiente)
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(72.dp))
        }
    }
    }
}


@Composable
private fun PedidoEnCocina(
    pedido: PedidoEntity,
    repositorio: SazonRepository?,
    onMarcarListo: (Long) -> Unit
) {
    val temaOscuro = LocalTemaOscuro.current
    val itemsEnVivo = repositorio?.observarItems(pedido.id)
    val items = itemsEnVivo?.collectAsState(initial = emptyList())?.value ?: emptyList()

    val lineas = items.map { "${it.nombrePlato} x${it.cantidad}" }
    val nota = items.firstOrNull { it.nota.isNotBlank() }?.nota ?: ""

    // el estado de la base se vuelve el momento que muestra el card
    val estado = when (pedido.estado) {
        "PENDIENTE" -> EstadoPedido.PENDIENTE
        "LISTO" -> EstadoPedido.LISTO
        "EN_CAMINO" -> EstadoPedido.EN_CAMINO
        "ENTREGADO" -> EstadoPedido.ENTREGADO
        else -> EstadoPedido.ANULADO
    }

    PedidoCard(
        numeroMesa = pedido.mesa,
        tiempo = textoHora(pedido.fecha),
        nota = nota,
        estado = estado,
        lineas = lineas,
        onCambiarEstado = { onMarcarListo(pedido.id) }
    )
}


@Preview
@Composable
fun PedidosPreview(){
    val temaOscuro = LocalTemaOscuro.current
    DashboardMesero(
        onLogoutClick = {},
        onCarritoClick = {},
        onAsistenteClick = {}
    )
}
