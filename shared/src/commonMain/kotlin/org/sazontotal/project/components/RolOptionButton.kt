package org.sazontotal.project.components

import org.sazontotal.project.theme.LocalTemaOscuro
import org.sazontotal.project.theme.UsoColor
import org.sazontotal.project.theme.colorDeTema
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.sazontotal.project.theme.Colores

@Composable
fun RolOptionButton(
    texto: String,
    seleccionado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val temaOscuro = LocalTemaOscuro.current
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(colorDeTema(if (seleccionado) Colores.VerdeOscuro else Colores.Superficie, temaOscuro, UsoColor.FONDO))
            .then(
                if (seleccionado) {
                    Modifier.border(1.dp, colorDeTema(Colores.VerdePrimario, temaOscuro, UsoColor.BORDE), RoundedCornerShape(12.dp))
                } else {
                    Modifier
                }
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            color = colorDeTema(if (seleccionado) Colores.VerdePrimario else Colores.TextoTenue, temaOscuro, UsoColor.TEXTO),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
