package org.sazontotal.project.screens.screensMesero

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import org.sazontotal.project.components.PerfilScreenUI
import org.sazontotal.project.components.PerfilStat
import org.sazontotal.project.data.db.EmpleadoEntity
import org.sazontotal.project.data.repository.SazonRepository
import org.sazontotal.project.theme.Colores

@Composable
fun MeseroPerfilScreen(
    onLogoutClick: () -> Unit = {},
    onCambiarPinClick: () -> Unit = {},
    empleado: EmpleadoEntity? = null,
    repositorio: SazonRepository? = null
) {

    // iniciales para el avatar ("Ricardo Sanchez" -> "RS").
    val iniciales = empleado?.nombre
        ?.split(" ")
        ?.mapNotNull { palabra -> palabra.firstOrNull()?.uppercase() }
        ?.take(2)
        ?.joinToString("") ?: "??"

    // pedidos tomados por este mesero (se buscan por su id).
    // el contador del perfil se calcula con los pedidos persistidos del mesero.
    val pedidosEnVivo = repositorio?.observarPedidos()
    val pedidos = pedidosEnVivo?.collectAsState(initial = emptyList())?.value ?: emptyList()
    val misPedidos = pedidos.count { it.meseroID == empleado?.id }

    PerfilScreenUI(
        iniciales = iniciales,
        nombre = empleado?.nombre ?: "Sin sesión",
        rol = empleado?.rol ?: "—",
        empleadoId = empleado?.id ?: "—",
        accent = Colores.VerdePrimario,
        stats = listOf(
            PerfilStat(valor = "22", etiqueta = "Turnos"),
            PerfilStat(valor = "$misPedidos", etiqueta = "Pedidos"),
            PerfilStat(valor = "6m", etiqueta = "Prom. prep.", color = Colores.VerdePrimario)
        ),
        onCambiarPinClick = onCambiarPinClick,
        onLogoutClick = onLogoutClick
    )
}

@Preview
@Composable
fun MeseroPerfilScreenPreview() {
    MeseroPerfilScreen(
        onLogoutClick = {},
        empleado = EmpleadoEntity(id = "EMP-104", nombre = "José Paredes", pin = "1234", rol = "Mesero")
    )
}
