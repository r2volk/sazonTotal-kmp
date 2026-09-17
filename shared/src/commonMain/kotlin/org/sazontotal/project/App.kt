package org.sazontotal.project

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview

import org.sazontotal.project.enums.UserRole
import org.sazontotal.project.models.User
import org.sazontotal.project.screens.DashboardAdmin
import org.sazontotal.project.screens.DashboardEmpleado
import org.sazontotal.project.screens.LoginScreen
import org.sazontotal.project.screens.screensAdmin.editarEmpleado.GestionPersonalScreen
import org.sazontotal.project.screens.screensAdmin.ReportesScreen
import org.sazontotal.project.screens.screensAdmin.TodosLosPedidosScreen
import org.sazontotal.project.screens.screensAdmin.editarMenu.EditarMenuScreen

@Composable
@Preview
fun App() {
    MaterialTheme {
        var pantallaActual by remember { mutableStateOf("login") }
        val usuarios = listOf(
            User("ADM-001", "Karina Castillo", "1234", UserRole.ADMIN),
            User("EMP-001", "Ricardo Sanchez", "4321", UserRole.COCINERO)
        )

        when (pantallaActual){
            "login" -> LoginScreen(
                onLogin = { empleadoIdIngresado, pinIngresado ->

                    val usuarioEncontrado = usuarios.find { usuario ->
                        usuario.id == empleadoIdIngresado.trim() &&
                        usuario.pin == pinIngresado
                    }

                    if (usuarioEncontrado?.rol == UserRole.ADMIN) {
                        pantallaActual = "dashboardAdmin"
                    } else if (usuarioEncontrado?.rol == UserRole.COCINERO) {
                        pantallaActual = "dashboardEmpleado"
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
            "dashboardEmpleado" -> DashboardEmpleado(
                onLogoutClick = {pantallaActual = "login"}
            )
            "reportesAdmin" -> ReportesScreen()
            "gestionPersonal" -> GestionPersonalScreen()
            "todosLosPedidos" -> TodosLosPedidosScreen()
            "editarMenu" -> EditarMenuScreen()
        }
    }
}