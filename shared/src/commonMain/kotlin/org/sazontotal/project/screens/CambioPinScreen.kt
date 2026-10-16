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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.VisibilityOff
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
import org.sazontotal.project.icons.BackspaceIcon

@Composable
fun CambioPinScreen(
    backCambioPinScreen: () -> Unit,
) {

    var pinActual by remember { mutableStateOf("● ● ● ●") }
    var pinNuevo by remember { mutableStateOf("● ●") }
    var pinConfirmar by remember { mutableStateOf("") }

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
                text = "Cambiar PIN",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
        }

        Text(
            text = "Ingresa tu PIN actual y elige uno nuevo de 4 dígitos.",
            color = Color(0xFF8E8E93),
            fontSize = 14.sp
        )

        // PIN actual
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "PIN actual",
                color = Color(0xFF8E8E93),
                fontSize = 14.sp
            )
            CampoTextoAdmin(
                valor = pinActual,
                onValorChange = { pinActual = it },
                trailing = {
                    Icon(
                        imageVector = Icons.Filled.VisibilityOff,
                        contentDescription = null,
                        tint = Color(0xFF8E8E93),
                        modifier = Modifier.size(20.dp)
                    )
                }
            )
        }

        // Nuevo PIN (enfocado)
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Nuevo PIN",
                color = Color(0xFF8E8E93),
                fontSize = 14.sp
            )
            CampoTextoAdmin(
                valor = pinNuevo,
                onValorChange = { pinNuevo = it },
                borderColor = Color(0xFF30D158),
                trailing = {
                    Icon(
                        imageVector = Icons.Filled.VisibilityOff,
                        contentDescription = null,
                        tint = Color(0xFF8E8E93),
                        modifier = Modifier.size(20.dp)
                    )
                }
            )
            Text(
                text = "Solo números, 4 dígitos",
                color = Color(0xFF8E8E93),
                fontSize = 13.sp
            )
        }

        // Confirmar
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Confirmar nuevo PIN",
                color = Color(0xFF8E8E93),
                fontSize = 14.sp
            )
            CampoTextoAdmin(
                valor = pinConfirmar,
                onValorChange = { pinConfirmar = it },
                placeholder = "Repite el nuevo PIN",
                trailing = {
                    Icon(
                        imageVector = Icons.Filled.VisibilityOff,
                        contentDescription = null,
                        tint = Color(0xFF8E8E93),
                        modifier = Modifier.size(20.dp)
                    )
                }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Botón deshabilitado (PIN incompleto)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1C2E22)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Guardar nuevo PIN",
                color = Color(0xFF3E6B4F),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Teclado numérico
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                NumKey(numero = "1", modifier = Modifier.weight(1f))
                NumKey(numero = "2", modifier = Modifier.weight(1f))
                NumKey(numero = "3", modifier = Modifier.weight(1f))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                NumKey(numero = "4", modifier = Modifier.weight(1f))
                NumKey(numero = "5", modifier = Modifier.weight(1f))
                NumKey(numero = "6", modifier = Modifier.weight(1f))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                NumKey(numero = "7", modifier = Modifier.weight(1f))
                NumKey(numero = "8", modifier = Modifier.weight(1f))
                NumKey(numero = "9", modifier = Modifier.weight(1f))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.weight(1f))
                NumKey(numero = "0", modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF1E1E20))
                        .clickable { },
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

@Composable
private fun NumKey(
    numero: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(54.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1E1E20))
            .clickable { },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = numero,
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium
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