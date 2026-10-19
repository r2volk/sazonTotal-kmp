package org.sazontotal.project.screens.screensAdmin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.sazontotal.project.components.FiltroButton
import org.sazontotal.project.data.repository.SazonRepository
import org.sazontotal.project.theme.Colores
import org.sazontotal.project.utils.ahoraMillis
import org.sazontotal.project.utils.decimales
import org.sazontotal.project.utils.moneda
import org.sazontotal.project.utils.partesFecha

@Composable
fun ReportesScreen(
    backReportes: () -> Unit,
    repositorio: SazonRepository? = null
){

    val pedidosEnVivo = repositorio?.observarPedidos()
    val pedidos = pedidosEnVivo?.collectAsState(initial = emptyList())?.value ?: emptyList()

    // solo lo ENTREGADO es venta. lo pendiente, listo o anulado no suma.
    val vendidos = pedidos.filter { it.estado == "ENTREGADO" }

    // la hora se congela para que los grupos no bailen al redibujar
    var periodo by remember { mutableStateOf("week") }
    val ahora = remember { ahoraMillis() }

    // un dia en milisegundos. las ventanas se miden hacia atras desde ahora.
    // solo cuentan pedidos con fecha real (los viejos con fecha 0 quedan fuera).
    val DIA = 86_400_000L
    val anioActual = partesFecha(ahora).third

    // "Semana" y "Mes" son ventanas rodantes hacia atras.
    // "Año" NO lo es: son los 12 meses calendario del anio actual (enero a
    // diciembre), que es como lo lee cualquiera un reporte anual.
    val largoVentana = when (periodo) {
        "month" -> 28 * DIA
        else -> 7 * DIA
    }
    fun enVentana(fecha: Long, atras: Long): Boolean {
        return fecha > 0 && ahora - fecha >= atras && ahora - fecha < atras + largoVentana
    }
    fun enAnio(fecha: Long, anio: Int): Boolean {
        return fecha > 0 && partesFecha(fecha).third == anio
    }
    val pedidosVentana = vendidos.filter { pedido ->
        if (periodo == "year") enAnio(pedido.fecha, anioActual) else enVentana(pedido.fecha, 0)
    }
    // La comparacion usa el mismo criterio: el ano anterior en modo "Año",
    // y la ventana anterior del mismo largo en los otros modos.
    val pedidosAntes = vendidos.filter { pedido ->
        if (periodo == "year") enAnio(pedido.fecha, anioActual - 1) else enVentana(pedido.fecha, largoVentana)
    }

    val ganancias = pedidosVentana.sumOf { it.total }
    val pedidosTotales = pedidosVentana.size
    val promedio = if (pedidosTotales == 0) 0.0 else ganancias / pedidosTotales

    val ventasAntes = pedidosAntes.sumOf { it.total }
    val nombreVentana = if (periodo == "month") "mes" else if (periodo == "year") "año" else "semana"
    val textoComparacion = if (ventasAntes == 0.0) {
        "Sin datos previos"
    } else {
        val cambio = ((ganancias - ventasAntes) / ventasAntes * 100).toInt()
        val flecha = if (cambio >= 0) "↑" else "↓"
        val numero = if (cambio < 0) -cambio else cambio
        "$flecha $numero% vs $nombreVentana pasada"
    }
    val colorComparacion = if (ventasAntes != 0.0 && ganancias < ventasAntes) {
        Color.Red
    } else {
        Colores.VerdePrimario
    }

    // el 1-1-1970 fue jueves, asi que cada dia suma 1 desde ahi
    val letrasDia = listOf("D", "L", "M", "M", "J", "V", "S")
    val inicialesMes = listOf("E", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D")
    val (etiquetas, sumas) = when (periodo) {
        "month" -> {
            // ultimas 4 semanas: S1 la mas vieja.
                val grupos = (3 downTo 0).map { atras ->
                    val desde = ahora - (atras + 1) * 7 * DIA
                    val hasta = ahora - atras * 7 * DIA
                    vendidos.filter { it.fecha > desde && it.fecha <= hasta }.sumOf { it.total }
            }
            listOf("S1", "S2", "S3", "S4") to grupos
        }
        "year" -> {
            // Los 12 meses REALES del anio actual, de enero a diciembre.
            val grupos = (1..12).map { mes ->
                vendidos.filter { pedido ->
                    pedido.fecha > 0 && partesFecha(pedido.fecha).let { (_, m, a) ->
                        m == mes && a == anioActual
                    }
                }.sumOf { it.total }
            }
            inicialesMes to grupos
        }
        else -> {
            // ultimos 7 dias, de la letra mas nueva a la mas vieja
            val nombres = (6 downTo 0).map { atras ->
                val indice = (((ahora - atras * DIA) / DIA) + 4) % 7
                letrasDia[indice.toInt()]
            }
            val grupos = (6 downTo 0).map { atras ->
                val desde = ahora - (atras + 1) * DIA
                val hasta = ahora - atras * DIA
                vendidos.filter { it.fecha > desde && it.fecha <= hasta }.sumOf { it.total }
            }
            nombres to grupos
        }
    }

    // Las barras son proporcionales al real: un dia SIN ventas tiene barra en 0,
// no un mini-boton de 15% (antes todo dia con 0 ventas parecia tener actividad).
    val maximo = sumas.maxOrNull() ?: 0.0
    val hayVentas = maximo > 0.0
    val alturas = sumas.map {
        if (hayVentas) (it / maximo).toFloat().coerceIn(0f, 1f) else 0f
    }
    val destacado = if (hayVentas) sumas.indexOf(maximo) else -1

    val topEnVivo = when {
        repositorio == null -> null
        periodo == "year" -> repositorio.observarTopPlatosEnAnio(anioActual)
        else -> repositorio.observarTopPlatosEnRango(ahora - largoVentana, ahora)
    }
    val top = topEnVivo?.collectAsState(initial = emptyList())?.value ?: emptyList()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000))

    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 55.dp, bottom = 20.dp, start = 17.dp, end = 17.dp ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(
                    onClick = {
                        backReportes()
                    },
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
                    text = "Reportes",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FiltroButton(
                    texto = "Semana",
                    activo = periodo == "week",
                    onClick = { periodo = "week" }
                )
                FiltroButton(
                    texto = "Mes",
                    activo = periodo == "month",
                    onClick = { periodo = "month" }
                )
                FiltroButton(
                    texto = "Año",
                    activo = periodo == "year",
                    onClick = { periodo = "year" }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 60.dp)
                        .background(color = Colores.Superficie,
                            shape = RoundedCornerShape(12.dp))
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = moneda(ganancias),
                        color = Color.Green,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (periodo == "month") "Ventas del mes" else if (periodo == "year") "Ventas del año" else "Ventas de la semana",
                        color = Colores.TextoTenue,
                        fontSize = 14.sp
                    )
                    Text(
                        text = textoComparacion,
                        color = colorComparacion,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 60.dp)
                        .background(
                            color = Colores.Superficie,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(
                            horizontal = 20.dp,
                            vertical = 12.dp
                        ),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = pedidosTotales.toString(),
                        color = Color.White,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Pedidos Totales",
                        color = Colores.TextoTenue,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Prom: ${decimales(promedio)}",
                        color = Colores.TextoTenue,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = if (periodo=="month") "Ventas por semana"
                       else if (periodo=="year") "Ventas por mes"
                       else "Ventas por dia",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Colores.TextoTenue,
            )

            graficoStatReporte(
                etiquetas = etiquetas,
                valores = alturas,
                indiceDestacado = destacado,
                hayVentas = hayVentas,
                tipo = periodo
            )

            Text(
                text = "Platos mas vendidos",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Colores.TextoTenue,
            )

            top.forEachIndexed { indice, venta ->
                platosMasVendidos(
                    plato = venta.nombrePlato,
                    puesto = indice + 1,
                    cantidad = venta.total
                )
            }

        }

    }
}


