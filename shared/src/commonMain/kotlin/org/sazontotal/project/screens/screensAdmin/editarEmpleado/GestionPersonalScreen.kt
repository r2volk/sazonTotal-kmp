package org.sazontotal.project.screens.screensAdmin.editarEmpleado

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import org.sazontotal.project.components.AdminBottomSheet
import org.sazontotal.project.components.BuscadorTextField
import org.sazontotal.project.components.FiltroButton
import org.sazontotal.project.components.StatCardCentrado
import org.sazontotal.project.components.SwitchButton
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import org.sazontotal.project.data.repository.SazonRepository
import org.sazontotal.project.theme.Colores

@Composable
fun GestionPersonalScreen(
    backGestionPersonal: () -> Unit,
    repositorio: SazonRepository? = null
){

    var nombreBuscado by remember { mutableStateOf("") }
    var mostrarFormulario by remember { mutableStateOf(false) }
    var filtroRol by remember { mutableStateOf("Todos") }
    // aviso cuando se bloquea un cambio (ej: no se puede apagar al ultimo admin).
    var aviso by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    // room emite la lista y la pantalla recalcula contadores y filtros al cambiarla.
    val empleados = repositorio?.observarEmpleados()
        ?.collectAsState(initial = emptyList())?.value ?: emptyList()
    val total = empleados.size
    val activos = empleados.count { it.activo }
    val inactivos = total - activos


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)

    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 50.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(
                    onClick = { backGestionPersonal()},
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
                    text = "Gestionar personal",
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
            ) {
                StatCardCentrado(
                    textoSuperior = total.toString(),
                    textoInferior = "Total",
                    colorSuperior = Color.White,
                    modifier = Modifier.weight(1f)
                )
                StatCardCentrado(
                    textoSuperior = activos.toString(),
                    textoInferior = "Activos",
                    colorSuperior = Colores.VerdePrimario,
                    modifier = Modifier.weight(1f)
                )
                StatCardCentrado(
                    textoSuperior = inactivos.toString(),
                    textoInferior = "Inactivos",
                    colorSuperior = Colores.TextoTenue,
                    modifier = Modifier.weight(1f)
                )
            }
            BuscadorTextField(
                nombreBuscado = nombreBuscado,
                onNombreChanged = { nuevoTexto ->
                    nombreBuscado = nuevoTexto },
                texto = "Buscar por nombre"
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                FiltroButton(
                    texto = "Todos",
                    activo = filtroRol == "Todos",
                    onClick = { filtroRol = "Todos" }
                )
                FiltroButton(
                    texto = "Meseros",
                    activo = filtroRol == "Meseros",
                    onClick = { filtroRol = "Meseros" }
                )
                FiltroButton(
                    texto = "Cocina",
                    activo = filtroRol == "Cocina",
                    onClick = { filtroRol = "Cocina" }
                )
                FiltroButton(
                    texto = "Admin",
                    activo = filtroRol == "Admin",
                    onClick = { filtroRol = "Admin" }
                )
            }

            // aviso de cambio bloqueado (solo aparece cuando pasa).
            if (aviso != null) {
                Text(
                    text = aviso!!,
                    color = Color(0xFFFF453A),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val filtrados = empleados.filter { empleado ->
                    val coincideNombre = nombreBuscado.isBlank() ||
                        empleado.nombre.contains(nombreBuscado, ignoreCase = true)
                    val coincideRol = filtroRol == "Todos" ||
                        (filtroRol == "Meseros" && empleado.rol == "Mesero") ||
                        (filtroRol == "Cocina" && empleado.rol == "Cocina") ||
                        (filtroRol == "Admin" && empleado.rol == "Administrador")
                    coincideNombre && coincideRol
                }
                filtrados.forEach { empleado ->
                    EmpleadoCard(
                        nombre = empleado.nombre,
                        trabajo = empleado.rol,
                        isActive = empleado.activo,
                        id = empleado.id,
                        onActivoChange = { prendido ->
                            scope.launch {
                                // el repositorio protege al ultimo admin antes de actualizar la fila.
                                val sePudo = repositorio?.cambiarActivoEmpleadoSeguro(empleado.id, prendido)
                                // false = bloqueado (ultimo admin); el switch vuelve solo porque el estado sale de la base.
                                aviso = if (sePudo == false) {
                                    "No se puede desactivar: debe quedar al menos un admin activo"
                                } else {
                                    null
                                }
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
                .background(Colores.VerdeBoton)
                .clickable {
                    aviso = null
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
                CrearEmpleado(
                    onGuardar = { nuevo ->
                        scope.launch {
                            try {
                                val repo = repositorio
                                if (repo == null) {
                                    aviso = "No se pudo guardar el empleado en este momento"
                                } else {
                                    // el repositorio valida el empleado y el dao lo inserta en room.
                                    repo.guardarEmpleado(nuevo)
                                    aviso = null
                                }
                            } catch (e: Exception) {
                                aviso = e.message ?: "No se pudo guardar el empleado"
                            } finally {
                                mostrarFormulario = false
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun EmpleadoCard(
    nombre: String,
    isActive: Boolean,
    trabajo: String,
    id: String,
    modifier: Modifier = Modifier,
    onActivoChange: (Boolean) -> Unit
) {
    val isCocina = trabajo.equals("Cocina", ignoreCase = true)
    val badgeBg = when {
        !isActive -> Colores.GrisMedio
        isCocina -> Color(0xFF1A365D)
        else -> Color(0xFF143D2B)
    }
    val badgeText = when {
        !isActive -> Color(0xFF636366)
        isCocina -> Color(0xFF64A8F5)
        else -> Colores.VerdePrimario
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Colores.Superficie)
            .padding(
                horizontal = 14.dp,
                vertical = 12.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Colores.GrisMedio),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "ツ",
                color = Color(0xFF9A9A9A),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.size(12.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = nombre,
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
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(badgeBg)
                        .padding(
                            horizontal = 8.dp,
                            vertical = 2.dp
                        )
                ) {
                    Text(
                        text = trabajo,
                        color = badgeText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.size(6.dp))

                Text(
                    text = "ID: $id",
                    color = if (isActive) {
                        Color(0xFF5F5F5F)
                    } else {
                        Color(0xFF444444)
                    },
                    fontSize = 12.sp
                )
            }
        }

        SwitchButton(isActive = isActive, onCambio = onActivoChange)
    }
}


@Preview
@Composable
fun GestionPersonalScreenPreview(){
    GestionPersonalScreen(
        backGestionPersonal = {},
        repositorio = null
    )
}
