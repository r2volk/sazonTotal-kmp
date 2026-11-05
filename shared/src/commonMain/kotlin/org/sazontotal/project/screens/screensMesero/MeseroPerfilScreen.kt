package org.sazontotal.project.screens.screensMesero

import org.sazontotal.project.theme.LocalTemaOscuro
import org.sazontotal.project.theme.UsoColor
import org.sazontotal.project.theme.colorDeTema
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
    val temaOscuro = LocalTemaOscuro.current

    // iniciales para el avatar. por ejemplo, Ricardo Sanchez queda como RS.
    val iniciales = empleado?.nombre
        ?.split(" ")
        ?.mapNotNull { palabra -> palabra.firstOrNull()?.uppercase() }
        ?.take(2)
        ?.joinToString("") ?: "??"

    // pedidos tomados por este mesero, buscados por su id.
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
            PerfilStat(valor = "6m", etiqueta = "Prom. prep.", color = colorDeTema(Colores.VerdePrimario, temaOscuro, UsoColor.TEXTO))
        ),
        onCambiarPinClick = onCambiarPinClick,
        onLogoutClick = onLogoutClick
    )
}

@Preview
@Composable
fun MeseroPerfilScreenPreview() {
    val temaOscuro = LocalTemaOscuro.current
    MeseroPerfilScreen(
        onLogoutClick = {},
        empleado = EmpleadoEntity(id = "EMP-104", nombre = "José Paredes", pin = "1234", rol = "Mesero")
    )
}
