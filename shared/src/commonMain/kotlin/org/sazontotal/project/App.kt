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
import org.sazontotal.project.screens.screensMesero.EntregasScreen
import org.sazontotal.project.screens.screensMesero.RepartoScreen
import org.sazontotal.project.data.maps.EntregaReparto
import org.sazontotal.project.data.maps.aEntregaEntity
import org.sazontotal.project.data.maps.aEntregaReparto
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
        var direccionActual by remember { mutableStateOf("") }
        var distanciaDeliveryKm by remember { mutableStateOf(0.0) }
        var costoDelivery by remember { mutableStateOf(0.0) }
        var entregaDeliveryPendiente by remember { mutableStateOf<EntregaReparto?>(null) }

        var errorLogin by remember { mutableStateOf<String?>(null) }
        // en preview no hay base de datos, por eso puede ser nula (pantallas sin datos).

        val db = remember {
            try { buildDatabase() } catch (e: Exception) { null }
        }
        val repositorio = remember(db) { db?.let { SazonRepository(it) } }
        val scope = rememberCoroutineScope()

        val carrito = remember { CarritoCompra() }

        fun cerrarSesion() {
            carrito.limpiar()
            mesaActual = "4"
            direccionActual = ""
            distanciaDeliveryKm = 0.0
            costoDelivery = 0.0
            entregaDeliveryPendiente = null
            ultimoPedidoId = 0L
            empleadoLogueado = null
            errorLogin = null
            pantallaActual = "login"
        }

        fun iniciarDelivery() {
            mesaActual = "Delivery"
            direccionActual = ""
            distanciaDeliveryKm = 0.0
            costoDelivery = 0.0
            entregaDeliveryPendiente = null
            pantallaActual = "reparto"
        }

        fun cambiarDestino(nuevoDestino: String) {
            if (nuevoDestino == "Delivery") {
                iniciarDelivery()
            } else {
                mesaActual = nuevoDestino
                direccionActual = ""
                distanciaDeliveryKm = 0.0
                costoDelivery = 0.0
                entregaDeliveryPendiente = null
            }
        }

        // entregas del modulo reparto: ya viven en Room; este estado es solo el reflejo
        // de la tabla para las pantallas (el repositorio la observa con un Flow).
        var entregasReparto by remember { mutableStateOf<List<EntregaReparto>>(emptyList()) }

        // al iniciar se agregan las cuentas y datos de demostracion que falten.
        LaunchedEffect(db) {
            repositorio?.seedIfEmpty()
        }

        // la lista de entregas se actualiza sola cuando room inserta o borra algo.
        LaunchedEffect(repositorio) {
            repositorio?.observarEntregas()?.collect { filas ->
                entregasReparto = filas.map { fila ->
                    fila.entrega.aEntregaReparto().copy(
                        cancelada = fila.estadoPedido == "ANULADO",
                        preparando = fila.estadoPedido == "PENDIENTE" || fila.estadoPedido == "LISTO",
                        entregada = if (fila.entrega.pedidoId != null) fila.estadoPedido == "ENTREGADO" else fila.entrega.entregada
                    )
                }
            }
        }

        // atras del sistema: cada pantalla vuelve a su padre, no cierra toda la app.
        BackHandlerApp(enabled = pantallaActual != "login") {
            val padre = when (pantallaActual) {
                "gestionPersonal", "todosLosPedidos", "reportesAdmin", "editarMenu" -> "dashboardAdmin"
                "menuMesero" -> "dashboardMesero"
                "carritoMesero" -> "menuMesero"
                "pedidoConcretado" -> "dashboardMesero"
                "asistente" -> "dashboardMesero"
                "reparto" -> "dashboardMesero"
                "entregas" -> "menuMesero"
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
            if (padre == "login") cerrarSesion() else pantallaActual = padre
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
                onLogoutClick = { cerrarSesion() },
                nombreAdmin = empleadoLogueado?.nombre ?: "Karina Castillo",
                repositorio = repositorio
            )

            "dashboardMesero" -> DashboardMesero(
                onLogoutClick = { cerrarSesion() },
                onAsistenteClick = {pantallaActual ="asistente"},
                onRepartoClick = { iniciarDelivery() },
                onCarritoClick = {pantallaActual = "menuMesero"},
                onCambiarPinClick = {pantallaActual = "cambioPin"},
                repositorio = repositorio,
                empleado = empleadoLogueado,
                carrito = carrito
            )

            "dashboardCocina" -> DashboardCocina(
                onLogoutClick = { cerrarSesion() },
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
                onMesaChange = { cambiarDestino(it) },
                nombreMesero = empleadoLogueado?.nombre ?: "José P.",
                deliveryFee = costoDelivery,
                onHistorialReparto = { pantallaActual = "entregas" }
            )
            "carritoMesero" -> CarritoScreen(
                backCarritoScreen = {pantallaActual = "menuMesero"},
                confirmarCarrito = { id ->
                    ultimoPedidoId = id
                    entregaDeliveryPendiente = null
                    direccionActual = ""
                    distanciaDeliveryKm = 0.0
                    costoDelivery = 0.0
                    pantallaActual = "pedidoConcretado"
                },
                meseroId = empleadoLogueado?.id ?: "EMP-001",
                repositorio = repositorio,
                carrito = carrito,
                mesa = mesaActual,
                onMesaChange = { cambiarDestino(it) },
                direccion = direccionActual,
                onDireccionChange = { direccionActual = it },
                deliveryDistanceKm = distanciaDeliveryKm,
                deliveryFee = costoDelivery,
                entregaPendiente = entregaDeliveryPendiente?.aEntregaEntity()
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
            "reparto" -> RepartoScreen(
                backReparto = {pantallaActual = "dashboardMesero"},
                onCrearPedido = { direccion, distanciaKm, costo, entrega ->
                    mesaActual = "Delivery"
                    direccionActual = direccion
                    distanciaDeliveryKm = distanciaKm
                    costoDelivery = costo
                    entregaDeliveryPendiente = entrega
                    pantallaActual = "menuMesero"
                },
                onVerEntregas = {pantallaActual = "entregas"},
                nombreMesero = empleadoLogueado?.nombre ?: "Mesero",
                // resaltado del historial: cuantas quedan por entregar.
                entregasPendientes = entregasReparto.count { !it.entregada && !it.cancelada && !it.preparando }
            )
            "entregas" -> EntregasScreen(
                backEntregas = {pantallaActual = "menuMesero"},
                entregas = entregasReparto,
                onMarcarEntregada = { id ->
                    val pedidoId = entregasReparto.find { it.id == id }?.pedidoId
                    scope.launch {
                        if (pedidoId != null) repositorio?.cambiarEstadoPedido(pedidoId, "ENTREGADO")
                        else repositorio?.cambiarEntregada(id, true)
                    }
                },
                onDeshacerEntrega = { id ->
                    // por si marco entregada por error: vuelve a En camino.
                    scope.launch { repositorio?.cambiarEntregada(id, false) }
                },
                onEliminarEntrega = { id ->
                    scope.launch { repositorio?.borrarEntrega(id) }
                }
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
