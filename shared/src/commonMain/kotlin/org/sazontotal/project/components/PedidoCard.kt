package org.sazontotal.project.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.sazontotal.project.enums.EstadoPedido

@Composable
fun PedidoCard(
    numeroMesa: String,
    esUrgente: Boolean,
    tiempo: String,
    nota: String,
    estado: EstadoPedido,
    onCambiarEstado: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color(0xFF1C1C1E),
                RoundedCornerShape(16.dp)
            )
            .border(
                1.dp,
                Color(0xFF2C2C2E),
                RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Mesa $numeroMesa",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            if (esUrgente) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF4A2528))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Urgente",
                        color = Color(0xFFFF6961),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = tiempo,
                color = if (esUrgente) Color(0xFFFF9F0A) else Color(0xFF8E8E93),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (numeroMesa == "7") {
                ItemFila(
                    emoji = "\uD83C\uDF72",
                    texto = "Ají de gallina x2",
                    bg = Color(0xFF3B2A18)
                )
            } else {
                ItemFila(
                    emoji = "\uD83C\uDF57",
                    texto = "Lomo saltado x1",
                    bg = Color(0xFF3B2A18)
                )
                ItemFila(
                    emoji = "\uD83C\uDF78",
                    texto = "Pisco sour x1",
                    bg = Color(0xFF1E3A2A)
                )
            }
        }

        if (nota.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Nota: $nota",
                color = Color(0xFF9A9AA0),
                fontSize = 14.sp,
                fontStyle = FontStyle.Italic
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (estado) {
            EstadoPedido.PENDIENTE -> {
                Button(
                    onClick = onCambiarEstado,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF9F0A)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Iniciar preparación",
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            EstadoPedido.PREPARANDO -> {
                Button(
                    onClick = onCambiarEstado,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF30D158)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Marcar listo",
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            EstadoPedido.LISTO -> {
                Text(
                    text = "✓ Pedido listo",
                    color = Color(0xFF30D158),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun ItemFila(
    emoji: String,
    texto: String,
    bg: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(bg),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = emoji,
                fontSize = 14.sp
            )
        }
        Text(
            text = texto,
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
