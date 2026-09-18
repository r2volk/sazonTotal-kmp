package org.sazontotal.project.screens.screensEmpleado

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
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
import org.sazontotal.project.components.FiltroButton
import org.sazontotal.project.components.OpcionPerfilRow
import org.sazontotal.project.components.StatCardCentrado
import org.sazontotal.project.components.SwitchButton

@Composable
@Preview
fun PerfilScreen() {
    var notificaciones by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(start = 20.dp, end = 20.dp, top = 60.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Perfil",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        // Datos del usuario
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2C2C2E)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "MR",
                    color = Color(0xFF7DD3FC),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "María Rojas",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            FiltroButton(
                texto = "Cocina",
                activo = true,
                onClick = {},
                textoColor = Color(0xFF64A8F5),
                fondoTextoColor = Color(0xFF1A365D)
            )
            Text(
                text = "ID: EMP-108",
                color = Color(0xFF8E8E93),
                fontSize = 14.sp
            )
        }

        // Estadísticas
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCardCentrado(
                textoSuperior = "18",
                textoInferior = "Turnos",
                modifier = Modifier.weight(1f)
            )
            StatCardCentrado(
                textoSuperior = "231",
                textoInferior = "Pedidos",
                modifier = Modifier.weight(1f)
            )
            StatCardCentrado(
                textoSuperior = "8m",
                textoInferior = "Prom. prep.",
                colorSuperior = Color(0xFF30D158),
                modifier = Modifier.weight(1f)
            )
        }

        // Opciones
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OpcionPerfilRow(
                icono = Icons.Filled.Lock,
                texto = "Cambiar PIN",
                onClick = {},
                trailing = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color(0xFF8E8E93),
                        modifier = Modifier.size(20.dp)
                    )
                }
            )
            OpcionPerfilRow(
                icono = Icons.Filled.Notifications,
                texto = "Notificaciones",
                onClick = {},
                trailing = {
                    SwitchButton(isActive = notificaciones)
                }
            )
            OpcionPerfilRow(
                icono = Icons.Filled.Info,
                texto = "Ayuda y soporte",
                onClick = {},
                trailing = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color(0xFF8E8E93),
                        modifier = Modifier.size(20.dp)
                    )
                }
            )
        }

        // Cerrar sesión
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, Color(0xFF3A1A1A), RoundedCornerShape(12.dp))
                .clickable { },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = null,
                tint = Color(0xFFFF453A),
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "  Cerrar sesión",
                color = Color(0xFFFF453A),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
