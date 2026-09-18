package org.sazontotal.project.components

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

@Composable
fun FiltroButton(
    activo: Boolean,
    texto: String,
    onClick: () -> Unit,
    textoColor: Color = Color.White,
    fondoTextoColor: Color = Color(0xFF2C2C2E),
){
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (activo) fondoTextoColor else Color.Transparent)
            .then(
                if (!activo) {
                    Modifier.border(1.dp
                        , Color(0xFF2C2C2E),
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
            color = if (activo) textoColor else Color(0xFF8E8E93),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }

}

@Preview
@Composable
fun FiltroButtonPreview(){
    FiltroButton(
        activo = true,
        texto = "Todos",
        onClick = {}
    )
}