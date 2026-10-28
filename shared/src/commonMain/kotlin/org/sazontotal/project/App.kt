package org.sazontotal.project

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview

import org.sazontotal.project.data.CarritoCompra
import org.sazontotal.project.data.db.EmpleadoEntity
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
import org.sazontotal.project.screens.CambioPinScreen
import org.sazontotal.project.screens.DashboardCocina
import org.sazontotal.project.screens.screensMesero.AsistenteScreen
import org.sazontotal.project.screens.screensMesero.carrito.CarritoScreen
import org.sazontotal.project.screens.screensMesero.carrito.MenuScreen
import org.sazontotal.project.screens.screensMesero.carrito.PedidoConcretadoScreen
import org.sazontotal.project.utils.BackHandlerApp

@Composable
@Preview
fun App() {
    MaterialTheme {
        var pantallaActual by remember { mutableStateOf("login") }
        var ultimoPedidoId by remember { mutableStateOf(0L) }

        var empleadoLogueado by remember { mutableStateOf<EmpleadoEntity?>(null) }

        var mesaActual by remember { mutableStateOf("4") }

        var errorLogin by remember { mutableStateOf<String?>(null) }
        // en preview no hay base de datos, por eso puede ser nula (pantallas sin datos).

        val db = remember {
            try { buildDatabase() } catch (e: Exception) { null }
        }
        val repositorio = remember(db) { db?.let { SazonRepository(it) } }
        val scope = rememberCoroutineScope()
        
        val carrito = remember { CarritoCompra() }

        // al iniciar se agregan las cuentas y datos de demostracion que falten.
        LaunchedEffect(db) {
            repositorio?.seedIfEmpty()
        }

        // atras del sistema: cada pantalla vuelve a su padre, no cierra toda la app.
        BackHandlerApp(enabled = pantallaActual != "login") {
            val padre = when (pantallaActual) {
                "gestionPersonal", "todosLosPedidos", "reportesAdmin", "editarMenu" -> "dashboardAdmin"
                "menuMesero" -> "dashboardMesero"
                "carritoMesero" -> "menuMesero"
                "pedidoConcretado" -> "dashboardMesero"
                "asistente" -> "dashboardMesero"
                "cambioPin" -> when {
                    empleadoLogueado?.id?.startsWith("MSR-") == true -> "dashboardMesero"
                    empleadoLogueado?.id?.startsWith("CCN-") == true -> "dashboardCocina"
                    else -> "dashboardAdmin"
                }
                else -> "login"
            }
            // al volver atras el carrito sigue abierto (mismo pedido en curso), salvo al salir del ticket.
            if (pantallaActual == "pedidoConcretado") {
                carrito.limpiar()
            }
            pantallaActual = padre
        }

        when (pantallaActual){
            "login" -> LoginScreen(
                onLogin = { empleadoIdIngresado, pinIngresado ->
                    errorLogin = null
                    scope.launch {
                        // el repositorio consulta empleados activos y valida el id con el pin.
                        val empleado = repositorio?.login(empleadoIdIngresado, pinIngresado)
                        if (empleado != null) {
                            // el rol define el area (mesero/cocina) y el ID el tipo: ADM-, MSR- o CCN-.
                            empleadoLogueado = empleado
                            pantallaActual = if (empleado.id.startsWith("ADM-")) {
                                "dashboardAdmin"
                            } else if (empleado.id.startsWith("MSR-")){
                                "dashboardMesero"
                            } else {
                                "dashboardCocina"
                            }
                        } else {
                            errorLogin = "ID o PIN incorrectos"
                        }
                    }
                },
                mensajeError = errorLogin
            )
            "dashboardAdmin" -> DashboardAdmin(
                onPersonalClick = {pantallaActual = "gestionPersonal"},
                onPedidosClick = {pantallaActual = "todosLosPedidos"},
                onReportesClick = {pantallaActual = "reportesAdmin"},
                onMenuClick = {pantallaActual = "editarMenu"},
                onLogoutClick = {
                    empleadoLogueado = null
                    pantallaActual = "login"
                },
                nombreAdmin = empleadoLogueado?.nombre ?: "Karina Castillo",
                repositorio = repositorio
            )

            "dashboardMesero" -> DashboardMesero(
                onLogoutClick = {
                    empleadoLogueado = null
                    pantallaActual = "login"
                },
                onAsistenteClick = {pantallaActual ="asistente"},
                onCarritoClick = {pantallaActual = "menuMesero"},
                onCambiarPinClick = {pantallaActual = "cambioPin"},
                repositorio = repositorio,
                empleado = empleadoLogueado,
                carrito = carrito
            )

            "dashboardCocina" -> DashboardCocina(
                onLogoutClick = {
                    empleadoLogueado = null
                    pantallaActual = "login"
                },
                onCambiarPinClick = {pantallaActual = "cambioPin"},
                repositorio = repositorio,
                empleado = empleadoLogueado
            )

            "reportesAdmin" -> ReportesScreen(
                backReportes = {pantallaActual = "dashboardAdmin"},
                repositorio = repositorio
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
                repositorio = repositorio,
                carrito = carrito,
                mesa = mesaActual,
                onMesaChange = { mesaActual = it },
                nombreMesero = empleadoLogueado?.nombre ?: "José P."
            )
            "carritoMesero" -> CarritoScreen(
                backCarritoScreen = {pantallaActual = "menuMesero"},
                confirmarCarrito = { id ->
                    ultimoPedidoId = id
                    pantallaActual = "pedidoConcretado"
                },
                meseroId = empleadoLogueado?.id ?: "EMP-001",
                repositorio = repositorio,
                carrito = carrito,
                mesa = mesaActual,
                onMesaChange = { mesaActual = it }
            )
            "pedidoConcretado" -> PedidoConcretadoScreen(
                volverDashboard = {pantallaActual = "dashboardMesero"},
                otroPedido = {pantallaActual = "menuMesero"},
                pedidoId = ultimoPedidoId,
                repositorio = repositorio
            )
            "asistente" -> AsistenteScreen(
                backAsistente = {pantallaActual = "dashboardMesero"},
                repositorio = repositorio,
                empleado = empleadoLogueado,
                mesa = mesaActual
            )
            "cambioPin" -> CambioPinScreen(
                backCambioPinScreen = {
                    if (empleadoLogueado?.id?.startsWith("MSR-") == true) {
                        pantallaActual = "dashboardMesero"
                    } else if (empleadoLogueado?.id?.startsWith("CCN-") == true) {
                        pantallaActual = "dashboardCocina"
                    } else {
                        pantallaActual = "dashboardAdmin"
                    }
                },
                empleado = empleadoLogueado,
                repositorio = repositorio
            )
        }
    }
}
