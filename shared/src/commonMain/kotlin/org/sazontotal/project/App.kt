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

@Composable
@Preview
fun App() {
    MaterialTheme {
        var pantallaActual by remember { mutableStateOf("login") }
        var ultimoPedidoId by remember { mutableStateOf(0L) }
        // Quién entró con su ID y PIN. Se recuerda para mostrar su nombre
        // en el perfil y firmar sus pedidos. Se borra al cerrar sesión.
        var empleadoLogueado by remember { mutableStateOf<EmpleadoEntity?>(null) }
        // Mesa atendida ahora (se elige en el Menú y viaja con el pedido).
        var mesaActual by remember { mutableStateOf("4") }
        // Mensaje de error del login (nulo = todo bien, no se muestra nada).
        var errorLogin by remember { mutableStateOf<String?>(null) }
        // En la vista previa no hay base de datos (no hay app corriendo),
        // así que se permite que sea nula y se muestran las pantallas sin datos.
        val db = remember {
            try { buildDatabase() } catch (e: Exception) { null }
        }
        val repositorio = remember(db) { db?.let { SazonRepository(it) } }
        val scope = rememberCoroutineScope()
        
        val carrito = remember { CarritoCompra() }

        LaunchedEffect(db) {
            repositorio?.seedIfEmpty()
        }

        when (pantallaActual){
            "login" -> LoginScreen(
                onLogin = { empleadoIdIngresado, pinIngresado ->
                    errorLogin = null
                    scope.launch {
                        val empleado = repositorio?.login(empleadoIdIngresado, pinIngresado)
                        if (empleado != null) {
                            // El rol dice en qué área trabaja (Mesero/Cocina).
                            // El ID dice si es administrador (ADM-), mesero (MSR-) o cocina (CCN-).
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
                repositorio = repositorio,
                empleado = empleadoLogueado
            )

            "dashboardCocina" -> DashboardCocina(
                onLogoutClick = {
                    empleadoLogueado = null
                    pantallaActual = "login"
                },
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
                mesa = mesaActual
            )
            "pedidoConcretado" -> PedidoConcretadoScreen(
                volverDashboard = {pantallaActual = "dashboardMesero"},
                otroPedido = {pantallaActual = "menuMesero"},
                pedidoId = ultimoPedidoId,
                repositorio = repositorio
            )
            "asistente" -> AsistenteScreen(
                backAsistente = {pantallaActual = "dashboardMesero"}
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
                }
            )
        }
    }
}