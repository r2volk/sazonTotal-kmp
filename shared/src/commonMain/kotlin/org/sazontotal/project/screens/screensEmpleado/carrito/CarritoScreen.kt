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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.ConfirmationNumber
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.sazontotal.project.components.CampoTextoAdmin
import org.sazontotal.project.components.CantidadStepper

@Composable
@Preview
fun CarritoScreen() {
    var cantidadCeviche by remember { mutableStateOf(1) }
    var cantidadLomo by remember { mutableStateOf(2) }
    var cantidadPisco by remember { mutableStateOf(1) }
    var codigoPromo by remember { mutableStateOf("") }

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
                text = "Carrito · Mesa 4",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
        }

        // Items con scroll
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CarritoItemCard(
                nombre = "Ceviche clásico",
                precioUnitario = "S/ 12.00 c/u",
                icono = Icons.Outlined.SoupKitchen,
                fondoImagen = Color(0xFF4A2620),
                tintaIcono = Color(0xFFE8935A),
                cantidad = cantidadCeviche,
                onMinus = { if (cantidadCeviche > 0) cantidadCeviche-- },
                onPlus = { cantidadCeviche++ },
                mostrarAgregarNota = true
            )
            CarritoItemCard(
                nombre = "Lomo saltado",
                precioUnitario = "S/ 18.00 c/u",
                icono = Icons.Outlined.DinnerDining,
                fondoImagen = Color(0xFF3D2E0A),
                tintaIcono = Color(0xFFFFB340),
                cantidad = cantidadLomo,
                onMinus = { if (cantidadLomo > 0) cantidadLomo-- },
                onPlus = { cantidadLomo++ },
                nota = "Nota: sin cebolla"
            )
            CarritoItemCard(
                nombre = "Pisco sour",
                precioUnitario = "S/ 20.00 c/u",
                icono = Icons.Outlined.LocalBar,
                fondoImagen = Color(0xFF0B3820),
                tintaIcono = Color(0xFF4ADE80),
                cantidad = cantidadPisco,
                onMinus = { if (cantidadPisco > 0) cantidadPisco-- },
                onPlus = { cantidadPisco++ }
            )


        }

        // Código de promoción
        CampoTextoAdmin(
            valor = codigoPromo,
            onValorChange = { codigoPromo = it },
            placeholder = "Código de promoción",
            leading = {
                Icon(
                    imageVector = Icons.Outlined.ConfirmationNumber,
                    contentDescription = null,
                    tint = Color(0xFF8E8E93),
                    modifier = Modifier.size(20.dp)
                )
            },
            trailing = {
                Text(
                    text = "Aplicar",
                    color = Color(0xFF30D158),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clickable { }
                )
            }
        )

        // Totales
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Subtotal",
                    color = Color(0xFF8E8E93),
                    fontSize = 15.sp,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "S/ 68.00",
                    color = Color.White,
                    fontSize = 15.sp
                )
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "IGV (18%)",
                    color = Color(0xFF8E8E93),
                    fontSize = 15.sp,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "S/ 12.24",
                    color = Color.White,
                    fontSize = 15.sp
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "S/ 80.24",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Confirmar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF5DBF3E))
                .clickable { },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Confirmar y enviar a cocina",
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun CarritoItemCard(
    nombre: String,
    precioUnitario: String,
    icono: ImageVector,
    fondoImagen: Color,
    tintaIcono: Color,
    cantidad: Int,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
    modifier: Modifier = Modifier,
    nota: String? = null,
    mostrarAgregarNota: Boolean = false
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1C1C1E))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(fondoImagen),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = tintaIcono,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = nombre,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = precioUnitario,
                    color = Color(0xFF8E8E93),
                    fontSize = 13.sp
                )
            }
            CantidadStepper(
                cantidad = cantidad,
                onMinus = onMinus,
                onPlus = onPlus
            )
        }
        if (nota != null) {
            Text(
                text = nota,
                color = Color(0xFF8E8E93),
                fontSize = 13.sp,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
        }
        if (mostrarAgregarNota) {
            Text(
                text = "+ Agregar nota (ej. sin cebolla)",
                color = Color(0xFF8E8E93),
                fontSize = 13.sp,
                modifier = Modifier.clickable { }
            )
        }
    }
}
