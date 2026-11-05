package org.sazontotal.project.screens.screensMesero.carrito

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
import kotlinx.coroutines.delay
import org.sazontotal.project.data.db.PedidoEntity
import org.sazontotal.project.data.destinoPedido
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
    val temaOscuro = LocalTemaOscuro.current
    // Evita que un segundo toque de confirmación active el botón en la misma posición.
    var accionesDisponibles by remember(pedidoId) { mutableStateOf(false) }
    LaunchedEffect(pedidoId) {
        delay(700)
        accionesDisponibles = true
    }
    // la cabecera se lee una vez y sus renglones se observan desde room para armar el ticket.
    var pedido by remember { mutableStateOf<PedidoEntity?>(null) }
    LaunchedEffect(pedidoId) {
        pedido = repositorio?.buscarPedidoPorId(pedidoId)
    }

    val itemsEnVivo = repositorio?.observarItems(pedidoId)
    val items = itemsEnVivo?.collectAsState(initial = emptyList())?.value ?: emptyList()

    // los precios ya incluyen IGV: se extraen base e IGV del total guardado, sin sumar impuesto.
    val total = pedido?.total ?: items.sumOf { it.precioUnit * it.cantidad }
    val subtotal = total / 1.18
    val igv = total - subtotal
    // arriba scrollea y los botones quedan fijos abajo para que no se salgan de pantalla.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorDeTema(Color.Black, temaOscuro, UsoColor.FONDO))
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
                    .background(colorDeTema(Color(0xFF143D2B), temaOscuro, UsoColor.FONDO)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = colorDeTema(Colores.VerdePrimario, temaOscuro, UsoColor.TEXTO),
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
                color = colorDeTema(Color.White, temaOscuro, UsoColor.TEXTO),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Ticket #${pedidoId.toString().padStart(4, '0')} · ${pedido?.let { destinoPedido(it.mesa) } ?: "–"}",
                color = colorDeTema(Colores.TextoTenue, temaOscuro, UsoColor.TEXTO),
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
                        .background(colorDeTema(Colores.VerdePrimario, temaOscuro, UsoColor.FONDO)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = colorDeTema(Color.Black, temaOscuro, UsoColor.TEXTO),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .padding(horizontal = 6.dp)
                        .clip(RoundedCornerShape(50))
                        .background(colorDeTema(Colores.VerdePrimario, temaOscuro, UsoColor.FONDO))
                )
                // preparando (actual)
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(colorDeTema(Color(0xFF3D2905), temaOscuro, UsoColor.FONDO))
                        .border(2.dp, colorDeTema(Colores.Ambar, temaOscuro, UsoColor.BORDE), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Schedule,
                        contentDescription = null,
                        tint = colorDeTema(Colores.Ambar, temaOscuro, UsoColor.TEXTO),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .padding(horizontal = 6.dp)
                        .clip(RoundedCornerShape(50))
                        .background(colorDeTema(Colores.GrisDeshabilitado, temaOscuro, UsoColor.FONDO))
                )
                // listo (pendiente)
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .border(2.dp, colorDeTema(Colores.GrisDeshabilitado, temaOscuro, UsoColor.BORDE), CircleShape)
                )
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Enviado",
                    color = colorDeTema(Colores.VerdePrimario, temaOscuro, UsoColor.TEXTO),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Preparando",
                    color = colorDeTema(Colores.Ambar, temaOscuro, UsoColor.TEXTO),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Listo",
                    color = colorDeTema(Colores.TextoTenue, temaOscuro, UsoColor.TEXTO),
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
                .background(colorDeTema(Colores.Superficie, temaOscuro, UsoColor.FONDO))
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
                    .background(colorDeTema(Colores.GrisMedio, temaOscuro, UsoColor.FONDO))
            )
            // base imponible e IGV incluidos en los precios del menu.
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Base imponible",
                    color = colorDeTema(Colores.TextoTenue, temaOscuro, UsoColor.TEXTO),
                    fontSize = 15.sp,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = moneda(subtotal),
                    color = colorDeTema(Colores.TextoTenue, temaOscuro, UsoColor.TEXTO),
                    fontSize = 15.sp
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "IGV incluido (18%)",
                    color = colorDeTema(Colores.TextoTenue, temaOscuro, UsoColor.TEXTO),
                    fontSize = 15.sp,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = moneda(igv),
                    color = colorDeTema(Colores.TextoTenue, temaOscuro, UsoColor.TEXTO),
                    fontSize = 15.sp
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(colorDeTema(Colores.GrisMedio, temaOscuro, UsoColor.FONDO))
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total",
                    color = colorDeTema(Color.White, temaOscuro, UsoColor.TEXTO),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = moneda(total),
                    color = colorDeTema(Color.White, temaOscuro, UsoColor.TEXTO),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(colorDeTema(Colores.Superficie, temaOscuro, UsoColor.FONDO))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tiempo estimado",
                color = colorDeTema(Colores.TextoTenue, temaOscuro, UsoColor.TEXTO),
                fontSize = 15.sp,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "~15 min",
                color = colorDeTema(Color.White, temaOscuro, UsoColor.TEXTO),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(colorDeTema(Colores.VerdeBoton, temaOscuro, UsoColor.FONDO))
                .clickable(enabled = accionesDisponibles) {
                        volverDashboard()
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Volver al dashboard",
                color = colorDeTema(Color.Black, temaOscuro, UsoColor.TEXTO),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, colorDeTema(Colores.GrisMedio, temaOscuro, UsoColor.BORDE), RoundedCornerShape(12.dp))
                .clickable(enabled = accionesDisponibles) {
                    otroPedido()
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Iniciar otro pedido",
                color = colorDeTema(Color(0xFFB0B0B0), temaOscuro, UsoColor.TEXTO),
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
    val temaOscuro = LocalTemaOscuro.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = plato,
            color = colorDeTema(Color(0xFFB0B0B0), temaOscuro, UsoColor.TEXTO),
            fontSize = 15.sp,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = precio,
            color = colorDeTema(Color.White, temaOscuro, UsoColor.TEXTO),
            fontSize = 15.sp
        )
    }
}

@Composable
@Preview
fun PedidoConcretadoPreview(){
    val temaOscuro = LocalTemaOscuro.current
    PedidoConcretadoScreen(
        volverDashboard = {},
        otroPedido = {}
    )
}
