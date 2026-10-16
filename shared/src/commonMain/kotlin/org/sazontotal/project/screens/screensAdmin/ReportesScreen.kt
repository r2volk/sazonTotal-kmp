package org.sazontotal.project.screens.screensAdmin

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.sazontotal.project.components.FiltroButton
import org.sazontotal.project.components.StatCardCentrado
import org.sazontotal.project.data.repository.SazonRepository
import org.sazontotal.project.utils.ahoraMillis
import org.sazontotal.project.utils.partesFecha
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.Dp
import org.sazontotal.project.screens.screensAdmin.editarMenu.EditarMenuScreen

@Composable
fun ReportesScreen(
    backReportes: () -> Unit,
    repositorio: SazonRepository? = null
){

    // 1. Pedidos en lista viva.
    val pedidosEnVivo = repositorio?.observarPedidos()
    val pedidos = pedidosEnVivo?.collectAsState(initial = emptyList())?.value ?: emptyList()

    // Solo lo ENTREGADO es venta. Lo pendiente, listo o anulado no suma.
    val vendidos = pedidos.filter { it.estado == "ENTREGADO" }

    // 2. Período elegido + hora congelada (para que los grupos no bailen al redibujar).
    var periodo by remember { mutableStateOf("week") }
    val ahora = remember { ahoraMillis() }

    // Un día en milisegundos. Las ventanas se miden hacia atrás desde ahora.
    // Solo cuentan pedidos con fecha real (los viejos con fecha 0 quedan fuera).
    val DIA = 86_400_000L
    val largoVentana = when (periodo) {
        "month" -> 28 * DIA
        "year" -> 360 * DIA
        else -> 7 * DIA
    }
    fun enVentana(fecha: Long, atras: Long): Boolean {
        return fecha > 0 && ahora - fecha >= atras && ahora - fecha < atras + largoVentana
    }
    val pedidosVentana = vendidos.filter { enVentana(it.fecha, 0) }
    val pedidosAntes = vendidos.filter { enVentana(it.fecha, largoVentana) }

    // 3. Stats del período: ventas, conteo y promedio.
    val ganancias = pedidosVentana.sumOf { it.total }
    val pedidosTotales = pedidosVentana.size
    val promedio = if (pedidosTotales == 0) 0.0 else ganancias / pedidosTotales

    // 4. Comparación con la ventana anterior del mismo largo.
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
        Color(0xFF30D158)
    }

    // 5. Barras del gráfico: etiquetas + sumas por grupo, de más viejo a más nuevo.
    val letrasDia = listOf("D", "L", "M", "M", "J", "V", "S")
    val inicialesMes = listOf("E", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D")
    val mesActual = partesFecha(ahora).second
    val (etiquetas, sumas) = when (periodo) {
        "month" -> {
            // Últimas 4 semanas: S1 la más vieja.
            val grupos = (3 downTo 0).map { atras ->
                val desde = ahora - (atras + 1) * 7 * DIA
                val hasta = ahora - atras * 7 * DIA
                vendidos.filter { it.fecha in desde..hasta }.sumOf { it.total }
            }
            listOf("S1", "S2", "S3", "S4") to grupos
        }
        "year" -> {
            // Últimos 12 grupos de 30 días, con la inicial de cada mes.
            val nombres = (11 downTo 0).map { atras ->
                inicialesMes[(mesActual - 1 - atras + 120) % 12]
            }
            val grupos = (11 downTo 0).map { atras ->
                val desde = ahora - (atras + 1) * 30 * DIA
                val hasta = ahora - atras * 30 * DIA
                vendidos.filter { it.fecha in desde..hasta }.sumOf { it.total }
            }
            nombres to grupos
        }
        else -> {
            // Últimos 7 días: la letra sale de matemática simple
            // (el 1-1-1970 fue jueves, y cada día suma 1).
            val nombres = (6 downTo 0).map { atras ->
                val indice = (((ahora - atras * DIA) / DIA) + 4) % 7
                letrasDia[indice.toInt()]
            }
            val grupos = (6 downTo 0).map { atras ->
                val desde = ahora - (atras + 1) * DIA
                val hasta = ahora - atras * DIA
                vendidos.filter { it.fecha in desde..hasta }.sumOf { it.total }
            }
            nombres to grupos
        }
    }

    // 6. Alturas proporcionales (la mayor en verde).
    val maximo = sumas.maxOrNull() ?: 0.0
    val alturas = if (maximo == 0.0) {
        sumas.map { 0.15f }
    } else {
        sumas.map { (it / maximo).toFloat().coerceIn(0.15f, 1f) }
    }
    val destacado = if (maximo == 0.0) -1 else sumas.indexOf(maximo)

    // 7. Top 5 platos más vendidos de siempre (lista viva).
    val topEnVivo = repositorio?.observarTopPlatos()
    val top = topEnVivo?.collectAsState(initial = emptyList())?.value ?: emptyList()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000))

    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 55.dp, bottom = 20.dp, start = 17.dp, end = 17.dp ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
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
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .heightIn(min = 60.dp)
                        .background(color = Color(0xFF1C1C1E),
                            shape = RoundedCornerShape(12.dp))
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "S/ "+ ganancias.toString(),
                        color = Color.Green,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (periodo == "month") "Ventas del mes" else if (periodo == "year") "Ventas del año" else "Ventas de la semana",
                        color = Color(0xFF8E8E93),
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
                        .heightIn(min = 60.dp)
                        .background(
                            color = Color(0xFF1C1C1E),
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
                        color = Color(0xFF8E8E93),
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Prom: S/ "+ promedio,
                        color = Color(0xFF8E8E93),
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
                color = Color(0xFF8E8E93),
            )

            graficoStatReporte(
                etiquetas = etiquetas,
                valores = alturas,
                indiceDestacado = destacado,
                tipo = periodo
            )

            Text(
                text = "Platos mas vendidos",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF8E8E93),
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
            .background(color = Color(0xFF1C1C1E),
                shape = RoundedCornerShape(13.dp))
            .padding(15.dp),
    ) {
        //puesto
        Text(
            text = puesto.toString(),
            color = if (puesto==1) Color.Green else Color(0xFF8E8E93),
            fontWeight = FontWeight.Bold
        )

        //nombre del plato
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(start = 32.dp),
            text = plato,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        //cantidad
        Text(
            text = cantidad.toString()+" uds",
            color = Color(0xFF8E8E93),
            fontWeight = FontWeight.Bold,
        )

    }

}

@Composable
private fun graficoStatReporte(
    etiquetas: List<String>,
    valores: List<Float>,
    indiceDestacado: Int,
    tipo: String = "week"
) {
    val anchoBarra = when (tipo) {
        "month" -> 40.dp
        "year" -> 20.dp
        else -> 35.dp // week
    }

    val colorBarra = Color(0xFF3A3A3C)
    val colorBarraDestacada = Color(0xFF34C759)

    Column(
        modifier = Modifier
            .heightIn(min = 60.dp)
            .background(color = Color(0xFF1C1C1E),
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
                        .fillMaxHeight(valor.coerceIn(0.15f, 1f))
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