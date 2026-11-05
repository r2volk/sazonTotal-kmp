package org.sazontotal.project.components

import org.sazontotal.project.theme.LocalTemaOscuro
import org.sazontotal.project.theme.UsoColor
import org.sazontotal.project.theme.colorDeTema
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.sazontotal.project.theme.Colores
import org.sazontotal.project.data.destinoPedido

data class CocinaPlatoVisual(
    val nombre: String,
    val cantidad: Int,
    val emoji: String = "\uD83C\uDF7D",
    val fondoIcono: Color = Color(0xFF3B2A18)
)

@Composable
fun CocinaPedidoCard(
    mesa: String,
    numeroPedido: String,
    mesero: String,
    tiempo: String,
    platos: List<CocinaPlatoVisual>,
    nota: String = "",
    onListoClick: () -> Unit = {},
    yaListo: Boolean = false,
    modifier: Modifier = Modifier
) {
    val temaOscuro = LocalTemaOscuro.current
    Column(
        modifier = modifier
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
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = destinoPedido(mesa),
                color = colorDeTema(Color.White, temaOscuro, UsoColor.TEXTO),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = tiempo,
                color = colorDeTema(Colores.TextoTenue, temaOscuro, UsoColor.TEXTO),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Text(
            text = "#$numeroPedido · Mesero: $mesero",
            color = colorDeTema(Colores.TextoTenue, temaOscuro, UsoColor.TEXTO),
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal
        )

        Spacer(modifier = Modifier.height(6.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            platos.forEach { plato ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(RoundedCornerShape(7.dp))
                            .background(colorDeTema(plato.fondoIcono, temaOscuro, UsoColor.FONDO)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = plato.emoji,
                            fontSize = 13.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = plato.nombre,
                        color = colorDeTema(Color.White, temaOscuro, UsoColor.TEXTO),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "x${plato.cantidad}",
                        color = colorDeTema(Colores.TextoTenue, temaOscuro, UsoColor.TEXTO),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        if (nota.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(colorDeTema(Color(0xFF2C2C1E), temaOscuro, UsoColor.FONDO))
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "\uD83D\uDCDD",
                    fontSize = 13.sp
                )
                Text(
                    text = nota,
                    color = colorDeTema(Color(0xFFD6C48A), temaOscuro, UsoColor.TEXTO),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // si cocina ya lo marco, se muestra como preparado y sin boton.
        if (yaListo) {
            Text(
                text = "✓ Listo",
                color = colorDeTema(Colores.VerdePrimario, temaOscuro, UsoColor.TEXTO),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        } else {
            Button(
                onClick = onListoClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorDeTema(Colores.VerdeAcento, temaOscuro, UsoColor.FONDO)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "✓  Listo",
                    color = Color.Black,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
