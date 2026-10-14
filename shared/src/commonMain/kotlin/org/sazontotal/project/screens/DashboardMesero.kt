package org.sazontotal.project.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import org.sazontotal.project.components.SelectedTabMesero
import org.sazontotal.project.data.repository.SazonRepository
import org.sazontotal.project.screens.screensMesero.MeseroHistorialScreen
import org.sazontotal.project.screens.screensMesero.MeseroPedidosScreen
import org.sazontotal.project.screens.screensMesero.MeseroPerfilScreen

@Composable
fun DashboardMesero(
    onLogoutClick: () -> Unit,
    onCarritoClick: () -> Unit,
    repositorio: SazonRepository? = null
){

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        SelectedTabMesero(
            pedidos = {MeseroPedidosScreen(
                onCarritoClick = onCarritoClick,
                repositorio = repositorio
            )},
            historial = {MeseroHistorialScreen(
                repositorio = repositorio
            )},
            perfil = {MeseroPerfilScreen(
                onLogoutClick = onLogoutClick
            )}
        )
    }

}

@Composable
@Preview
fun DashboardMeseroPreview(){
    DashboardMesero(
        onLogoutClick = {},
        onCarritoClick = {}
    )
}


