package org.sazontotal.project.screens.screensMesero

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// false = con entregas, true = estado vacío (solo diseño, tú conectas la lógica)
@Composable
fun EntregasScreen(
    modoVacio: Boolean = false
) {
    if (modoVacio) {
        EstadoVacioEntregas()
    } else {
        EstadoConEntregas()
    }
}

@Preview
@Composable
private fun EntregasScreenPreview() {
    EntregasScreen()
}

@Preview
@Composable
private fun EntregasVaciasPreview() {
    EntregasScreen(modoVacio = true)
}

@Composable
private fun EstadoConEntregas() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(start = 20.dp, end = 20.dp, top = 60.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        EntregasHeader(
            resumen = "4 entregas · Más activo: Ricardo S."
        )

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            EntregaCard(
                direccion = "Calle Berlín 500, Miraflores",
                detalle = "2.7 km · ~9 min",
                hace = "hace 15 min",
                repartidor = "Ricardo S.",
                entregada = false
            )
            EntregaCard(
                direccion = "Av. Benavides 1200, Miraflores",
                detalle = "3.4 km · ~11 min",
                hace = "hace 28 min",
                repartidor = "José P.",
                entregada = false
            )
            EntregaCard(
                direccion = "Calle Schell 320, Miraflores",
                detalle = "1.2 km · ~5 min",
                hace = "hace 1 h",
                repartidor = "Ricardo S.",
                entregada = true
            )
            EntregaCard(
                direccion = "Av. Arequipa 2450, Lince",
                detalle = "4.8 km · ~15 min",
                hace = "hace 2 h",
                repartidor = "Luis T.",
                entregada = true
            )
        }
    }
}

@Composable
private fun EstadoVacioEntregas() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(start = 20.dp, end = 20.dp, top = 60.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        EntregasHeader(
            resumen = "0 entregas"
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Aún no hay entregas hoy",
                    color = Color(0xFFB0B0B0),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Las entregas que registres\naparecerán aquí.",
                    color = Color(0xFF8E8E93),
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun EntregasHeader(
    resumen: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        IconButton(
            onClick = { },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Atrás",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF143D2B)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.History,
                contentDescription = null,
                tint = Color(0xFF7BE382),
                modifier = Modifier.size(24.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Entregas de hoy",
                color = Color.White,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = resumen,
                color = Color(0xFF8E8E93),
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun EntregaCard(
    direccion: String,
    detalle: String,
    hace: String,
    repartidor: String,
    entregada: Boolean,
    modifier: Modifier = Modifier
) {
    val acento = if (entregada) Color(0xFF7BE382) else Color(0xFFFFB340)
    val badgeBg = if (entregada) Color(0xFF12331B) else Color(0xFF3A2B08)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1C1C1E))
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (entregada) Color(0xFF143D2B) else Color(0xFF3D2905)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Home,
                    contentDescription = null,
                    tint = acento,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = direccion,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = detalle,
                    color = Color(0xFF8E8E93),
                    fontSize = 13.sp
                )
            }
            Spacer(modifier = Modifier.size(8.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(badgeBg)
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (entregada) "Entregada" else "En camino",
                    color = acento,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.size(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFF2C2C2E))
        )

        Spacer(modifier = Modifier.size(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = hace,
                color = Color(0xFF8E8E93),
                fontSize = 13.sp,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = repartidor,
                color = Color(0xFF8E8E93),
                fontSize = 13.sp
            )
        }
    }
}
