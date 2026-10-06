package org.sazontotal.project.screens.screensAdmin.editarMenu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.EmojiFoodBeverage
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.sazontotal.project.components.AdminBottomSheet
import org.sazontotal.project.components.BuscadorTextField
import org.sazontotal.project.components.FiltroButton
import org.sazontotal.project.components.SwitchButton
import org.sazontotal.project.data.repository.SazonRepository
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch


@Composable
fun EditarMenuScreen(
    backEditarMenu: () -> Unit,
    repositorio: SazonRepository? = null
){
    var textoBusqueda by remember { mutableStateOf("") }
    var filtroCategoria by remember { mutableStateOf("Todos") }

    // 1. Pedimos la lista viva de platos a la base
    // Si no hay base (vista previa), esto es nulo
    val platosEnVivo = repositorio?.observarTodosLosPlatos()

    // 2. Convertimos esa lista viva en una lista normal que la pantalla puede dibujar
    // Si no hay nada, empezamos con una lista vacía.
    val platos = platosEnVivo?.collectAsState(initial = emptyList())?.value ?: emptyList()

    // 3. Nos quedamos solo con los que pasan el buscador
    // Si el buscador está vacío, pasan todos
    val porNombre = platos.filter { plato ->
        textoBusqueda.isBlank() || plato.nombre.contains(textoBusqueda, ignoreCase = true)
    }

    // 4. De esos, nos quedamos con los de la categoría elegida
    // Si dice "Todos", pasan todos
    val filtrados = porNombre.filter { plato ->
        filtroCategoria == "Todos" || plato.categoria == filtroCategoria
    }

    var mostrarFormulario by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope() //evita que el app se congele al guardar un dato en la bd

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
        ){
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            )
            {
                IconButton(
                    onClick = {
                        backEditarMenu()
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
                    text = "Editar Menú",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${platos.size} platos",
                    color = Color.Gray,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            BuscadorTextField(
                nombreBuscado = textoBusqueda,
                onNombreChanged = { nuevoTexto ->
                    textoBusqueda = nuevoTexto
                },
                texto = "Buscar por mesa o mesero"
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            )
            {
                FiltroButton(
                    texto = "Todos",
                    activo = filtroCategoria == "Todos",
                    onClick = { filtroCategoria = "Todos" },
                    textoColor = Color.Green,
                    fondoTextoColor = Color(0xFF0D3B20)
                )
                FiltroButton(
                    texto = "Platos",
                    activo = filtroCategoria == "Platos",
                    onClick = { filtroCategoria = "Platos" },
                    textoColor = Color.Green,
                    fondoTextoColor = Color(0xFF0D3B20)
                )
                FiltroButton(
                    texto = "Bebidas",
                    activo = filtroCategoria == "Bebidas",
                    onClick = { filtroCategoria = "Bebidas" },
                    textoColor = Color.Green,
                    fondoTextoColor = Color(0xFF0D3B20)
                )
                FiltroButton(
                    texto = "Postres",
                    activo = filtroCategoria == "Postres",
                    onClick = { filtroCategoria = "Postres" },
                    textoColor = Color.Green,
                    fondoTextoColor = Color(0xFF0D3B20)
                )
            }

            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                filtrados.forEach { plato ->
                    menuCard(
                        nombrePlato = plato.nombre,
                        isActive = plato.activo,
                        precio = plato.precio,
                        descipcion = plato.descripcion,
                        editar = {},
                        onActivoChange = { prendido ->
                            scope.launch {
                                repositorio?.cambiarActivoPlato(plato.id, prendido)
                            }
                        }
                    )
                }
            }

        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 20.dp)
                .size(52.dp)
                .clip(CircleShape)
                .background(Color(0xFF5DBF3E))
                .clickable {
                    mostrarFormulario = true
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "+",
                color = Color.Black,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (mostrarFormulario){
            AdminBottomSheet(
                onDismiss = { mostrarFormulario = false }
            ) {
                EditarPlato(
                    onGuardar = { nuevoPlato ->
                        scope.launch {
                            repositorio?.guardarPlato(nuevoPlato)
                            mostrarFormulario = false
                        }
                    }
                )
            }
        }

    }
}


@Composable
private fun menuCard(
    nombrePlato: String,
    imagen: Painter? = null,
    isActive: Boolean,
    precio: Double,
    descipcion: String,
    editar: () -> Unit,
    onActivoChange: (Boolean) -> Unit
)
{
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1C1C1E))
            .padding(
                horizontal = 14.dp,
                vertical = 12.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Imagen / placeholder
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF4A2620)),
            contentAlignment = Alignment.Center
        ) {
            if (imagen != null) {
                androidx.compose.foundation.Image(
                    painter = imagen,
                    contentDescription = nombrePlato,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.EmojiFoodBeverage,
                    contentDescription = null,
                    tint = Color(0xFFE8935A),
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.size(12.dp))

        // Información
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = nombrePlato,
                color = if (isActive) {
                    Color(0xFFE8E8E8)
                } else {
                    Color(0xFF777777)
                },
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "S/ $precio •",
                    color = if (isActive) {
                        Color(0xFF5F5F5F)
                    } else {
                        Color(0xFF444444)
                    },
                    fontSize = 15.sp
                )

                Spacer(modifier = Modifier.size(6.dp))

                Text(
                    text = descipcion,
                    color = if (isActive) {
                        Color(0xFF5F5F5F)
                    } else {
                        Color(0xFF444444)
                    },
                    fontSize = 12.sp
                )
            }
        }

        // Botón editar
        IconButton(onClick = editar) {
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = "Editar",
                tint = Color(0xFF9A9A9A),
                modifier = Modifier.size(20.dp)
            )
        }

        SwitchButton(isActive = isActive, onCambio = onActivoChange)

    }
}

@Composable
@Preview
fun EditarMenuScreenPreview(){
    EditarMenuScreen(
        backEditarMenu = {}
    )
}