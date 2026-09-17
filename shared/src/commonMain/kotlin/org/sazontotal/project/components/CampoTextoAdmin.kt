package org.sazontotal.project.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CampoTextoAdmin(
    valor: String,
    onValorChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = if (singleLine) 1 else 4,
    readOnly: Boolean = false,
    trailing: @Composable (() -> Unit)? = null
) {
    TextField(
        value = valor,
        onValueChange = onValorChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = placeholder,
                color = Color(0xFF8E8E93),
                fontSize = 15.sp
            )
        },
        trailingIcon = trailing,
        singleLine = singleLine,
        minLines = minLines,
        maxLines = maxLines,
        readOnly = readOnly,
        shape = RoundedCornerShape(12.dp),
        textStyle = TextStyle(
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        ),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFF1C1C1E),
            unfocusedContainerColor = Color(0xFF1C1C1E),
            disabledContainerColor = Color(0xFF1C1C1E),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = Color.White
        )
    )
}
