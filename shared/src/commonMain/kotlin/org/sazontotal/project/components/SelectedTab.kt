package org.sazontotal.project.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SelectedTab(
    pedidos: @Composable () -> Unit,
    historial: @Composable () -> Unit,
    perfil: @Composable () -> Unit
) {
    var selectedTab by rememberSaveable {
        mutableIntStateOf(0)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000))
    ) {

        // CONTENIDO
        Column(
            modifier = Modifier.weight(1f, fill = true)
        ) {
            when (selectedTab) {
                0 -> pedidos()
                1 -> historial()
                2 -> perfil()
            }
        }

        // BOTTOM NAVIGATION
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF101010))
                .navigationBarsPadding()
                .padding(vertical = 8.dp)
                .height(58.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {

            TabItem(
                icon = Icons.Default.Restaurant,
                text = "Pedidos",
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 }
            )

            TabItem(
                icon = Icons.Default.History,
                text = "Historial",
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 }
            )

            TabItem(
                icon = Icons.Default.Person,
                text = "Perfil",
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 }
            )
        }
    }
}

@Composable
private fun TabItem(
    icon: ImageVector,
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val color = if (selected) {
        Color(0xFF22C55E)
    } else {
        Color(0xFF666666)
    }

    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = color
        )

        Text(
            text = text,
            color = color,
            fontSize = 10.sp
        )
    }
}
