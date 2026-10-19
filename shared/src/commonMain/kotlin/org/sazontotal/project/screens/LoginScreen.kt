package org.sazontotal.project.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.sazontotal.project.App
import org.sazontotal.project.components.PinButton
import org.sazontotal.project.icons.BackspaceIcon
import org.sazontotal.project.icons.CheckIcon
import org.sazontotal.project.theme.Colores

@Composable
fun LoginScreen(
    onLogin:(String, String)-> Unit,
    mensajeError: String? = null
) {

    var empleadoId by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }

    // si llega un error, se limpia el PIN para reintentar desde cero.
    LaunchedEffect(mensajeError) {
        if (mensajeError != null) {
            pin = ""
        }
    }

    val onNumberClick: (Int) -> Unit = { number ->
        if (pin.length < 4) {
            pin += number.toString()
        }
    }

    val onBackspaceClick: () -> Unit = {
        if (pin.isNotEmpty()) {
            pin = pin.dropLast(1)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .safeDrawingPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 380.dp)
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "SazonTotal",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Inicia sesión para entrar",
                fontSize = 14.sp,
                color = Colores.TextoTenue,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(26.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E1E20), RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "ID de empleado",
                    fontSize = 12.sp,
                    color = Colores.TextoTenue
                )
                BasicTextField(
                    value = empleadoId,
                    onValueChange = { empleadoId = it },
                    singleLine = true,
                    textStyle = TextStyle(
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    cursorBrush = SolidColor(Colores.VerdeAcento),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(4) { index ->
                    val isFilled = index < pin.length
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(if (isFilled) Colores.VerdeAcento else Color(0xFF2E2E32))
                    )
                }
            }

            // mensaje de error (solo aparece cuando App avisa que fallo).
            if (mensajeError != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = mensajeError,
                    color = Colores.Rojo,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PinButton(number = 1, onClick = onNumberClick, modifier = Modifier.weight(1f))
                    PinButton(number = 2, onClick = onNumberClick, modifier = Modifier.weight(1f))
                    PinButton(number = 3, onClick = onNumberClick, modifier = Modifier.weight(1f))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PinButton(number = 4, onClick = onNumberClick, modifier = Modifier.weight(1f))
                    PinButton(number = 5, onClick = onNumberClick, modifier = Modifier.weight(1f))
                    PinButton(number = 6, onClick = onNumberClick, modifier = Modifier.weight(1f))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PinButton(number = 7, onClick = onNumberClick, modifier = Modifier.weight(1f))
                    PinButton(number = 8, onClick = onNumberClick, modifier = Modifier.weight(1f))
                    PinButton(number = 9, onClick = onNumberClick, modifier = Modifier.weight(1f))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1.05f)
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { onLogin(empleadoId, pin) },
                        contentAlignment = Alignment.Center
                    ) {
                        CheckIcon(
                            color = Colores.VerdeAcento,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    PinButton(
                        number = 0,
                        onClick = onNumberClick,
                        modifier = Modifier.weight(1f)
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1.05f)
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { onBackspaceClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        BackspaceIcon(
                            color = Colores.TextoTenue,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun LoginScreenPreview() {
    App()
}