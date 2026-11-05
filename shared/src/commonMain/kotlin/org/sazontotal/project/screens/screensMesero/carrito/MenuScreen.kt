package org.sazontotal.project.screens.screensMesero.carrito

import org.sazontotal.project.theme.LocalTemaOscuro
import org.sazontotal.project.theme.UsoColor
import org.sazontotal.project.theme.colorDeTema
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
    nombreMesero: String = "José P.",
    deliveryFee: Double = 0.0,
    onHistorialReparto: () -> Unit = {}
) {
    val temaOscuro = LocalTemaOscuro.current
    var textoBusqueda by remember { mutableStateOf("") }
    var filtroCategoria by remember { mutableStateOf("Todos") }

    // el menu consulta solo platos activos; cantidades y mesa se comparten en memoria.
    val platosEnVivo = repositorio?.observarPlatos()
    val platos = platosEnVivo?.collectAsState(initial = emptyList())?.value ?: emptyList()

    val porNombre = platos.filter { plato ->
        textoBusqueda.isBlank() || plato.nombre.contains(textoBusqueda, ignoreCase = true)
    }

    val filtrados = porNombre.filter { plato ->
        filtroCategoria == "Todos" || plato.categoria == filtroCategoria
    }

    // la mesa elegida se guarda en el estado compartido de App.
    var mostrarMesas by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorDeTema(Color.Black, temaOscuro, UsoColor.FONDO))
            .padding(start = 20.dp, end = 20.dp, top = 60.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
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
                    tint = colorDeTema(Color.White, temaOscuro, UsoColor.TEXTO),
                    modifier = Modifier.size(22.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Menú",
                    color = colorDeTema(Color.White, temaOscuro, UsoColor.TEXTO),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Box {
                    Text(
                        text = "${if (mesa == "Delivery") "Delivery" else "Mesa $mesa"} · $nombreMesero ▾",
                        color = colorDeTema(Colores.TextoTenue, temaOscuro, UsoColor.TEXTO),
                        fontSize = 13.sp,
                        modifier = Modifier.clickable { mostrarMesas = true }
                    )
                    DropdownMenu(
                        expanded = mostrarMesas,
                        onDismissRequest = { mostrarMesas = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Delivery", color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface) },
                            onClick = { onMesaChange("Delivery"); mostrarMesas = false }
                        )
                        (1..15).forEach { numero ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "Mesa $numero",
                                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
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
            if (mesa == "Delivery") {
                Text("Entregas anteriores", color = colorDeTema(Colores.VerdePrimario, temaOscuro, UsoColor.TEXTO), fontSize = 12.sp,
                    modifier = Modifier.clickable { onHistorialReparto() })
            }
        }

        BuscadorTextField(
            nombreBuscado = textoBusqueda,
            onNombreChanged = { textoBusqueda = it },
            texto = "Buscar platos o bebidas"
        )

        // los 4 filtros no caben en un movil de 360dp, por eso hay scroll horizontal.
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
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
                            onPlus = { carrito?.sumar(plato) },
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
                .background(colorDeTema(Colores.Superficie, temaOscuro, UsoColor.FONDO))
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // un cuadrito por item del carrito, tope 3.
            val cuadritos = minOf(carrito?.totalItems() ?: 0, 3)
            val coloresCuadrito = listOf(Color(0xFF4A2620), Color(0xFF5A4A12), Color(0xFF0B3820))
            repeat(cuadritos) { indice ->
                Box(
                    modifier = Modifier
                        .then(if (indice > 0) Modifier.offset(x = (-10).dp) else Modifier)
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(colorDeTema(coloresCuadrito[indice], temaOscuro, UsoColor.FONDO))
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
            ) {
                Text(
                    text = "${carrito?.totalItems() ?: 0} ítems",
                    color = colorDeTema(Colores.TextoTenue, temaOscuro, UsoColor.TEXTO),
                    fontSize = 13.sp
                )
                Text(
                    text = moneda((carrito?.total() ?: 0.0) + if (mesa == "Delivery") deliveryFee else 0.0),
                    color = colorDeTema(Color.White, temaOscuro, UsoColor.TEXTO),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                if (mesa == "Delivery" && deliveryFee > 0.0) {
                    Text(
                        text = "Incluye ${moneda(deliveryFee)} de delivery",
                        color = colorDeTema(Colores.TextoTenue, temaOscuro, UsoColor.TEXTO),
                        fontSize = 11.sp
                    )
                }
            }
            Box(
                modifier = Modifier
                    .width(110.dp)
                    .height(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colorDeTema(Colores.VerdeBoton, temaOscuro, UsoColor.FONDO))
                    .clickable { verCarrito() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Ver carrito",
                    color = colorDeTema(Color.Black, temaOscuro, UsoColor.TEXTO),
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
    val temaOscuro = LocalTemaOscuro.current
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(colorDeTema(Colores.Superficie, temaOscuro, UsoColor.FONDO))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(colorDeTema(fondoImagen, temaOscuro, UsoColor.FONDO)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = colorDeTema(tintaIcono, temaOscuro, UsoColor.TEXTO),
                modifier = Modifier.size(32.dp)
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = nombre,
                color = colorDeTema(Color.White, temaOscuro, UsoColor.TEXTO),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = etiqueta,
                color = colorDeTema(Colores.TextoTenue, temaOscuro, UsoColor.TEXTO),
                fontSize = 12.sp
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = precio,
                color = colorDeTema(Color(0xFFB0B0B0), temaOscuro, UsoColor.TEXTO),
                fontSize = 15.sp,
                modifier = Modifier.weight(1f)
            )
            if (cantidad == 0) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(colorDeTema(Colores.VerdePrimario, temaOscuro, UsoColor.FONDO))
                        .clickable { onPlus() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+",
                        color = colorDeTema(Color.Black, temaOscuro, UsoColor.TEXTO),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(colorDeTema(Colores.GrisMedio, temaOscuro, UsoColor.FONDO))
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "−",
                        color = colorDeTema(Colores.TextoTenue, temaOscuro, UsoColor.TEXTO),
                        fontSize = 18.sp,
                        modifier = Modifier.clickable { onMinus() }
                    )
                    Text(
                        text = "$cantidad",
                        color = colorDeTema(Color.White, temaOscuro, UsoColor.TEXTO),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "+",
                        color = colorDeTema(Colores.VerdePrimario, temaOscuro, UsoColor.TEXTO),
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
    val temaOscuro = LocalTemaOscuro.current
    MenuScreen(
        backMenuScreen = {},
        verCarrito = {}
    )
}
