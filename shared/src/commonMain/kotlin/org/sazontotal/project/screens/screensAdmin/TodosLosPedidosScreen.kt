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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import org.sazontotal.project.components.StatCardCentrado
import org.sazontotal.project.enums.EstadoPedido
import org.sazontotal.project.enums.EstadoTodosLosPedidos

@Composable
@Preview
fun TodosLosPedidosScreen(){

    var textoBusqueda by remember { mutableStateOf("") }
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
                    textoSuperior = "24",
                    textoInferior = "Hoy",
                    colorSuperior = Color.White,
                    modifier = Modifier.weight(1f)
                )
                StatCardCentrado(
                    textoSuperior = "3",
                    textoInferior = "En Curso",
                    colorSuperior = Color(0xFFFF9F0A),
                    modifier = Modifier.weight(1f)
                )
                StatCardCentrado(
                    textoSuperior = "20",
                    textoInferior = "Entregado",
                    colorSuperior = Color.Green,
                    modifier = Modifier.weight(1f)
                )
                StatCardCentrado(
                    textoSuperior = "1",
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
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                FiltroButton(
                    texto = "Todos",
                    activo = true,
                    onClick = {}
                )
                FiltroButton(
                    texto = "Pendientes",
                    activo = false,
                    onClick = {}
                )
                FiltroButton(
                    texto = "Listos",
                    activo = false,
                    onClick = {}
                )
            }

            CardPedido(
                estadoTodosLosPedidos = EstadoTodosLosPedidos.PREPARANDO
            )
            CardPedido(
                estadoTodosLosPedidos = EstadoTodosLosPedidos.ENTREGADO
            )
            CardPedido(
                estadoTodosLosPedidos = EstadoTodosLosPedidos.ANULADO
            )



        }
    }
}
@Composable
private fun CardPedido(
    estadoTodosLosPedidos: EstadoTodosLosPedidos
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
                color = Color(0xFF1C1C1E),
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
                text = "Mesa 4 • #0234",
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
            text = "Mesero: Jose P. • 3 items",
            color = Color.Gray,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "2:14 pm",
                color = Color.Gray,
                fontSize = 15.sp,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "S/. 80.24",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
