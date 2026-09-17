package org.sazontotal.project.screens.screensAdmin.editarEmpleado

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.DateRange
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
import org.sazontotal.project.components.FotoEmpleadoAvatar
import org.sazontotal.project.components.RolOptionButton
import org.sazontotal.project.components.SwitchButton

@Composable
@Preview
fun CrearEmpleado() {

    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var ingreso by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var rol by remember { mutableStateOf("Mesero") }
    var activo by remember { mutableStateOf(true) }

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
                text = "Nuevo Empleado",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f).padding(top = 10.dp)
            )
        }

        // Avatar
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            FotoEmpleadoAvatar(onClick = {})
        }

        // Nombre completo
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Nombre completo",
                color = Color(0xFF8E8E93),
                fontSize = 14.sp
            )
            CampoTextoAdmin(
                valor = nombre,
                onValorChange = { nombre = it },
                placeholder = "Ej. Ana Gómez"
            )
        }

        // Teléfono + Ingreso
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                modifier = Modifier.weight(1.15f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Teléfono",
                    color = Color(0xFF8E8E93),
                    fontSize = 14.sp
                )
                CampoTextoAdmin(
                    valor = telefono,
                    onValorChange = { telefono = it },
                    placeholder = "999 000 000"
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Ingreso",
                    color = Color(0xFF8E8E93),
                    fontSize = 14.sp
                )
                CampoTextoAdmin(
                    valor = ingreso,
                    onValorChange = { ingreso = it },
                    placeholder = "dd/mm/aa",
                    leading = {
                        Icon(
                            imageVector = Icons.Filled.DateRange,
                            contentDescription = null,
                            tint = Color(0xFF8E8E93),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
            }
        }

        // Rol
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Rol",
                color = Color(0xFF8E8E93),
                fontSize = 14.sp
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RolOptionButton(
                    texto = "Mesero",
                    seleccionado = rol == "Mesero",
                    onClick = { rol = "Mesero" },
                    modifier = Modifier.weight(1f)
                )
                RolOptionButton(
                    texto = "Cocina",
                    seleccionado = rol == "Cocina",
                    onClick = { rol = "Cocina" },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // PIN
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "PIN inicial (4 dígitos)",
                color = Color(0xFF8E8E93),
                fontSize = 14.sp
            )
            CampoTextoAdmin(
                valor = pin,
                onValorChange = { pin = it },
                placeholder = "● ● ● ●"
            )
        }

        // Empleado activo
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1C1C1E))
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Empleado activo",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            SwitchButton(isActive = activo)
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Botón guardar (flujo, listo para usarse dentro de AdminBottomSheet)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF5DF08A))
                .clickable { }
                .padding(bottom = 0.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Guardar empleado",
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
