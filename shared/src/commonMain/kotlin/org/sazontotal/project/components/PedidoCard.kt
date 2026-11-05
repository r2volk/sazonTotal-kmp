package org.sazontotal.project.components

import org.sazontotal.project.theme.LocalTemaOscuro
import org.sazontotal.project.theme.UsoColor
import org.sazontotal.project.theme.colorDeTema
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import org.sazontotal.project.data.destinoPedido
import org.sazontotal.project.data.esDelivery
import org.sazontotal.project.data.direccionDelivery

@Composable
fun PedidoCard(
    numeroMesa: String,
    tiempo: String,
    nota: String,
    estado: EstadoPedido,
    onCambiarEstado: () -> Unit,
    lineas: List<String> = emptyList()
) {
    val temaOscuro = LocalTemaOscuro.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                colorDeTema(Colores.Superficie, temaOscuro, UsoColor.FONDO),
                RoundedCornerShape(16.dp)
            )
            .border(
                1.dp,
                colorDeTema(Colores.GrisMedio, temaOscuro, UsoColor.BORDE),
                RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = destinoPedido(numeroMesa),
                color = colorDeTema(Color.White, temaOscuro, UsoColor.TEXTO),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = tiempo,
                color = colorDeTema(Colores.TextoTenue, temaOscuro, UsoColor.TEXTO),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (esDelivery(numeroMesa)) {
            Text(text = direccionDelivery(numeroMesa), color = colorDeTema(Colores.TextoTenue, temaOscuro, UsoColor.TEXTO), fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
        }

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
                color = colorDeTema(Color(0xFF9A9AA0), temaOscuro, UsoColor.TEXTO),
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
                        color = colorDeTema(Colores.Naranja, temaOscuro, UsoColor.TEXTO),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(colorDeTema(Colores.Naranja.copy(alpha = 0.12f), temaOscuro, UsoColor.FONDO))
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
                        containerColor = colorDeTema(Colores.VerdeBoton, temaOscuro, UsoColor.FONDO)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        if (esDelivery(numeroMesa)) "Enviar con motorizado" else "Marcar entregado",
                        color = colorDeTema(Color.Black, temaOscuro, UsoColor.TEXTO),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            EstadoPedido.EN_CAMINO -> {
                Button(onClick = onCambiarEstado, modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorDeTema(Colores.VerdeBoton, temaOscuro, UsoColor.FONDO)),
                    shape = RoundedCornerShape(12.dp)) {
                    Text("Marcar entregado", color = colorDeTema(Color.Black, temaOscuro, UsoColor.TEXTO), fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
                Text("En camino", color = colorDeTema(Colores.VerdePrimario, temaOscuro, UsoColor.TEXTO), fontSize = 14.sp)
            }

            EstadoPedido.ENTREGADO -> {
                Text(
                    text = "✓ Pedido entregado",
                    color = colorDeTema(Colores.VerdePrimario, temaOscuro, UsoColor.TEXTO),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            EstadoPedido.ANULADO -> {
                Text(
                    text = "Anulado",
                    color = colorDeTema(Colores.Rojo, temaOscuro, UsoColor.TEXTO),
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
    val temaOscuro = LocalTemaOscuro.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(colorDeTema(bg, temaOscuro, UsoColor.FONDO)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = emoji,
                fontSize = 14.sp
            )
        }
        Text(
            text = texto,
            color = colorDeTema(Color.White, temaOscuro, UsoColor.TEXTO),
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
