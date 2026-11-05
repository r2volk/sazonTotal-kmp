package org.sazontotal.project.components

import org.sazontotal.project.theme.LocalTemaOscuro
import org.sazontotal.project.theme.UsoColor
import org.sazontotal.project.theme.colorDeTema
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.sazontotal.project.theme.Colores


@Composable
fun StatCardCentrado(
    textoSuperior: String,
    textoInferior: String,
    colorSuperior: Color = Color.White,
    modifier: Modifier = Modifier
) {
    val temaOscuro = LocalTemaOscuro.current
    Column(
        modifier = modifier
            .heightIn(min = 60.dp)
            .background(
                color = colorDeTema(Colores.Superficie, temaOscuro, UsoColor.FONDO),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(
                horizontal = 10.dp,
                vertical = 12.dp
            ),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = textoSuperior,
            color = colorDeTema(colorSuperior, temaOscuro, UsoColor.TEXTO),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = textoInferior,
            color = colorDeTema(Colores.TextoTenue, temaOscuro, UsoColor.TEXTO),
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal
        )
    }

}

@Composable
@Preview
fun PreviewStat(){
    val temaOscuro = LocalTemaOscuro.current
    StatCardCentrado(
        textoSuperior = "2",
        textoInferior = "Pendientes",
        colorSuperior = Color.White
    )
}