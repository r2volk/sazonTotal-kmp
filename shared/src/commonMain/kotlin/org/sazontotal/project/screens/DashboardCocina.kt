package org.sazontotal.project.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import org.sazontotal.project.components.SelectedTabCocina
import org.sazontotal.project.screens.screensCocina.CocinaPedidosScreen
import org.sazontotal.project.screens.screensCocina.CocinaPerfilScreen

@Composable
fun DashboardCocina() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        SelectedTabCocina(
            pedidos = { CocinaPedidosScreen() },
            perfil = { CocinaPerfilScreen() }
        )
    }

}

@Composable
@Preview
fun DashboardCocinaPreview() {
    DashboardCocina()
}
