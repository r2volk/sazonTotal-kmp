package org.sazontotal.project.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.sazontotal.project.components.TabBarPill
import org.sazontotal.project.components.TabItem
import org.sazontotal.project.data.CarritoCompra
import org.sazontotal.project.data.db.EmpleadoEntity
import org.sazontotal.project.data.repository.SazonRepository
import org.sazontotal.project.screens.screensMesero.MeseroHistorialScreen
import org.sazontotal.project.screens.screensMesero.MeseroPedidosScreen
import org.sazontotal.project.screens.screensMesero.MeseroPerfilScreen

@Composable
fun DashboardMesero(
    onLogoutClick: () -> Unit,
    onCarritoClick: () -> Unit,
    onAsistenteClick: () -> Unit,
    onCambiarPinClick: () -> Unit = {},
    repositorio: SazonRepository? = null,
    empleado: EmpleadoEntity? = null,
    carrito: CarritoCompra? = null
) {
    TabBarPill(
        items = listOf(
            TabItem(Icons.Default.Restaurant, "Pedidos") {
                MeseroPedidosScreen(
                    onCarritoClick = onCarritoClick,
                    onAsistenteClick = onAsistenteClick,
                    repositorio = repositorio,
                    empleado = empleado,
                    carrito = carrito
                )
            },
            TabItem(Icons.Default.History, "Historial") {
                MeseroHistorialScreen(
                    repositorio = repositorio
                )
            },
            TabItem(Icons.Default.Person, "Perfil") {
                MeseroPerfilScreen(
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
fun DashboardMeseroPreview() {
    DashboardMesero(
        onLogoutClick = {},
        onCarritoClick = {},
        onAsistenteClick = {}
    )
}
