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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.Dp

@Composable
@Preview
fun ReportesScreen(){

    var ganancias by remember { mutableStateOf( 5900.0) }
    var porcentaje by remember { mutableStateOf( 12) }
    var pedidosTotales by remember { mutableStateOf( 168) }
    var promedio by remember { mutableStateOf( 35.35) }
    var periodo by remember { mutableStateOf( "year") }

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
                    onClick = { },
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
                    activo = true,
                    onClick = {}
                )
                FiltroButton(
                    texto = "Mes",
                    activo = false,
                    onClick = {}
                )
                FiltroButton(
                    texto = "Año",
                    activo = false,
                    onClick = {}
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
                        text = "Ventas de la semana",
                        color = Color(0xFF8E8E93),
                        fontSize = 14.sp
                    )
                    Text(
                        text = "↑ "+ porcentaje +"% vs sem. pasada",
                        color = Color.Red,
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
                periodo = periodo
            )

            Text(
                text = "Platos mas vendidos",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF8E8E93),
            )

            platosMasVendidos(
                plato = "Lomo Saltado",
                puesto = 1,
                cantidad = 12
            )
            platosMasVendidos(
                plato = "Lomo Saltado",
                puesto = 2,
                cantidad = 12
            )
            platosMasVendidos(
                plato = "Lomo Saltado",
                puesto = 3,
                cantidad = 12
            )
            platosMasVendidos(
                plato = "Lomo Saltado",
                puesto = 4,
                cantidad = 12
            )
            platosMasVendidos(
                plato = "Lomo Saltado",
                puesto = 5,
                cantidad = 12
            )

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
private fun graficoStatReporte(periodo: String = "week") {

    val tipo = if (periodo == "week" || periodo == "month" || periodo == "year") periodo else "week"

    val labels: List<String>
    val valores: List<Float>
    val indiceDestacado: Int
    val anchoBarra = when (tipo) {
        "month" -> 40.dp
        "year" -> 20.dp
        else -> 35.dp // week
    }

    when (tipo) {
        "month" -> {
            labels = listOf("S1", "S2", "S3", "S4")
            valores = listOf(0.55f, 0.8f, 0.65f, 1f)
            indiceDestacado = 3
        }
        "year" -> {
            labels = listOf("E", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D")
            valores = listOf(0.4f, 0.6f, 0.5f, 0.7f, 0.55f, 0.65f, 0.8f, 1f, 0.6f, 0.5f, 0.45f, 0.7f)
            indiceDestacado = 7
        }
        else -> {
            labels = listOf("L", "M", "M", "J", "V", "S", "D")
            valores = listOf(0.35f, 0.55f, 0.45f, 0.65f, 1f, 0.4f, 0.3f)
            indiceDestacado = 4
        }
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
            labels.forEachIndexed { index, label ->
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