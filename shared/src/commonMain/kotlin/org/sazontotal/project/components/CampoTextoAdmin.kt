package org.sazontotal.project.components

import org.sazontotal.project.theme.LocalTemaOscuro
import org.sazontotal.project.theme.UsoColor
import org.sazontotal.project.theme.colorDeTema
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.sazontotal.project.theme.Colores

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
    keyboardOptions: KeyboardOptions? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailing: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    borderColor: Color? = null
) {
    val temaOscuro = LocalTemaOscuro.current
    TextField(
        value = valor,
        onValueChange = onValorChange,
        keyboardOptions = keyboardOptions ?: KeyboardOptions.Default,
        visualTransformation = visualTransformation,
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (borderColor != null) {
                    Modifier.border(2.dp, colorDeTema(borderColor, temaOscuro, UsoColor.BORDE), RoundedCornerShape(12.dp))
                } else {
                    Modifier.border(1.dp, colorDeTema(Colores.GrisMedio, temaOscuro, UsoColor.BORDE), RoundedCornerShape(12.dp))
                }
            ),
        placeholder = {
            Text(
                text = placeholder,
                color = colorDeTema(Color(0xFF5A5A5E), temaOscuro, UsoColor.TEXTO),
                fontSize = 15.sp
            )
        },
        trailingIcon = trailing,
        leadingIcon = leading,
        singleLine = singleLine,
        minLines = minLines,
        maxLines = maxLines,
        readOnly = readOnly,
        shape = RoundedCornerShape(12.dp),
        textStyle = TextStyle(
            color = colorDeTema(Color.White, temaOscuro, UsoColor.TEXTO),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        ),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = colorDeTema(Colores.Superficie, temaOscuro, UsoColor.FONDO),
            unfocusedContainerColor = colorDeTema(Colores.Superficie, temaOscuro, UsoColor.FONDO),
            disabledContainerColor = colorDeTema(Colores.Superficie, temaOscuro, UsoColor.FONDO),
            focusedIndicatorColor = colorDeTema(Color.Transparent, temaOscuro, UsoColor.BORDE),
            unfocusedIndicatorColor = colorDeTema(Color.Transparent, temaOscuro, UsoColor.BORDE),
            disabledIndicatorColor = colorDeTema(Color.Transparent, temaOscuro, UsoColor.BORDE),
            cursorColor = colorDeTema(Color.White, temaOscuro, UsoColor.TEXTO)
        )
    )
}
