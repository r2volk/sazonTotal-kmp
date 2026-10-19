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
import org.sazontotal.project.theme.Colores

@Composable
fun PedidoCard(
    numeroMesa: String,
    tiempo: String,
    nota: String,
    estado: EstadoPedido,
    onCambiarEstado: () -> Unit,
    lineas: List<String> = emptyList()
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Colores.Superficie,
                RoundedCornerShape(16.dp)
            )
            .border(
                1.dp,
                Colores.GrisMedio,
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

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = tiempo,
                color = Colores.TextoTenue,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            lineas.forEach { linea ->
                ItemFila(
                    emoji = "\uD83C\uDF7D",
                    texto = linea,
                    bg = Color(0xFF3B2A18)
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

        // el card pasa por PENDIENTE (espera), LISTO (boton para entregar) y ENTREGADO (fin).
        when (estado) {
            // PENDIENTE es estado, no boton: se muestra como etiqueta centrada para no parecer accionable.
            EstadoPedido.PENDIENTE -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Preparando en cocina",
                        color = Colores.Naranja,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Colores.Naranja.copy(alpha = 0.12f))
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    )
                }
            }

            EstadoPedido.LISTO -> {
                Button(
                    onClick = onCambiarEstado,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Colores.VerdeBoton
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Marcar entregado",
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            EstadoPedido.ENTREGADO -> {
                Text(
                    text = "✓ Pedido entregado",
                    color = Colores.VerdePrimario,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            EstadoPedido.ANULADO -> {
                Text(
                    text = "Anulado",
                    color = Colores.Rojo,
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
