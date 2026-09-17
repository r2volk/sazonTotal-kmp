package org.sazontotal.project.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.sazontotal.project.components.StatCardAdmin
import org.sazontotal.project.components.AccesoRapidoCard
import org.sazontotal.project.icons.LogoutButton
import org.sazontotal.project.icons.MenuIcon
import org.sazontotal.project.icons.PedidosIcon
import org.sazontotal.project.icons.PersonalIcon
import org.sazontotal.project.icons.ReportesIcon

@Composable
fun DashboardAdmin(
    nombreAdmin: String = "Karina Castillo",
    onPersonalClick: () -> Unit = {},
    onPedidosClick: () -> Unit = {},
    onReportesClick: () -> Unit = {},
    onMenuClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .safeDrawingPadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 520.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 30.dp)
        ) {
            // Header: textos + logout + avatar KC
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Panel de administrador",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF8E8E93)
                    )
                    Text(
                        text = nombreAdmin,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                LogoutButton(
                    onLogout = onLogoutClick,
                    size = 48.dp
                )
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1C2E22)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "KC",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00E676)
                    )
                }
            }

            // Stats 2x2
            Column(
                modifier = Modifier.padding(top = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    StatCardAdmin(
                        textoSuperior = "10",
                        textoInferior = "Empleados activos",
                        colorSuperior = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    StatCardAdmin(
                        textoSuperior = "24",
                        textoInferior = "Pedidos hoy",
                        colorSuperior = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    StatCardAdmin(
                        textoSuperior = "S/ 842",
                        textoInferior = "Ventas del día",
                        colorSuperior = Color(0xFF00E676),
                        modifier = Modifier.weight(1f)
                    )
                    StatCardAdmin(
                        textoSuperior = "3",
                        textoInferior = "Pedidos pendientes",
                        colorSuperior = Color(0xFFFFA000),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Text(
                text = "Accesos rápidos",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF8E8E93),
                modifier = Modifier.padding(top = 24.dp, bottom = 14.dp)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                AccesoRapidoCard(
                    onClick = onPersonalClick,
                    icon = { PersonalIcon() },
                    texto = "Gestionar personal"
                )
                AccesoRapidoCard(
                    onClick = onPedidosClick,
                    icon = { PedidosIcon() },
                    texto = "Ver todos los pedidos"
                )
                AccesoRapidoCard(
                    onClick = onReportesClick,
                    icon = { ReportesIcon() },
                    texto = "Reportes"
                )
                AccesoRapidoCard(
                    onClick = onMenuClick,
                    icon = { MenuIcon() },
                    texto = "Editar menú"
                )
            }
        }
    }
}

@Preview
@Composable
fun DashboardAdminPreview() {
    DashboardAdmin()
}
