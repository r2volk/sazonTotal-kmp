package org.sazontotal.project

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview

import org.sazontotal.project.data.db.buildDatabase
import org.sazontotal.project.data.repository.SazonRepository
import org.sazontotal.project.screens.DashboardAdmin
import org.sazontotal.project.screens.DashboardMesero
import org.sazontotal.project.screens.LoginScreen
import org.sazontotal.project.screens.screensAdmin.editarEmpleado.GestionPersonalScreen
import org.sazontotal.project.screens.screensAdmin.ReportesScreen
import org.sazontotal.project.screens.screensAdmin.TodosLosPedidosScreen
import org.sazontotal.project.screens.screensAdmin.editarMenu.EditarMenuScreen
import kotlinx.coroutines.launch
import org.sazontotal.project.screens.DashboardCocina
import org.sazontotal.project.screens.screensMesero.carrito.CarritoScreen
import org.sazontotal.project.screens.screensMesero.carrito.MenuScreen
import org.sazontotal.project.screens.screensMesero.carrito.PedidoConcretadoScreen

@Composable
@Preview
fun App() {
    MaterialTheme {
        var pantallaActual by remember { mutableStateOf("login") }
        // En la vista previa no hay base de datos (no hay app corriendo),
        // así que se permite que sea nula y se muestran las pantallas sin datos.
        val db = remember {
            try { buildDatabase() } catch (e: Exception) { null }
        }
        val repositorio = remember(db) { db?.let { SazonRepository(it) } }
        val scope = rememberCoroutineScope()

        LaunchedEffect(db) {
            repositorio?.seedIfEmpty()
        }

        when (pantallaActual){
            "login" -> LoginScreen(
                onLogin = { empleadoIdIngresado, pinIngresado ->
                    scope.launch {
                        val empleado = repositorio?.login(empleadoIdIngresado, pinIngresado)
                        if (empleado != null) {
                            // El rol dice en qué área trabaja (Mesero/Cocina).
                            // El ID dice si es administrador (ADM-), mesero (MSR-) o cocina (CCN-).
                            pantallaActual = if (empleado.id.startsWith("ADM-")) {
                                "dashboardAdmin"
                            } else if (empleado.id.startsWith("MSR-")){
                                "dashboardMesero"
                            } else {
                                "dashboardCocina"
                            }
                        }
                    }
                }
            )
            "dashboardAdmin" -> DashboardAdmin(
                onPersonalClick = {pantallaActual = "gestionPersonal"},
                onPedidosClick = {pantallaActual = "todosLosPedidos"},
                onReportesClick = {pantallaActual = "reportesAdmin"},
                onMenuClick = {pantallaActual = "editarMenu"},
                onLogoutClick = {pantallaActual= "login"}
            )

            "dashboardMesero" -> DashboardMesero(
                onLogoutClick = {pantallaActual = "login"},
                onCarritoClick = {pantallaActual = "menuMesero"},
                repositorio = repositorio
            )

            "dashboardCocina" -> DashboardCocina(
                onLogoutClick = {pantallaActual = "login"},
            )

            "reportesAdmin" -> ReportesScreen(
                backReportes = {pantallaActual = "dashboardAdmin"}
            )

            "gestionPersonal" -> GestionPersonalScreen(
                backGestionPersonal = {pantallaActual = "dashboardAdmin"},
                repositorio = repositorio
            )
            "todosLosPedidos" -> TodosLosPedidosScreen(
                backTodosLosPedidos = {pantallaActual = "dashboardAdmin"},
                repositorio = repositorio
            )
            "editarMenu" -> EditarMenuScreen(
                backEditarMenu = {pantallaActual = "dashboardAdmin"},
                repositorio = repositorio
            )
            "menuMesero" -> MenuScreen(
                backMenuScreen = {pantallaActual = "dashboardMesero"},
                verCarrito = {pantallaActual = "carritoMesero"},
                repositorio = repositorio
            )
            "carritoMesero" -> CarritoScreen(
                backCarritoScreen = {pantallaActual = "menuMesero"},
                confirmarCarrito = {pantallaActual = "pedidoConcretado"},
                repositorio = repositorio
            )
            "pedidoConcretado" -> PedidoConcretadoScreen(
                volverDashboard = {pantallaActual = "dashboardMesero"},
                otroPedido = {pantallaActual = "menuMesero"}
            )
        }
    }
}