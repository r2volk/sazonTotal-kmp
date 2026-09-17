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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import org.sazontotal.project.components.BuscadorTextField
import org.sazontotal.project.components.FiltroButton

@Composable
@Preview
fun EditarMenuScreen(){
    var textoBusqueda by remember { mutableStateOf("") }
    var cantidadPlatos by remember { mutableStateOf(18) }
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
                    text = "Editar Menú",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "$cantidadPlatos platos",
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
                    texto = "Platos",
                    activo = true,
                    onClick = {},
                    textoColor = Color.Green,
                    fondoTextoColor = Color(0xFF0D3B20)
                )
                FiltroButton(
                    texto = "Bebidas",
                    activo = false,
                    onClick = {},
                    textoColor = Color.Green,
                    fondoTextoColor = Color(0xFF0D3B20)
                )
                FiltroButton(
                    texto = "Postres",
                    activo = false,
                    onClick = {},
                    textoColor = Color.Green,
                    fondoTextoColor = Color(0xFF0D3B20)
                )
            }

            menuCard(
                nombrePlato = "Ceviche Clasico",
                isActive = true,
                precio = 12.0,
                descipcion = "Sin Gluten",
                editar = {}
            )
            menuCard(
                nombrePlato = "Lomo Saltado",
                isActive = true,
                precio = 18.0,
                descipcion = "Picante Leve",
                editar = {}
            )
            menuCard(
                nombrePlato = "Causa Limeña",
                isActive = false,
                precio = 14.0,
                descipcion = "Sin Gluten",
                editar = {}
            )
            menuCard(
                nombrePlato = "Suspiro Limeño",
                isActive = true,
                precio = 13.0,
                descipcion = "Contiene Lacteos",
                editar = {}
            )

        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 20.dp)
                .size(52.dp)
                .clip(CircleShape)
                .background(Color(0xFF5DBF3E))
                .clickable {  },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "+",
                color = Color.Black,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
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
    editar: () ->Unit
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

        // Switch
        Switch(
            checked = isActive,
            onCheckedChange = null,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xFF000000),
                checkedTrackColor = Color(0xFF30D158),

                uncheckedThumbColor = Color(0xFF000000),
                uncheckedTrackColor = Color(0xFF3A3A3C),

                checkedBorderColor = Color.Transparent,
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}