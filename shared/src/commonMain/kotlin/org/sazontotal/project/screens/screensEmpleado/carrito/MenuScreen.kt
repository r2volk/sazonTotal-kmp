package org.sazontotal.project.screens.screensEmpleado.carrito

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.Cake
import androidx.compose.material.icons.outlined.DinnerDining
import androidx.compose.material.icons.outlined.LocalBar
import androidx.compose.material.icons.outlined.SoupKitchen
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.sazontotal.project.components.BuscadorTextField
import org.sazontotal.project.components.FiltroButton

@Composable
@Preview
fun MenuScreen() {
    var textoBusqueda by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(start = 20.dp, end = 20.dp, top = 60.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Menú",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Mesa 4 · José P.",
                    color = Color(0xFF8E8E93),
                    fontSize = 13.sp
                )
            }
            IconButton(
                onClick = { },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Tune,
                    contentDescription = "Filtros",
                    tint = Color(0xFF8E8E93),
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        BuscadorTextField(
            nombreBuscado = textoBusqueda,
            onNombreChanged = { textoBusqueda = it },
            texto = "Buscar platos o bebidas"
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FiltroButton(
                texto = "Platos",
                activo = true,
                onClick = {},
                textoColor = Color(0xFF30D158),
                fondoTextoColor = Color(0xFF0D3B20)
            )
            FiltroButton(
                texto = "Bebidas",
                activo = false,
                onClick = {}
            )
            FiltroButton(
                texto = "Postres",
                activo = false,
                onClick = {}
            )
        }

        // Grid con scroll (6 cards para probar, se ven ~4 por vez)
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PlatoCard(
                    nombre = "Ceviche clásico",
                    etiqueta = "Sin gluten",
                    precio = "S/ 12.00",
                    icono = Icons.Outlined.SoupKitchen,
                    fondoImagen = Color(0xFF4A2620),
                    tintaIcono = Color(0xFFE8935A),
                    badge = "Más pedido",
                    modifier = Modifier.weight(1f)
                )
                PlatoCard(
                    nombre = "Lomo saltado",
                    etiqueta = "Picante leve",
                    precio = "S/ 18.00",
                    icono = Icons.Outlined.DinnerDining,
                    fondoImagen = Color(0xFF3D2E0A),
                    tintaIcono = Color(0xFFFFB340),
                    cantidadInicial = 2,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PlatoCard(
                    nombre = "Pisco sour",
                    etiqueta = "Con alcohol",
                    precio = "S/ 20.00",
                    icono = Icons.Outlined.LocalBar,
                    fondoImagen = Color(0xFF0B3820),
                    tintaIcono = Color(0xFF4ADE80),
                    modifier = Modifier.weight(1f)
                )
                PlatoCard(
                    nombre = "Suspiro limeño",
                    etiqueta = "Contiene lácteos",
                    precio = "S/ 13.00",
                    icono = Icons.Outlined.Cake,
                    fondoImagen = Color(0xFF3D1A2E),
                    tintaIcono = Color(0xFFF48FB1),
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PlatoCard(
                    nombre = "Ceviche clásico",
                    etiqueta = "Sin gluten",
                    precio = "S/ 12.00",
                    icono = Icons.Outlined.SoupKitchen,
                    fondoImagen = Color(0xFF4A2620),
                    tintaIcono = Color(0xFFE8935A),
                    modifier = Modifier.weight(1f)
                )
                PlatoCard(
                    nombre = "Lomo saltado",
                    etiqueta = "Picante leve",
                    precio = "S/ 18.00",
                    icono = Icons.Outlined.DinnerDining,
                    fondoImagen = Color(0xFF3D2E0A),
                    tintaIcono = Color(0xFFFFB340),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Barra inferior del carrito
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF1C1C1E))
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF4A2620))
            )
            Box(
                modifier = Modifier
                    .offset(x = (-10).dp)
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF5A4A12))
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
            ) {
                Text(
                    text = "3 ítems",
                    color = Color(0xFF8E8E93),
                    fontSize = 13.sp
                )
                Text(
                    text = "S/ 44.00",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Box(
                modifier = Modifier
                    .width(110.dp)
                    .height(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF5DBF3E))
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Ver carrito",
                    color = Color.Black,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun PlatoCard(
    nombre: String,
    etiqueta: String,
    precio: String,
    icono: ImageVector,
    fondoImagen: Color,
    tintaIcono: Color,
    modifier: Modifier = Modifier,
    badge: String? = null,
    cantidadInicial: Int = 0
) {
    var cantidad by remember { mutableStateOf(cantidadInicial) }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1C1C1E))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(fondoImagen),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = tintaIcono,
                modifier = Modifier.size(32.dp)
            )
            if (badge != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF2A1F08))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = badge,
                        color = Color(0xFFFFB340),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = nombre,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = etiqueta,
                color = Color(0xFF8E8E93),
                fontSize = 12.sp
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = precio,
                color = Color(0xFFB0B0B0),
                fontSize = 15.sp,
                modifier = Modifier.weight(1f)
            )
            if (cantidad == 0) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF30D158))
                        .clickable { cantidad++ },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+",
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF2C2C2E))
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "−",
                        color = Color(0xFF8E8E93),
                        fontSize = 18.sp,
                        modifier = Modifier.clickable { if (cantidad > 0) cantidad-- }
                    )
                    Text(
                        text = "$cantidad",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "+",
                        color = Color(0xFF30D158),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { cantidad++ }
                    )
                }
            }
        }
    }
}
