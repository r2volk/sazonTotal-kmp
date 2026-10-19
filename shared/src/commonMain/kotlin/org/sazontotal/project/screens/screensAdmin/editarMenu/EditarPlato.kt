package org.sazontotal.project.screens.screensAdmin.editarMenu

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.ui.unit.DpOffset
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
import org.sazontotal.project.components.CampoTextoAdmin
import org.sazontotal.project.components.FiltroButton
import org.sazontotal.project.components.FotoPlatoBanner
import org.sazontotal.project.components.SwitchButton
import org.sazontotal.project.data.db.PlatoEntity
import org.sazontotal.project.theme.Colores

@Composable
@Preview
fun EditarPlato(
    plato: PlatoEntity? = null,
    onGuardar: (PlatoEntity) -> Unit = {},
    onEliminar: (Long) -> Unit = {}
) {
    // con plato el formulario arranca lleno (editar); con nulo, vacio (crear); al cambiar se reinicia.
    var nombre by remember(plato) { mutableStateOf(plato?.nombre ?: "") }
    var categoria by remember(plato) { mutableStateOf(plato?.categoria ?: "Platos") }
    var precio by remember(plato) { mutableStateOf(plato?.precio?.toString() ?: "") }
    var descripcion by remember(plato) { mutableStateOf(plato?.descripcion ?: "") }
    var disponible by remember(plato) { mutableStateOf(plato?.activo ?: true) }
    var mostrarCategorias by remember { mutableStateOf(false) }
    // aviso cuando falta algo o el precio no es un numero valido.
    var error by remember { mutableStateOf<String?>(null) }
    // etiquetas elegidas (pueden ser varias). al editar se marcan las guardadas.
    var etiquetasElegidas by remember(plato) {
        mutableStateOf(
            plato?.etiquetas
                ?.split(",")
                ?.map { it.trim() }
                ?.filter { it.isNotEmpty() }
                ?.toSet() ?: emptySet()
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(horizontal = 20.dp)
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(){
            Text(
                text = if (plato == null) "Nuevo plato" else "Editar plato",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f).padding(top = 10.dp)
            )
        }

        FotoPlatoBanner(onCameraClick = {})

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Nombre del plato",
                color = Colores.TextoTenue,
                fontSize = 14.sp
            )
            CampoTextoAdmin(
                valor = nombre,
                onValorChange = { nombre = it }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Categoría",
                    color = Colores.TextoTenue,
                    fontSize = 14.sp
                )
                Box(modifier = Modifier.fillMaxWidth()) {
                    CampoTextoAdmin(
                        valor = categoria,
                        onValorChange = { },
                        readOnly = true,
                        trailing = {
                            IconButton(onClick = { mostrarCategorias = !mostrarCategorias }) {
                                Icon(
                                    imageVector = Icons.Filled.KeyboardArrowDown,
                                    contentDescription = "Elegir categoría",
                                    tint = Colores.TextoTenue,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    )
                    // menu nativo: flota encima y se cierra al elegir o tocar fuera
                    DropdownMenu(
                        expanded = mostrarCategorias,
                        onDismissRequest = { mostrarCategorias = false },
                        offset = DpOffset(0.dp, 4.dp)
                    ) {
                        listOf("Platos", "Bebidas", "Postres").forEach { opcion ->
                            val elegida = opcion == categoria
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = opcion,
                                        color = Colores.Superficie,
                                        fontSize = 15.sp,
                                        fontWeight = if (elegida) FontWeight.Bold
                                        else FontWeight.Normal
                                    )
                                },
                                onClick = {
                                    categoria = opcion
                                    mostrarCategorias = false
                                },
                                leadingIcon = {
                                    if (elegida) {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = null,
                                            tint = Colores.VerdePrimario
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Precio (S/.)",
                    color = Colores.TextoTenue,
                    fontSize = 14.sp
                )
                CampoTextoAdmin(
                    valor = precio,
                    onValorChange = { precio = it }
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Descripción",
                color = Colores.TextoTenue,
                fontSize = 14.sp
            )
            CampoTextoAdmin(
                valor = descripcion,
                onValorChange = { descripcion = it },
                singleLine = false,
                minLines = 2,
                maxLines = 3
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Etiquetas",
                color = Colores.TextoTenue,
                fontSize = 14.sp
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf("Sin Gluten", "Picante", "Lácteo").forEach { opcion ->
                    FiltroButton(
                        texto = opcion,
                        activo = opcion in etiquetasElegidas,
                        onClick = {
                            etiquetasElegidas = if (opcion in etiquetasElegidas) {
                                etiquetasElegidas - opcion
                            } else {
                                etiquetasElegidas + opcion
                            }
                        },
                        textoColor = Color.Green,
                        fondoTextoColor = Colores.VerdeOscuro
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Colores.Superficie)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Disponible",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            SwitchButton(isActive = disponible, onCambio = {disponible = it})
        }

        Spacer(modifier = Modifier.height(4.dp))

        // aviso de validacion (solo aparece si falta algo o el precio no cuadra).
        if (error != null) {
            Text(
                text = error!!,
                color = Color(0xFFFF453A),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // eliminar solo existe al editar (al crear no hay nada que borrar).
            if (plato != null) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFF3A1A1A), RoundedCornerShape(12.dp))
                        .clickable { onEliminar(plato.id) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Eliminar",
                        color = Color(0xFFE5484D),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Box(
                modifier = Modifier
                    .weight(1.6f)
                    .height(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF5DF08A))
                    .clickable {
                        val precioNumero = precio
                            .trim()
                            .replace(",", ".")
                            .toDoubleOrNull()
                        error = when {
                            nombre.isBlank() -> "Escribe el nombre del plato"
                            precio.isBlank() -> "Escribe el precio"
                            precioNumero == null -> "El precio debe ser un número (ej: 12.50)"
                            precioNumero <= 0 -> "El precio debe ser mayor a 0"
                            else -> null
                        }
                        if (error == null) {
                            onGuardar(
                                PlatoEntity(
                                    // con el mismo id actualiza (REPLACE); con 0 crea uno nuevo.
                                    id = plato?.id ?: 0,
                                    nombre = nombre.trim(),
                                    precio = precioNumero ?: 0.0,
                                    descripcion = descripcion.trim(),
                                    categoria = categoria,
                                    activo = disponible,
                                    etiquetas = etiquetasElegidas.joinToString(",")
                                )
                            )
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Guardar cambios",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
