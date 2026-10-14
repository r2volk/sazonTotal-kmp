package org.sazontotal.project.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import org.sazontotal.project.components.SelectedTabCocina
import org.sazontotal.project.data.db.EmpleadoEntity
import org.sazontotal.project.data.repository.SazonRepository
import org.sazontotal.project.screens.screensCocina.CocinaPedidosScreen
import org.sazontotal.project.screens.screensCocina.CocinaPerfilScreen

@Composable
fun DashboardCocina(
    onLogoutClick: () -> Unit,
    repositorio: SazonRepository? = null,
    empleado: EmpleadoEntity? = null
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        SelectedTabCocina(
            pedidos = { CocinaPedidosScreen(repositorio = repositorio, empleado = empleado) },
            perfil = { CocinaPerfilScreen(
                onLogoutClick = onLogoutClick,
                empleado = empleado,
                repositorio = repositorio
            ) }
        )
    }

}

@Composable
@Preview
fun DashboardCocinaPreview() {
    DashboardCocina(
        onLogoutClick = {}
    )
}
