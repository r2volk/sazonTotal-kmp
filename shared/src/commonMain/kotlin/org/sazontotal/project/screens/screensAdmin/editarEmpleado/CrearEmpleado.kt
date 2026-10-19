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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.sazontotal.project.components.CampoTextoAdmin
import org.sazontotal.project.components.FotoEmpleadoAvatar
import org.sazontotal.project.components.RolOptionButton
import org.sazontotal.project.components.SwitchButton
import org.sazontotal.project.data.db.EmpleadoEntity
import org.sazontotal.project.data.db.esPinEmpleadoValido
import org.sazontotal.project.theme.Colores
import org.sazontotal.project.utils.ahoraMillis

@Composable
@Preview
fun CrearEmpleado(
    onGuardar: (EmpleadoEntity) -> Unit = {}
) {

    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var rol by remember { mutableStateOf("Mesero") }
    var activo by remember { mutableStateOf(true) }
    // Aviso cuando falta algo (antes el boton no hacia nada y parecia congelado).
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(horizontal = 20.dp)
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // header
        Row(){
            Text(
                text = "Nuevo Empleado",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f).padding(top = 10.dp)
            )
        }

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            FotoEmpleadoAvatar(onClick = {})
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Nombre completo",
                color = Colores.TextoTenue,
                fontSize = 14.sp
            )
            CampoTextoAdmin(
                valor = nombre,
                onValorChange = { nombre = it },
                placeholder = "Ej. Ana Gómez"
            )
        }

        // Telefono (el campo "Fecha de ingreso" se elimino: nunca se guardaba)
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Teléfono",
                color = Colores.TextoTenue,
                fontSize = 14.sp
            )
            CampoTextoAdmin(
                valor = telefono,
                onValorChange = { telefono = it },
                placeholder = "999 000 000"
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Rol",
                color = Colores.TextoTenue,
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
                RolOptionButton(
                    texto = "Admin",
                    seleccionado = rol == "Administrador",
                    onClick = { rol = "Administrador" },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "PIN inicial (4 dígitos)",
                color = Colores.TextoTenue,
                fontSize = 14.sp
            )
            CampoTextoAdmin(
                valor = pin,
                onValorChange = { texto ->
                    pin = texto.filter { it in '0'..'9' }.take(4)
                    error = null
                },
                placeholder = "● ● ● ●",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
            )
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
                text = "Empleado activo",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            SwitchButton(isActive = activo, onCambio = {activo = it})
        }

        Spacer(modifier = Modifier.height(28.dp))

// Aviso de validacion (solo aparece si falta algo).
        if (error != null) {
            Text(
                text = error!!,
                color = Color(0xFFFF453A),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // boton guardar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF5DF08A))
                .clickable {
                    // Se revisa todo y se avisa que falta: el boton nunca
                    // se queda "sin hacer nada" como si la app estuviera congelada.
                    val faltaNombre = nombre.isBlank()
                    val faltaPin = pin.isBlank()
                    val pinInvalido = pin.isNotBlank() && !esPinEmpleadoValido(pin)
                    error = when {
                        faltaNombre -> "Escribe el nombre del empleado"
                        faltaPin -> "Escribe el PIN de 4 dígitos"
                        pinInvalido -> "El PIN debe tener exactamente 4 dígitos numéricos"
                        else -> null
                    }
                    if (error == null) {
                        // el prefijo sale del rol, y el numero de la hora
                        // (mas unico que random)
                        val prefijo = when (rol) {
                            "Administrador" -> "ADM"
                            "Mesero" -> "MSR"
                            else -> "CCN"
                        }
                        onGuardar(
                            EmpleadoEntity(
                                id = "$prefijo-${ahoraMillis() % 100000}",
                                nombre = nombre.trim(),
                                pin = pin,
                                rol = rol,
                                telefono = telefono,
                                activo = activo
                            )
                        )
                    }
                }
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
