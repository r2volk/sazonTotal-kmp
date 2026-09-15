package org.sazontotal.project.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.sazontotal.project.components.SelectedTab
import org.sazontotal.project.icons.PedidosIcon
import org.sazontotal.project.screens.screensEmpleado.HistorialScreen
import org.sazontotal.project.screens.screensEmpleado.PedidosScreen
import org.sazontotal.project.screens.screensEmpleado.PerfilScreen

@Composable
@Preview
fun DashboardEmpleado(){

    Column() {
        SelectedTab(
            pedidos = {PedidosScreen()},
            historial = {HistorialScreen()},
            perfil = {PerfilScreen()}
        )
    }

}