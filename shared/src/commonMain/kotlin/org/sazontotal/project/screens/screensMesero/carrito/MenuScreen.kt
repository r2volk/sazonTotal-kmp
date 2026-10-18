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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import org.sazontotal.project.components.visualDeCategoria
import org.sazontotal.project.data.CarritoCompra
import androidx.compose.runtime.collectAsState
import org.sazontotal.project.data.repository.SazonRepository
import org.sazontotal.project.utils.moneda
import org.sazontotal.project.theme.Colores

@Composable
fun MenuScreen(
    backMenuScreen: () -> Unit,
    verCarrito: () -> Unit,
    repositorio: SazonRepository? = null,
    carrito: CarritoCompra? = null,
    mesa: String = "4",
    onMesaChange: (String) -> Unit = {},
    nombreMesero: String = "José P."
) {
    var textoBusqueda by remember { mutableStateOf("") }
    var filtroCategoria by remember { mutableStateOf("Todos") }

    val platosEnVivo = repositorio?.observarPlatos()
    val platos = platosEnVivo?.collectAsState(initial = emptyList())?.value ?: emptyList()

    val porNombre = platos.filter { plato ->
        textoBusqueda.isBlank() || plato.nombre.contains(textoBusqueda, ignoreCase = true)
    }

    val filtrados = porNombre.filter { plato ->
        filtroCategoria == "Todos" || plato.categoria == filtroCategoria
    }

    // menu desplegable de mesas (1 a 10). la elegida viaja con el pedido.
    var mostrarMesas by remember { mutableStateOf(false) }

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
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = {
                    backMenuScreen()
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Menú",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Box {
                    Text(
                        text = "Mesa $mesa · $nombreMesero ▾",
                        color = Colores.TextoTenue,
                        fontSize = 13.sp,
                        modifier = Modifier.clickable { mostrarMesas = true }
                    )
                    DropdownMenu(
                        expanded = mostrarMesas,
                        onDismissRequest = { mostrarMesas = false }
                    ) {
                        (1..10).forEach { numero ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "Mesa $numero",
                                        color = Colores.Superficie,
                                        fontSize = 15.sp
                                    )
                                },
                                onClick = {
                                    onMesaChange("$numero")
                                    mostrarMesas = false
                                }
                            )
                        }
                    }
                }
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
                texto = "Todos",
                activo = filtroCategoria == "Todos",
                onClick = { filtroCategoria = "Todos" },
                textoColor = Color.Green,
                fondoTextoColor = Colores.VerdeOscuro
            )
            FiltroButton(
                texto = "Platos",
                activo = filtroCategoria == "Platos",
                onClick = { filtroCategoria = "Platos" },
                textoColor = Color.Green,
                fondoTextoColor = Colores.VerdeOscuro
            )
            FiltroButton(
                texto = "Bebidas",
                activo = filtroCategoria == "Bebidas",
                onClick = { filtroCategoria = "Bebidas" },
                textoColor = Color.Green,
                fondoTextoColor = Colores.VerdeOscuro
            )
            FiltroButton(
                texto = "Postres",
                activo = filtroCategoria == "Postres",
                onClick = { filtroCategoria = "Postres" },
                textoColor = Color.Green,
                fondoTextoColor = Colores.VerdeOscuro
            )
        }

        // grid con scroll (6 cards para probar, se ven ~4 por vez)
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val filas = filtrados.chunked(2)
            filas.forEach { fila ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    fila.forEach { plato ->
                        val visual = visualDeCategoria(plato.categoria)
                        PlatoCard(
                            nombre = plato.nombre,
                            etiqueta = plato.etiquetas.ifBlank { plato.descripcion },
                            precio = moneda(plato.precio),
                            icono = visual.icono,
                            fondoImagen = visual.fondo,
                            tintaIcono = visual.tinta,
                            cantidad = carrito?.cantidadDe(plato.id) ?: 0,
                            onMinus = { carrito?.restar(plato.id) },
                            onPlus = { carrito?.sumar(plato.id) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (fila.size == 1) {
                        Box(modifier = Modifier.weight(1f))
                    }
                }
            }

        }

        // barra inferior del carrito
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Colores.Superficie)
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // cuadritos segun lo que lleva el carrito: 1, 2 o 3 como maximo.
            // si hay mas de 3, se queda en 3.
            val cuadritos = minOf(carrito?.totalItems() ?: 0, 3)
            val coloresCuadrito = listOf(Color(0xFF4A2620), Color(0xFF5A4A12), Color(0xFF0B3820))
            repeat(cuadritos) { indice ->
                Box(
                    modifier = Modifier
                        .then(if (indice > 0) Modifier.offset(x = (-10).dp) else Modifier)
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(coloresCuadrito[indice])
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
            ) {
                Text(
                    text = "${carrito?.totalItems() ?: 0} ítems",
                    color = Colores.TextoTenue,
                    fontSize = 13.sp
                )
                Text(
                    text = moneda(carrito?.let { it.subtotal(platos) } ?: 0.0),
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
                    .background(Colores.VerdeBoton)
                    .clickable { verCarrito() },
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
    cantidad: Int,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Colores.Superficie)
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
                color = Colores.TextoTenue,
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
                        .background(Colores.VerdePrimario)
                        .clickable { onPlus() },
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
                        .background(Colores.GrisMedio)
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "−",
                        color = Colores.TextoTenue,
                        fontSize = 18.sp,
                        modifier = Modifier.clickable { onMinus() }
                    )
                    Text(
                        text = "$cantidad",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "+",
                        color = Colores.VerdePrimario,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onPlus() }
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun MenuScreenPreview(){
    MenuScreen(
        backMenuScreen = {},
        verCarrito = {}
    )
}