package org.sazontotal.project.components

import org.sazontotal.project.theme.LocalTemaOscuro
import org.sazontotal.project.theme.UsoColor
import org.sazontotal.project.theme.colorDeTema
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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

@Composable
fun CantidadStepper(
    cantidad: Int,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
    plusEnabled: Boolean = true,
    minusEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val temaOscuro = LocalTemaOscuro.current
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(colorDeTema(Colores.GrisMedio, temaOscuro, UsoColor.FONDO))
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "−",
            color = colorDeTema(if (minusEnabled) Colores.TextoTenue else Color(0xFF55555A), temaOscuro, UsoColor.TEXTO),
            fontSize = 18.sp,
            modifier = Modifier.clickable(enabled = minusEnabled) { onMinus() }
        )
        Text(
            text = "$cantidad",
            color = colorDeTema(Color.White, temaOscuro, UsoColor.TEXTO),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "+",
            color = colorDeTema(if (plusEnabled) Colores.VerdePrimario else Colores.TextoTenue, temaOscuro, UsoColor.TEXTO),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable(enabled = plusEnabled) { onPlus() }
        )
    }
}
