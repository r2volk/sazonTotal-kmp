package org.sazontotal.project.screens.screensCocina

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import org.sazontotal.project.components.PerfilScreenUI
import org.sazontotal.project.components.PerfilStat
import org.sazontotal.project.data.db.EmpleadoEntity
import org.sazontotal.project.data.repository.SazonRepository
import org.sazontotal.project.theme.Colores

@Composable
fun CocinaPerfilScreen(
    onLogoutClick: () -> Unit,
    onCambiarPinClick: () -> Unit = {},
    empleado: EmpleadoEntity? = null,
    repositorio: SazonRepository? = null
) {

    // iniciales para el avatar ("Joaquin Gutierrez" -> "JG").
    val iniciales = empleado?.nombre
        ?.split(" ")
        ?.mapNotNull { palabra -> palabra.firstOrNull()?.uppercase() }
        ?.take(2)
        ?.joinToString("") ?: "??"

    // pedidos que cocina ya marco listos (trabajo del equipo).
    // el perfil consulta pedidos persistidos para mostrar el contador de cocina.
    val pedidosEnVivo = repositorio?.observarPedidos()
    val pedidos = pedidosEnVivo?.collectAsState(initial = emptyList())?.value ?: emptyList()
    val listos = pedidos.count { it.estado == "LISTO" }

    PerfilScreenUI(
        iniciales = iniciales,
        nombre = empleado?.nombre ?: "Sin sesión",
        rol = empleado?.rol ?: "—",
        empleadoId = empleado?.id ?: "—",
        accent = Color(0xFF64A8F5),
        stats = listOf(
            PerfilStat(valor = "18", etiqueta = "Turnos"),
            PerfilStat(valor = "$listos", etiqueta = "Pedidos"),
            PerfilStat(valor = "8m", etiqueta = "Prom. prep.", color = Colores.VerdePrimario)
        ),
        onCambiarPinClick = onCambiarPinClick,
        onLogoutClick = onLogoutClick
    )
}

@Preview
@Composable
fun CocinaPerfilScreenPreview() {
    CocinaPerfilScreen(
        onLogoutClick = {},
        empleado = EmpleadoEntity(id = "EMP-108", nombre = "María Rojas", pin = "1234", rol = "Cocina")
    )
}
