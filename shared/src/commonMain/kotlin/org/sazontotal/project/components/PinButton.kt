package org.sazontotal.project.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PinButton(
    number: Int,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = { onClick(number) },
        modifier = modifier.aspectRatio(1.05f),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF1E1E20)
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = number.toString(),
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
    }
}
