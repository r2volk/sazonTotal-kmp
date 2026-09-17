package org.sazontotal.project.components

import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color

@Composable
fun SwitchButton(
    isActive: Boolean,
    modifier: Modifier = Modifier
){
    Switch(
        checked = isActive,
        onCheckedChange = null,
        modifier = modifier.scale(0.8f),
        colors = SwitchDefaults.colors(
            checkedThumbColor = Color(0xFF000000),
            checkedTrackColor = Color(0xFF30D158),

            uncheckedThumbColor = Color(0xFF000000),
            uncheckedTrackColor = Color(0xFF3A3A3C),

            checkedBorderColor = Color.Transparent,
            uncheckedBorderColor = Color.Transparent
        )
    )
}