package org.sazontotal.project.components

import org.sazontotal.project.theme.LocalTemaOscuro
import org.sazontotal.project.theme.UsoColor
import org.sazontotal.project.theme.colorDeTema
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.sazontotal.project.theme.Colores

@Composable
fun BuscadorTextField(
    nombreBuscado: String,
    onNombreChanged: (String) -> Unit,
    texto: String
){
    val temaOscuro = LocalTemaOscuro.current
    TextField(
        value = nombreBuscado,
        onValueChange = onNombreChanged,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .border(1.dp, colorDeTema(Colores.GrisMedio, temaOscuro, UsoColor.BORDE), RoundedCornerShape(12.dp)),
        placeholder = {
            Text(
                text = texto,
                color = colorDeTema(Colores.TextoTenue, temaOscuro, UsoColor.TEXTO),
                fontSize = 15.sp
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = colorDeTema(Colores.TextoTenue, temaOscuro, UsoColor.TEXTO),
                modifier = Modifier.size(20.dp)
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        textStyle = TextStyle(color = colorDeTema(Color.White, temaOscuro, UsoColor.TEXTO), fontSize = 15.sp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = colorDeTema(Colores.Superficie, temaOscuro, UsoColor.FONDO),
            unfocusedContainerColor = colorDeTema(Colores.Superficie, temaOscuro, UsoColor.FONDO),
            focusedIndicatorColor = colorDeTema(Color.Transparent, temaOscuro, UsoColor.BORDE),
            unfocusedIndicatorColor = colorDeTema(Color.Transparent, temaOscuro, UsoColor.BORDE),
            cursorColor = colorDeTema(Color.White, temaOscuro, UsoColor.TEXTO)
        )
    )
}