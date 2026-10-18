package org.sazontotal.project.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.sazontotal.project.theme.Colores


@Composable
fun StatCardAdmin(
    textoSuperior: String,
    textoInferior: String,
    colorSuperior: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .heightIn(min = 80.dp)
            .background(
                color = Colores.Superficie,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(
                horizontal = 18.dp,
                vertical = 20.dp
            ),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = textoSuperior,
            color = colorSuperior,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = textoInferior,
            color = Colores.TextoTenue,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal
        )
    }

}
