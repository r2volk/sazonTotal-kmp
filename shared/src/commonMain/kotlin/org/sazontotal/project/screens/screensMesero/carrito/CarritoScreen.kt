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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Chair
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect
import org.sazontotal.project.components.CampoTextoAdmin
import org.sazontotal.project.components.CantidadStepper
import org.sazontotal.project.components.visualDeCategoria
import org.sazontotal.project.data.CarritoCompra
import org.sazontotal.project.data.db.PedidoEntity
import org.sazontotal.project.data.db.PedidoItemEntity
import org.sazontotal.project.data.db.PlatoEntity
import org.sazontotal.project.data.repository.SazonRepository
import org.sazontotal.project.theme.Colores
import org.sazontotal.project.utils.ahoraMillis
import org.sazontotal.project.utils.moneda

@Composable
fun CarritoScreen(
    backCarritoScreen: () -> Unit,
    confirmarCarrito: (Long) -> Unit,
    meseroId: String = "MSR-001",
    repositorio: SazonRepository? = null,
    carrito: CarritoCompra? = null,
    mesa: String = "4",
    onMesaChange: (String) -> Unit = {}
) {
    val scope = rememberCoroutineScope()

    // Observa tambien los platos desactivados para indicar cuando alguno deja
    // de venderse. El carrito conserva su copia aunque el plato sea borrado.
    var catalogo by remember { mutableStateOf<List<PlatoEntity>?>(null) }
    LaunchedEffect(repositorio) {
        catalogo = null
        repositorio?.observarTodosLosPlatos()?.collect { catalogo = it }
    }

    // En Peru los precios del menu ya incluyen IGV; no se agrega otro 18%.
    val total = carrito?.total() ?: 0.0

    // sin nada en el carrito el boton no hace nada
    val hayAlgo = (carrito?.totalItems() ?: 0) > 0

    // se apaga mientras guarda, asi el doble tap no crea dos pedidos
    var guardando by remember { mutableStateOf(false) }
    // Si el guardado falla, se avisa (y nunca se queda en "Enviando...").
    var errorGuardado by remember { mutableStateOf<String?>(null) }

    // al bajar a 0 el card sale solo de la lista
    val platosEnCarrito = carrito?.platosEnCarrito() ?: emptyList()
    val catalogoPorId = catalogo?.associateBy { it.id } ?: emptyMap()
    val todosLosPlatosDisponibles = catalogo != null && platosEnCarrito.all { plato ->
        catalogoPorId[plato.id]?.activo == true
    }
    val mesaValida = mesa.toIntOrNull()?.let { it in 1..15 } == true
    val puedeConfirmar = hayAlgo &&
        carrito != null &&
        repositorio != null &&
        catalogo != null &&
        todosLosPlatosDisponibles &&
        mesaValida &&
        !guardando

    // La mesa se puede cambiar mientras haya productos en el carrito y no se
    // este guardando. El envio valida de forma independiente que sea 1..15.
    var mesaAbierta by remember { mutableStateOf(false) }
    var anclaMesa by remember { mutableStateOf<Rect?>(null) }
    val distanciaMesa = with(LocalDensity.current) { 10.dp.toPx() }
    val selectorActivo = hayAlgo && !guardando

    val mensajeBloqueo = when {
        !hayAlgo -> null
        repositorio == null -> "No se puede enviar el pedido en este momento."
        catalogo == null -> "Cargando el menú..."
        !mesaValida -> "Selecciona una mesa del 1 al 15."
        !todosLosPlatosDisponibles -> "Hay platos que ya no están disponibles. Reduce su cantidad a cero para quitarlos."
        else -> null
    }

    val totalItems = carrito?.totalItems() ?: 0
    val textoItems = if (totalItems == 1) "1 item" else "$totalItems items"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .imePadding()
            .padding(start = 20.dp, end = 20.dp, top = 60.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // header: el titulo acompana la mesa que eligio en el selector de abajo
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(
                onClick = { backCarritoScreen() },
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
                text = "Carrito · Mesa $mesa",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
        }

        // items con scroll
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            platosEnCarrito.forEach { plato ->
                key(plato.id) {
                    val cantidad = carrito?.cantidadDe(plato.id) ?: 0
                    val disponible = catalogo?.any { it.id == plato.id && it.activo } ?: true
                    val visual = visualDeCategoria(plato.categoria)
                    CarritoItemCard(
                        nombre = plato.nombre,
                        precioUnitario = "${moneda(plato.precio)} c/u",
                        icono = visual.icono,
                        fondoImagen = visual.fondo,
                        tintaIcono = visual.tinta,
                        cantidad = cantidad,
                        disponible = disponible,
                        interactivo = !guardando,
                        onMinus = {
                            errorGuardado = null
                            carrito?.restar(plato.id)
                        },
                        onPlus = {
                            errorGuardado = null
                            carrito?.sumar(plato)
                        },
                        nota = carrito?.notaDe(plato.id) ?: "",
                        onNotaChange = {
                            errorGuardado = null
                            carrito?.ponerNota(plato.id, it)
                        }
                    )
                }
            }
            if (!hayAlgo) {
                Text(
                    text = "Carrito vacío: suma platos con + desde el menú",
                    color = Colores.TextoTenue,
                    fontSize = 14.sp
                )
            }

        }

        // bloque inferior: selector de mesa + total, todo en una tarjeta
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF161619))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1F1F23)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Chair,
                        contentDescription = null,
                        tint = Color(0xFFA9D9B3),
                        modifier = Modifier.size(22.dp)
                    )
                }
                Text(
                    text = "Mesa:",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp)
                )
                SelectorDeMesa(
                    mesa = mesa,
                    activo = selectorActivo,
                    abierto = mesaAbierta,
                    onToggle = { mesaAbierta = !mesaAbierta },
                    onAncla = { anclaMesa = it }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Total",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = textoItems,
                        color = Colores.TextoTenue,
                        fontSize = 13.sp
                    )
                }
                Text(
                    text = moneda(total),
                    color = Color.White,
                    fontSize = 27.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Explica por que no se puede enviar o muestra el error del guardado.
        val aviso = errorGuardado ?: mensajeBloqueo
        if (aviso != null) {
            Text(
                text = aviso,
                color = if (errorGuardado != null || mensajeBloqueo != "Cargando el menú...") {
                    Color(0xFFFF453A)
                } else {
                    Colores.TextoTenue
                },
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (puedeConfirmar) Colores.VerdeBoton else Colores.GrisDeshabilitado)
                .clickable(enabled = puedeConfirmar) {
                    val carritoConfirmado = carrito ?: return@clickable
                    val repositorioConfirmado = repositorio ?: return@clickable
                    val renglones = platosEnCarrito.map { plato ->
                        PedidoItemEntity(
                            pedidoId = 0,
                            platoId = plato.id,
                            nombrePlato = plato.nombre,
                            cantidad = carritoConfirmado.cantidadDe(plato.id),
                            nota = carritoConfirmado.notaDe(plato.id),
                            precioUnit = plato.precio
                        )
                    }
                    guardando = true
                    errorGuardado = null
                    scope.launch {
                        try {
                            val cabecera = PedidoEntity(
                                mesa = mesa,
                                meseroID = meseroId,
                                estado = "PENDIENTE",
                                total = total,
                                fecha = ahoraMillis()
                            )
                            val numeroPedido = repositorioConfirmado.crearPedidoConItems(cabecera, renglones)
                            check(numeroPedido > 0L) { "La base de datos no devolvió un número de pedido" }
                            // Solo vaciar despues de que el pedido exista.
                            carritoConfirmado.limpiar()
                            confirmarCarrito(numeroPedido)
                        } catch (e: Exception) {
                            // si algo falla, el boton se rehabilita y se avisa:
                            // nunca queda en "Enviando..." para siempre.
                            errorGuardado = "No se pudo enviar el pedido: ${e.message ?: "intenta de nuevo"}"
                        } finally {
                            guardando = false
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (guardando) "Enviando..." else "Confirmar y enviar a cocina",
                color = if (puedeConfirmar) Color.Black else Colores.TextoTenue,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }

    // el dropdown se abre por encima de todo, pegado al borde derecho del
    // boton. afuera, al elegir una mesa o al tocar el boton de nuevo se cierra.
    if (mesaAbierta && anclaMesa != null) {
        Popup(
            onDismissRequest = { mesaAbierta = false },
            properties = PopupProperties(focusable = true),
            popupPositionProvider = PosicionSobreAncla(anclaMesa!!, distanciaMesa)
        ) {
            ListaDeMesas(
                mesaActual = mesa,
                onSelecciona = { nuevaMesa ->
                    onMesaChange(nuevaMesa)
                    mesaAbierta = false
                }
            )
        }
    }
}

/**
 * El botoncito "4 v" de la tarjeta inferior. Asi el ancla [onAncla] queda en
 * un sitio conocido y no se dispara el redibujo solo por tener un estado.
 */
@Composable
private fun SelectorDeMesa(
    mesa: String,
    activo: Boolean,
    abierto: Boolean,
    onToggle: () -> Unit,
    onAncla: (Rect) -> Unit
) {
    Box(
        modifier = Modifier
            .onGloballyPositioned { onAncla(it.boundsInRoot()) }
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1C1C1F))
            .border(
                width = if (abierto) 1.5.dp else 1.dp,
                color = when {
                    abierto -> Colores.VerdeBoton
                    !activo -> Color(0xFF2E2E32)
                    else -> Color(0xFF3A3A3E)
                },
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(enabled = activo) { onToggle() }
            .padding(horizontal = 18.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = mesa,
                color = if (activo) Color.White else Colores.TextoTenue,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = if (abierto) "Cerrar lista de mesas" else "Elegir mesa",
                tint = if (activo) Color(0xFF9A9AA0) else Colores.TextoTenue,
                modifier = Modifier
                    .size(22.dp)
                    .rotate(if (abierto) 180f else 0f)
            )
        }
    }
}

/**
 * La lista que aparece flotando: mesas 1 a 15, con un limite de altura para
 * que siempre quepa encima del boton y sea scrolleable.
 */
@Composable
private fun ListaDeMesas(
    mesaActual: String,
    onSelecciona: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .width(112.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF232327))
            .border(1.dp, Color(0xFF36363B), RoundedCornerShape(16.dp))
            .padding(6.dp)
            .heightIn(max = 240.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        (1..15).forEach { numero ->
            val textoMesa = "$numero"
            val elegida = textoMesa == mesaActual
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (elegida) Color(0xFF14311F) else Color.Transparent)
                    .clickable(enabled = !elegida) { onSelecciona(textoMesa) }
                    .padding(horizontal = 14.dp, vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = textoMesa,
                    color = if (elegida) Colores.VerdeBoton else Color(0xFFB3B3B8),
                    fontSize = 16.sp,
                    fontWeight = if (elegida) FontWeight.Bold else FontWeight.Normal
                )
                if (elegida) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Colores.VerdeBoton,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

/**
 * Posiciona la lista justo encima del boton de mesa, alineada a su borde
 * derecho. Si arriba no hay espacio (pantalla corta), se abre hacia abajo.
 */
private class PosicionSobreAncla(
    private val ancla: Rect,
    private val distanciaPx: Float
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize
    ): IntOffset {
        var x = ancla.right.toInt() - popupContentSize.width
        x = x.coerceIn(0, (windowSize.width - popupContentSize.width).coerceAtLeast(0))

        var y = (ancla.top - distanciaPx).toInt() - popupContentSize.height
        // si no cabe arriba, se abre hacia abajo.
        if (y < 0) y = (ancla.bottom + distanciaPx).toInt()
        y = y.coerceIn(0, (windowSize.height - popupContentSize.height).coerceAtLeast(0))
        return IntOffset(x, y)
    }
}

@Composable
private fun CarritoItemCard(
    nombre: String,
    precioUnitario: String,
    icono: ImageVector,
    fondoImagen: Color,
    tintaIcono: Color,
    cantidad: Int,
    disponible: Boolean,
    interactivo: Boolean,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
    modifier: Modifier = Modifier,
    nota: String = "",
    onNotaChange: (String) -> Unit = {}
) {
    // si esta escribiendo la nota se muestra el campo; si no, el texto o el boton.
    var editandoNota by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Colores.Superficie)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(fondoImagen),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = tintaIcono,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = nombre,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = precioUnitario,
                    color = Colores.TextoTenue,
                    fontSize = 13.sp
                )
            }
            CantidadStepper(
                cantidad = cantidad,
                onMinus = onMinus,
                onPlus = onPlus,
                plusEnabled = disponible && interactivo,
                minusEnabled = interactivo
            )
        }
        if (!disponible) {
            Text(
                text = "No disponible · reduce la cantidad a cero para quitarlo",
                color = Color(0xFFFFA500),
                fontSize = 12.sp
            )
        }
        if (editandoNota) {
            CampoTextoAdmin(
                valor = nota,
                onValorChange = onNotaChange,
                placeholder = "Ej. sin cebolla",
                readOnly = !interactivo,
                trailing = {
                    Text(
                        text = "OK",
                        color = Colores.VerdePrimario,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clickable(enabled = interactivo) { editandoNota = false }
                    )
                }
            )
        } else if (nota.isNotBlank()) {
            Text(
                text = nota,
                color = Colores.TextoTenue,
                fontSize = 13.sp,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                modifier = Modifier.clickable(enabled = interactivo) { editandoNota = true }
            )
        } else {
            Text(
                text = "+ Agregar nota (ej. sin cebolla)",
                color = Colores.TextoTenue,
                fontSize = 13.sp,
                modifier = Modifier.clickable(enabled = interactivo) { editandoNota = true }
            )
        }
    }
}

@Composable
@Preview
fun CarritoPreviewScreen(){
    CarritoScreen(
        backCarritoScreen = {},
        confirmarCarrito = { _ -> }
    )
}