@Composable
private fun platosMasVendidos(
    plato: String,
    puesto: Int,
    cantidad: Int
){
    Row(
        modifier = Modifier
            .background(color = Colores.Superficie,
                shape = RoundedCornerShape(13.dp))
            .padding(15.dp),
    ) {
        Text(
            text = puesto.toString(),
            color = if (puesto==1) Color.Green else Colores.TextoTenue,
            fontWeight = FontWeight.Bold
        )

        Text(
            modifier = Modifier
                .weight(1f)
                .padding(start = 32.dp),
            text = plato,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = cantidad.toString()+" uds",
            color = Colores.TextoTenue,
            fontWeight = FontWeight.Bold,
        )

    }

}

@Composable
private fun graficoStatReporte(
    etiquetas: List<String>,
    valores: List<Float>,
    indiceDestacado: Int,
    hayVentas: Boolean,
    tipo: String = "week"
) {
    val anchoBarra = when (tipo) {
        "month" -> 40.dp
        "year" -> 20.dp
        else -> 35.dp
    }

    val colorBarra = Colores.GrisDeshabilitado
    val colorBarraDestacada = Color(0xFF34C759)

    // Sin ventas no se dibuja una grafica de barras vacia (que antes salia
    // con todas las barras al 15% y parecia actividad). Se dice que no hay.
    if (!hayVentas) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(color = Colores.Superficie, shape = RoundedCornerShape(12.dp))
                .padding(15.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Sin ventas en este período",
                color = Colores.TextoTenue,
                fontSize = 15.sp
            )
        }
        return
    }

    Column(
        modifier = Modifier
            .heightIn(min = 60.dp)
            .background(color = Colores.Superficie,
                shape = RoundedCornerShape(12.dp))
            .padding(15.dp),
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            valores.forEachIndexed { index, valor ->
                Box(
                    modifier = Modifier
                        .width(anchoBarra)
                        .fillMaxHeight(valor.coerceIn(0f, 1f))
                        .background(
                            color = if (index == indiceDestacado) colorBarraDestacada else colorBarra,
                            shape = RoundedCornerShape(10.dp)
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            etiquetas.forEachIndexed { index, label ->
                Text(
                    text = label,
                    color = if (index == indiceDestacado) colorBarraDestacada else Color.Gray,
                    fontSize = 13.sp,
                    fontWeight = if (index == indiceDestacado) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
@Preview
fun ReportesScreenPreview(){
    ReportesScreen(
        backReportes = {}
    )
}
