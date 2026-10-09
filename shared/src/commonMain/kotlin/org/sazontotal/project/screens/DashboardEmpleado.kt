package org.sazontotal.project.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import org.sazontotal.project.components.SelectedTab
import org.sazontotal.project.data.repository.SazonRepository
import org.sazontotal.project.icons.PedidosIcon
import org.sazontotal.project.screens.screensEmpleado.HistorialScreen
import org.sazontotal.project.screens.screensEmpleado.PedidosScreen
import org.sazontotal.project.screens.screensEmpleado.PerfilScreen

@Composable
fun DashboardEmpleado(
    onLogoutClick: () -> Unit,
    onCarritoClick: () -> Unit,
    repositorio: SazonRepository? = null
){

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        SelectedTab(
            pedidos = {PedidosScreen(
                onCarritoClick = onCarritoClick,
                repositorio = repositorio
            )},
            historial = {HistorialScreen(
                repositorio = repositorio
            )},
            perfil = {PerfilScreen(
                onLogoutClick = onLogoutClick
            )}
        )
    }

}

@Composable
@Preview
fun DashboardEmpleadoPreview(){
    DashboardEmpleado(
        onLogoutClick = {},
        onCarritoClick = {}
    )
}


