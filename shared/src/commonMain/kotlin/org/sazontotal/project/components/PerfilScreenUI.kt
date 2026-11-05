package org.sazontotal.project.components

import org.sazontotal.project.theme.LocalTemaOscuro
import org.sazontotal.project.theme.UsoColor
import org.sazontotal.project.theme.colorDeTema
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.sazontotal.project.theme.Colores

private val PerfilSuperficie = Color(0xFF141417)
private val PerfilDivisor = Color(0xFF2A2A2E)
private val PerfilChipFondo = Color(0xFF191A1D)
private val PerfilRojoFondo = Color(0xFF2A0D0D)

data class PerfilStat(
    val valor: String,
    val etiqueta: String,
    val color: Color = Color.White
)

@Composable
fun PerfilScreenUI(
    iniciales: String,
    nombre: String,
    rol: String,
    empleadoId: String,
    accent: Color,
    stats: List<PerfilStat>,
    modifier: Modifier = Modifier,
    onCambiarPinClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    val temaOscuro = LocalTemaOscuro.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorDeTema(Color.Black, temaOscuro, UsoColor.FONDO))
            .padding(start = 20.dp, end = 20.dp, top = 60.dp, bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Perfil",
            color = colorDeTema(Color.White, temaOscuro, UsoColor.TEXTO),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        PerfilHeroCard(
            iniciales = iniciales,
            nombre = nombre,
            rol = rol,
            empleadoId = empleadoId,
            accent = accent
        )

        PerfilStatsCard(stats = stats)

        OpcionPerfilRow(
            icono = Icons.Filled.Lock,
            texto = "Cambiar PIN",
            onClick = onCambiarPinClick,
            trailing = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = colorDeTema(Color(0xFF6E6E73), temaOscuro, UsoColor.TEXTO),
                    modifier = Modifier.size(20.dp)
                )
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        CerrarSesionButton(onClick = onLogoutClick)
    }
}

@Composable
private fun PerfilHeroCard(
    iniciales: String,
    nombre: String,
    rol: String,
    empleadoId: String,
    accent: Color
) {
    val temaOscuro = LocalTemaOscuro.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(colorDeTema(accent.copy(alpha = 0.10f), temaOscuro, UsoColor.FONDO))
            .border(1.dp, colorDeTema(accent.copy(alpha = 0.22f), temaOscuro, UsoColor.BORDE), RoundedCornerShape(28.dp))
            .padding(horizontal = 20.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .background(colorDeTema(accent.copy(alpha = 0.14f), temaOscuro, UsoColor.FONDO))
                .border(2.dp, colorDeTema(accent, temaOscuro, UsoColor.BORDE), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = iniciales,
                color = colorDeTema(accent, temaOscuro, UsoColor.TEXTO),
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = nombre,
            color = colorDeTema(Color.White, temaOscuro, UsoColor.TEXTO),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(colorDeTema(accent.copy(alpha = 0.18f), temaOscuro, UsoColor.FONDO))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = rol,
                    color = colorDeTema(accent, temaOscuro, UsoColor.TEXTO),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(colorDeTema(PerfilChipFondo, temaOscuro, UsoColor.FONDO))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "ID: $empleadoId",
                    color = colorDeTema(Colores.TextoTenue, temaOscuro, UsoColor.TEXTO),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun PerfilStatsCard(stats: List<PerfilStat>) {
    val temaOscuro = LocalTemaOscuro.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(colorDeTema(PerfilSuperficie, temaOscuro, UsoColor.FONDO))
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        stats.forEachIndexed { index, stat ->
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = stat.valor,
                    color = colorDeTema(stat.color, temaOscuro, UsoColor.TEXTO),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stat.etiqueta,
                    color = colorDeTema(Colores.TextoTenue, temaOscuro, UsoColor.TEXTO),
                    fontSize = 11.sp
                )
            }
            if (index != stats.lastIndex) {
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(32.dp)
                        .background(colorDeTema(PerfilDivisor, temaOscuro, UsoColor.FONDO))
                )
            }
        }
    }
}

@Composable
private fun CerrarSesionButton(onClick: () -> Unit) {
    val temaOscuro = LocalTemaOscuro.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(50))
            .background(colorDeTema(PerfilRojoFondo, temaOscuro, UsoColor.FONDO))
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
            contentDescription = null,
            tint = colorDeTema(Colores.Rojo, temaOscuro, UsoColor.TEXTO),
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Cerrar sesión",
            color = colorDeTema(Colores.Rojo, temaOscuro, UsoColor.TEXTO),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
    }
}