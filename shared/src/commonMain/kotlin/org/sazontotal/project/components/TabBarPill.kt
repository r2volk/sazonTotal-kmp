package org.sazontotal.project.components

import org.sazontotal.project.theme.coloresNavegacion
import org.sazontotal.project.theme.LocalTemaOscuro
import org.sazontotal.project.theme.UsoColor
import org.sazontotal.project.theme.colorDeTema
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class TabItem(
    val icon: ImageVector,
    val label: String,
    val content: @Composable () -> Unit
)

@Composable
fun TabBarPill(
    items: List<TabItem>,
    modifier: Modifier = Modifier
) {
    val temaOscuro = LocalTemaOscuro.current
    val colores = coloresNavegacion(temaOscuro)
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorDeTema(Color(0xFF000000), temaOscuro, UsoColor.FONDO))
    ) {

        Column(
            modifier = Modifier.weight(1f, fill = true)
        ) {
            items.getOrNull(selectedIndex)?.content()
        }

        // barra de navegacion inferior tipo pill
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(colores.superficie)
                    .border(1.dp, colores.borde, RoundedCornerShape(28.dp))
                    .padding(6.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEachIndexed { index, item ->
                    PillTabItem(
                        icon = item.icon,
                        text = item.label,
                        selected = index == selectedIndex,
                        onClick = { selectedIndex = index },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun PillTabItem(
    icon: ImageVector,
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val temaOscuro = LocalTemaOscuro.current
    val colores = coloresNavegacion(temaOscuro)

    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(if (selected) colores.seleccion else Color.Transparent)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = text,
                    tint = colores.sobreSeleccion,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = text,
                    color = colores.sobreSeleccion,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = colores.inactivo,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}