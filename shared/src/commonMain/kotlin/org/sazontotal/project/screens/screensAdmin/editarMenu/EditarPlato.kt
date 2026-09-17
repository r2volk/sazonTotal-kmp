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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
import org.sazontotal.project.components.CampoTextoAdmin
import org.sazontotal.project.components.FiltroButton
import org.sazontotal.project.components.FotoPlatoBanner
import org.sazontotal.project.components.SwitchButton

@Composable
@Preview
fun EditarPlato() {
    // Solo diseño (valores de muestra, sin lógica)
    var nombre by remember { mutableStateOf("Ceviche clásico") }
    var categoria by remember { mutableStateOf("Platos") }
    var precio by remember { mutableStateOf("S/ 12.00") }
    var descripcion by remember { mutableStateOf("Pescado fresco marinado en limón, cebolla y ají...") }
    var disponible by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(horizontal = 20.dp)
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        Row(){
            Text(
                text = "Editar plato",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f).padding(top = 10.dp)
            )
        }

        // Foto
        FotoPlatoBanner(onCameraClick = {})

        // Nombre
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Nombre del plato",
                color = Color(0xFF8E8E93),
                fontSize = 14.sp
            )
            CampoTextoAdmin(
                valor = nombre,
                onValorChange = { nombre = it }
            )
        }

        // Categoría + Precio
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
                    color = Color(0xFF8E8E93),
                    fontSize = 14.sp
                )
                CampoTextoAdmin(
                    valor = categoria,
                    onValorChange = { categoria = it },
                    readOnly = true,
                    trailing = {
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowDown,
                            contentDescription = null,
                            tint = Color(0xFF8E8E93),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Precio",
                    color = Color(0xFF8E8E93),
                    fontSize = 14.sp
                )
                CampoTextoAdmin(
                    valor = precio,
                    onValorChange = { precio = it }
                )
            }
        }

        // Descripción
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Descripción",
                color = Color(0xFF8E8E93),
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

        // Etiquetas
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Etiquetas",
                color = Color(0xFF8E8E93),
                fontSize = 14.sp
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FiltroButton(
                    texto = "Sin Gluten",
                    activo = true,
                    onClick = {},
                    textoColor = Color.Green,
                    fondoTextoColor = Color(0xFF0D3B20)
                )
                FiltroButton(
                    texto = "Picante",
                    activo = false,
                    onClick = {},
                    textoColor = Color.Green,
                    fondoTextoColor = Color(0xFF0D3B20)
                )
                FiltroButton(
                    texto = "Lacteo",
                    activo = false,
                    onClick = {},
                    textoColor = Color.Green,
                    fondoTextoColor = Color(0xFF0D3B20)
                )
            }
        }

        // Disponible
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1C1C1E))
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
            SwitchButton(isActive = disponible)
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Botones inferiores (en flujo, ya no overlay)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFF3A1A1A), RoundedCornerShape(12.dp))
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Eliminar",
                    color = Color(0xFFE5484D),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Box(
                modifier = Modifier
                    .weight(1.6f)
                    .height(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF5DF08A))
                    .clickable { },
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
