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
import sazontotal.shared.generated.resources.Res

@Composable
fun LoginScreen( onLogin:(String, String)-> Unit)  {

    var empleadoId by remember { mutableStateOf("EMP-104") }
    var pin by remember { mutableStateOf("12") }

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

            // header (titulo y subtitulo)
            Text(
                text = "Login de mesero",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Ingresa tu ID y PIN",
                fontSize = 14.sp,
                color = Color(0xFF8E8E93),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(26.dp))

            // textField del empleado
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
                    color = Color(0xFF8E8E93)
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
                    cursorBrush = SolidColor(Color(0xFF22C55E)),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // indicador pin
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
                            .background(if (isFilled) Color(0xFF22C55E) else Color(0xFF2E2E32))
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // pad numerico
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // row 1, 2, 3
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PinButton(number = 1, onClick = onNumberClick, modifier = Modifier.weight(1f))
                    PinButton(number = 2, onClick = onNumberClick, modifier = Modifier.weight(1f))
                    PinButton(number = 3, onClick = onNumberClick, modifier = Modifier.weight(1f))
                }

                // row 4, 5, 6
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PinButton(number = 4, onClick = onNumberClick, modifier = Modifier.weight(1f))
                    PinButton(number = 5, onClick = onNumberClick, modifier = Modifier.weight(1f))
                    PinButton(number = 6, onClick = onNumberClick, modifier = Modifier.weight(1f))
                }

                // row 7, 8, 9
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PinButton(number = 7, onClick = onNumberClick, modifier = Modifier.weight(1f))
                    PinButton(number = 8, onClick = onNumberClick, modifier = Modifier.weight(1f))
                    PinButton(number = 9, onClick = onNumberClick, modifier = Modifier.weight(1f))
                }

                // row 0, backspace
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // Botón aceptar / entrar
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1.05f)
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { onLogin(empleadoId, pin) },
                        contentAlignment = Alignment.Center
                    ) {
                        CheckIcon(
                            color = Color(0xFF22C55E),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    PinButton(
                        number = 0,
                        onClick = onNumberClick,
                        modifier = Modifier.weight(1f)
                    )

                    // Backspace button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1.05f)
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { onBackspaceClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        BackspaceIcon(
                            color = Color(0xFF8E8E93),
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