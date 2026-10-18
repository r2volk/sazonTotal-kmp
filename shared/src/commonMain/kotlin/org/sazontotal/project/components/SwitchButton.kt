package org.sazontotal.project.components

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
    Switch(
        checked = isActive,
        onCheckedChange = onCambio,
        modifier = modifier.scale(0.8f),
        colors = SwitchDefaults.colors(
            checkedThumbColor = Color(0xFF000000),
            checkedTrackColor = Colores.VerdePrimario,

            uncheckedThumbColor = Color(0xFF000000),
            uncheckedTrackColor = Colores.GrisDeshabilitado,

            checkedBorderColor = Color.Transparent,
            uncheckedBorderColor = Color.Transparent
        )
    )
}