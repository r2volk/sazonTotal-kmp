package org.sazontotal.project.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.sazontotal.project.components.TabBarPill
import org.sazontotal.project.components.TabItem
import org.sazontotal.project.data.db.EmpleadoEntity
import org.sazontotal.project.data.repository.SazonRepository
import org.sazontotal.project.screens.screensCocina.CocinaPedidosScreen
import org.sazontotal.project.screens.screensCocina.CocinaPerfilScreen

@Composable
fun DashboardCocina(
    onLogoutClick: () -> Unit,
    onCambiarPinClick: () -> Unit = {},
    repositorio: SazonRepository? = null,
    empleado: EmpleadoEntity? = null
) {
    TabBarPill(
        items = listOf(
            TabItem(Icons.Default.Restaurant, "Pedidos") {
                CocinaPedidosScreen(
                    repositorio = repositorio,
                    empleado = empleado
                )
            },
            TabItem(Icons.Default.Person, "Perfil") {
                CocinaPerfilScreen(
                    onLogoutClick = onLogoutClick,
                    onCambiarPinClick = onCambiarPinClick,
                    empleado = empleado,
                    repositorio = repositorio
                )
            }
        )
    )
}

@Composable
@Preview
fun DashboardCocinaPreview() {
    DashboardCocina(
        onLogoutClick = {}
    )
}
