package org.sazontotal.project.components

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


@Composable
fun StatCardCentrado(
    textoSuperior: String,
    textoInferior: String,
    colorSuperior: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .heightIn(min = 60.dp)
            .background(
                color = Color(0xFF1C1C1E),
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
            color = colorSuperior,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = textoInferior,
            color = Color(0xFF8E8E93),
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal
        )
    }

}

@Composable
@Preview
fun PreviewStat(){
    StatCardCentrado(
        textoSuperior = "2",
        textoInferior = "Pendientes",
        colorSuperior = Color.White
    )
}