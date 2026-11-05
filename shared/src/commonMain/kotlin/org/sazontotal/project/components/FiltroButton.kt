package org.sazontotal.project.components

import org.sazontotal.project.theme.LocalTemaOscuro
import org.sazontotal.project.theme.UsoColor
import org.sazontotal.project.theme.colorDeTema
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.sazontotal.project.theme.Colores

@Composable
fun FiltroButton(
    activo: Boolean,
    texto: String,
    onClick: () -> Unit,
    textoColor: Color = Color.White,
    fondoTextoColor: Color = Colores.GrisMedio,
){
    val temaOscuro = LocalTemaOscuro.current
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(colorDeTema(if (activo) fondoTextoColor else Color.Transparent, temaOscuro, UsoColor.FONDO))
            .then(
                if (!activo) {
                    Modifier.border(1.dp
                        , colorDeTema(Colores.GrisMedio, temaOscuro, UsoColor.BORDE),
                        RoundedCornerShape(50))
                } else {
                    Modifier
                }
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            color = colorDeTema(if (activo) textoColor else Colores.TextoTenue, temaOscuro, UsoColor.TEXTO),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }

}

@Preview
@Composable
fun FiltroButtonPreview(){
    val temaOscuro = LocalTemaOscuro.current
    FiltroButton(
        activo = true,
        texto = "Todos",
        onClick = {}
    )
}