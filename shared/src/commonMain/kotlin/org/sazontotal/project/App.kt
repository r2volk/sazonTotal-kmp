package org.sazontotal.project

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

import org.jetbrains.compose.resources.painterResource
import org.sazontotal.project.enums.UserRole
import org.sazontotal.project.models.User
import org.sazontotal.project.screens.DashboardAdmin
import org.sazontotal.project.screens.DashboardEmpleado
import org.sazontotal.project.screens.LoginScreen
import org.sazontotal.project.screens.screensAdmin.GestionPersonalScreen
import org.sazontotal.project.screens.screensAdmin.ReportesScreen
import org.sazontotal.project.screens.screensAdmin.TodosLosPedidosScreen
import org.sazontotal.project.screens.screensAdmin.editarMenu.EditarMenuScreen

import sazontotal.shared.generated.resources.Res
import sazontotal.shared.generated.resources.compose_multiplatform

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
                onMenuClick = {pantallaActual = "editarMenu"}
            )
            "reportesAdmin" -> ReportesScreen()
            "dashboardEmpleado" -> DashboardEmpleado()
            "gestionPersonal" -> GestionPersonalScreen()
            "todosLosPedidos" -> TodosLosPedidosScreen()
            "editarMenu" -> EditarMenuScreen()
        }
    }
}