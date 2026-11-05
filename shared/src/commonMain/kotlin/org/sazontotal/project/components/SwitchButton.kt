package org.sazontotal.project.components

import org.sazontotal.project.theme.LocalTemaOscuro
import org.sazontotal.project.theme.UsoColor
import org.sazontotal.project.theme.colorDeTema
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import org.sazontotal.project.theme.Colores

@Composable
fun SwitchButton(
    isActive: Boolean,
    modifier: Modifier = Modifier,
    onCambio: (Boolean) -> Unit
){
    val temaOscuro = LocalTemaOscuro.current
    Switch(
        checked = isActive,
        onCheckedChange = onCambio,
        modifier = modifier.scale(0.8f),
        colors = SwitchDefaults.colors(
            checkedThumbColor = colorDeTema(Color(0xFF000000), temaOscuro, UsoColor.TEXTO),
            checkedTrackColor = colorDeTema(Colores.VerdePrimario, temaOscuro, UsoColor.FONDO),

            uncheckedThumbColor = colorDeTema(Color.White, temaOscuro, UsoColor.TEXTO),
            uncheckedTrackColor = colorDeTema(Colores.GrisDeshabilitado, temaOscuro, UsoColor.FONDO),

            checkedBorderColor = colorDeTema(Color.Transparent, temaOscuro, UsoColor.BORDE),
            uncheckedBorderColor = colorDeTema(Colores.GrisMedio, temaOscuro, UsoColor.BORDE)
        )
    )
}