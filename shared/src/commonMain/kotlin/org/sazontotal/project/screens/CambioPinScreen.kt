package org.sazontotal.project.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.sazontotal.project.components.CampoTextoAdmin
import org.sazontotal.project.data.db.EmpleadoEntity
import org.sazontotal.project.data.repository.SazonRepository
import org.sazontotal.project.theme.Colores

@Composable
fun CambioPinScreen(
    backCambioPinScreen: () -> Unit,
    empleado: EmpleadoEntity? = null,
    repositorio: SazonRepository? = null
) {

    val scope = rememberCoroutineScope()

    var pinActual by remember { mutableStateOf("") }
    var pinNuevo by remember { mutableStateOf("") }
    var pinConfirmar by remember { mutableStateOf("") }

    val guardando = remember { mutableStateOf(false) }
    val exito = remember { mutableStateOf(false) }
    val error = remember { mutableStateOf<String?>(null) }

    // al guardar con exito volvemos al dashboard tras mostrar la confirmacion.
    LaunchedEffect(exito.value) {
        if (exito.value) {
            delay(1500)
            backCambioPinScreen()
        }
    }

    // solo digitos, maximo 4, y el teclado del sistema es numerico para PIN.
    val tecladoPin = KeyboardOptions(
        keyboardType = KeyboardType.NumberPassword,
        imeAction = ImeAction.Next
    )
    val permitirEscribir = !guardando.value && !exito.value
    fun limpiar(valor: String): String = valor.filter { it.isDigit() }.take(4)

    fun guardar() {
        if (exito.value || guardando.value) return
        error.value = when {
            pinActual.length != 4 || pinNuevo.length != 4 || pinConfirmar.length != 4 ->
                "Completa los tres PINs (4 dígitos cada uno)"
            pinNuevo != pinConfirmar -> "Los PIN nuevos no coinciden"
            pinNuevo == pinActual -> "El nuevo PIN debe ser distinto al actual"
            else -> null
        }
        if (error.value != null) return

        val repositorioUso = repositorio
        val empleadoUso = empleado
        if (repositorioUso == null || empleadoUso == null) {
            error.value = "No hay sesión activa"
            return
        }

        guardando.value = true
        scope.launch {
            val logro = repositorioUso.cambiarPin(empleadoUso.id, pinActual, pinNuevo)
            guardando.value = false
            if (logro) {
                exito.value = true
            } else {
                error.value = "El PIN actual es incorrecto"
            }
        }
    }

    val enmascaramiento = PasswordVisualTransformation()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .navigationBarsPadding()
            .padding(start = 20.dp, end = 20.dp, top = 60.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(
                onClick = { backCambioPinScreen() },
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
                text = "Cambiar PIN",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
        }

        Text(
            text = "Ingresa tu PIN actual y elige uno nuevo de 4 dígitos.",
            color = Colores.TextoTenue,
            fontSize = 14.sp
        )

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "PIN actual",
                color = Colores.TextoTenue,
                fontSize = 14.sp
            )
            CampoTextoAdmin(
                valor = pinActual,
                onValorChange = {
                    if (permitirEscribir) {
                        error.value = null
                        pinActual = limpiar(it)
                    }
                },
                placeholder = "Tu PIN actual",
                keyboardOptions = tecladoPin,
                visualTransformation = enmascaramiento,
                trailing = {
                    Icon(
                        imageVector = Icons.Filled.VisibilityOff,
                        contentDescription = null,
                        tint = Colores.TextoTenue,
                        modifier = Modifier.size(20.dp)
                    )
                }
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Nuevo PIN",
                color = Colores.TextoTenue,
                fontSize = 14.sp
            )
            CampoTextoAdmin(
                valor = pinNuevo,
                onValorChange = {
                    if (permitirEscribir) {
                        error.value = null
                        pinNuevo = limpiar(it)
                    }
                },
                placeholder = "Nuevo PIN",
                borderColor = Colores.VerdePrimario,
                keyboardOptions = tecladoPin,
                visualTransformation = enmascaramiento,
                trailing = {
                    Icon(
                        imageVector = Icons.Filled.VisibilityOff,
                        contentDescription = null,
                        tint = Colores.TextoTenue,
                        modifier = Modifier.size(20.dp)
                    )
                }
            )
            Text(
                text = "Solo números, 4 dígitos",
                color = Colores.TextoTenue,
                fontSize = 13.sp
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Confirmar nuevo PIN",
                color = Colores.TextoTenue,
                fontSize = 14.sp
            )
            CampoTextoAdmin(
                valor = pinConfirmar,
                onValorChange = {
                    if (permitirEscribir) {
                        error.value = null
                        pinConfirmar = limpiar(it)
                    }
                },
                placeholder = "Repite el nuevo PIN",
                keyboardOptions = tecladoPin,
                visualTransformation = enmascaramiento,
                trailing = {
                    Icon(
                        imageVector = Icons.Filled.VisibilityOff,
                        contentDescription = null,
                        tint = Colores.TextoTenue,
                        modifier = Modifier.size(20.dp)
                    )
                }
            )
        }

        // mensaje de estado (error en rojo, exito en verde)
        cuandoMostrarEstado(exito.value, error.value)

        Spacer(modifier = Modifier.weight(1f))

        // boton: gris-verde apagado mientras esta incompleto, verde cuando esta listo
        val completo = pinActual.length == 4 && pinNuevo.length == 4 && pinConfirmar.length == 4
        val puedeGuardar = completo && !guardando.value && !exito.value
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (puedeGuardar) Colores.VerdePrimario else Color(0xFF1C2E22)
                )
                .clickable(enabled = puedeGuardar) { guardar() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = when {
                    exito.value -> "PIN guardado"
                    guardando.value -> "Guardando..."
                    else -> "Guardar nuevo PIN"
                },
                color = if (puedeGuardar) Color.Black else Color(0xFF3E6B4F),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun cuandoMostrarEstado(exito: Boolean, error: String?) {
    when {
        exito -> Text(
            text = "PIN actualizado con éxito",
            color = Colores.VerdePrimario,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        error != null -> Text(
            text = error,
            color = Colores.Rojo,
            fontSize = 14.sp
        )
    }
}

@Composable
@Preview
fun CambioPinScreenPreview(){
    CambioPinScreen(
        backCambioPinScreen = {}
    )
}
