package org.sazontotal.project.screens.screensMesero.carrito

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.ConfirmationNumber
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.sazontotal.project.components.CampoTextoAdmin
import org.sazontotal.project.components.CantidadStepper
import org.sazontotal.project.components.visualDeCategoria
import org.sazontotal.project.data.CarritoCompra
import org.sazontotal.project.data.db.PedidoEntity
import org.sazontotal.project.data.db.PedidoItemEntity
import org.sazontotal.project.data.repository.SazonRepository
import org.sazontotal.project.theme.Colores
import org.sazontotal.project.utils.ahoraMillis

@Composable
fun CarritoScreen(
    backCarritoScreen: () -> Unit,
    confirmarCarrito: (Long) -> Unit,
    meseroId: String = "MSR-001",
    repositorio: SazonRepository? = null,
    carrito: CarritoCompra? = null,
    mesa: String = "4"
) {
    var codigoPromo by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    val platosEnVivo = repositorio?.observarPlatos()
    val platos = platosEnVivo?.collectAsState(initial = emptyList())?.value ?: emptyList()

    // 0 = sin promo
    var descuentoPorc by remember { mutableStateOf(0) }
    var promoAplicada by remember { mutableStateOf("") }
    var promoError by remember { mutableStateOf(false) }

    val subtotal = carrito?.subtotal(platos) ?: 0.0
    val descuento = subtotal * descuentoPorc / 100.0
    val igv = (subtotal - descuento) * 0.18
    val total = subtotal - descuento + igv

    // sin nada en el carrito el boton no hace nada
    val hayAlgo = (carrito?.totalItems() ?: 0) > 0

    // se apaga mientras guarda, asi el doble tap no crea dos pedidos
    var guardando by remember { mutableStateOf(false) }
    val puedeConfirmar = hayAlgo && !guardando
    // Si el guardado falla, se avisa (y nunca se queda en "Enviando...").
    var errorGuardado by remember { mutableStateOf<String?>(null) }

    // al bajar a 0 el card sale solo de la lista
    val platosEnCarrito = platos.filter { plato ->
        (carrito?.cantidadDe(plato.id) ?: 0) > 0
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(start = 20.dp, end = 20.dp, top = 60.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(
                onClick = { backCarritoScreen()},
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
                val cantidad = carrito?.cantidadDe(plato.id) ?: 0
                val visual = visualDeCategoria(plato.categoria)
                CarritoItemCard(
                    nombre = plato.nombre,
                    precioUnitario = "S/ ${plato.precio} c/u",
                    icono = visual.icono,
                    fondoImagen = visual.fondo,
                    tintaIcono = visual.tinta,
                    cantidad = cantidad,
                    onMinus = { carrito?.restar(plato.id) },
                    onPlus = { carrito?.sumar(plato.id) },
                    nota = carrito?.notaDe(plato.id) ?: "",
                    onNotaChange = { carrito?.ponerNota(plato.id, it) }
                )
            }
            if (!hayAlgo) {
                Text(
                    text = "Carrito vacío: suma platos con + desde el menú",
                    color = Colores.TextoTenue,
                    fontSize = 14.sp
                )
            }

        }

        // codigo de promocion
        CampoTextoAdmin(
            valor = codigoPromo,
            onValorChange = { codigoPromo = it },
            placeholder = "Código de promoción",
            leading = {
                Icon(
                    imageVector = Icons.Outlined.ConfirmationNumber,
                    contentDescription = null,
                    tint = Colores.TextoTenue,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailing = {
                Text(
                    text = "Aplicar",
                    color = Colores.VerdePrimario,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clickable {
                            scope.launch {
                                // se busca el codigo en la base (vale en mayusculas o minusculas).
                                val promo = repositorio?.buscarPromo(codigoPromo)
                                if (promo == null) {
                                    promoError = true
                                    descuentoPorc = 0
                                    promoAplicada = ""
                                } else {
                                    promoError = false
                                    descuentoPorc = promo.porcentaje
                                    promoAplicada = promo.codigo
                                }
                            }
                        }
                )
            }
        )
        // mensaje segun lo que paso con el codigo.
        if (promoError) {
            Text(
                text = "Código inválido",
                color = Colores.Rojo,
                fontSize = 13.sp
            )
        }
        if (descuentoPorc > 0) {
            Text(
                text = "Promo $promoAplicada: −$descuentoPorc%",
                color = Colores.VerdePrimario,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // totales
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Subtotal",
                    color = Colores.TextoTenue,
                    fontSize = 15.sp,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "S/ $subtotal",
                    color = Color.White,
                    fontSize = 15.sp
                )
            }
            if (descuento > 0) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Descuento ($descuentoPorc%)",
                        color = Colores.VerdePrimario,
                        fontSize = 15.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "−S/ $descuento",
                        color = Colores.VerdePrimario,
                        fontSize = 15.sp
                    )
                }
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "IGV (18%)",
                    color = Colores.TextoTenue,
                    fontSize = 15.sp,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "S/ $igv",
                    color = Color.White,
                    fontSize = 15.sp
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "S/ $total",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Aviso si el pedido no se pudo guardar (para que el mesero no quede colgado).
        if (errorGuardado != null) {
            Text(
                text = errorGuardado!!,
                color = Color(0xFFFF453A),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (puedeConfirmar) Colores.VerdeBoton else Colores.GrisDeshabilitado)
                .clickable {
                    if (!puedeConfirmar) return@clickable
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
                            // solo los que tienen cantidad > 0, y se copia el precio
                            // al renglon para que no cambie si editan el plato
                            val renglones = platos.mapNotNull { plato ->
                                val cantidad = carrito?.cantidadDe(plato.id) ?: 0
                                if (cantidad == 0) null else PedidoItemEntity(
                                    pedidoId = 0, // se marca solo adentro de crearPedidoConItems
                                    platoId = plato.id,
                                    nombrePlato = plato.nombre,
                                    cantidad = cantidad,
                                    nota = carrito?.notaDe(plato.id) ?: "",
                                    precioUnit = plato.precio
                                )
                            }
                            val numeroPedido = repositorio?.crearPedidoConItems(cabecera, renglones) ?: 0
                            // el pedido ya salio: se vacia el carrito y la promo.
                            carrito?.limpiar()
                            descuentoPorc = 0
                            promoAplicada = ""
                            codigoPromo = ""
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
}

@Composable
private fun CarritoItemCard(
    nombre: String,
    precioUnitario: String,
    icono: ImageVector,
    fondoImagen: Color,
    tintaIcono: Color,
    cantidad: Int,
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
                onPlus = onPlus
            )
        }
        if (editandoNota) {
            CampoTextoAdmin(
                valor = nota,
                onValorChange = onNotaChange,
                placeholder = "Ej. sin cebolla",
                trailing = {
                    Text(
                        text = "OK",
                        color = Colores.VerdePrimario,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clickable { editandoNota = false }
                    )
                }
            )
        } else if (nota.isNotBlank()) {
            Text(
                text = nota,
                color = Colores.TextoTenue,
                fontSize = 13.sp,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                modifier = Modifier.clickable { editandoNota = true }
            )
        } else {
            Text(
                text = "+ Agregar nota (ej. sin cebolla)",
                color = Colores.TextoTenue,
                fontSize = 13.sp,
                modifier = Modifier.clickable { editandoNota = true }
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