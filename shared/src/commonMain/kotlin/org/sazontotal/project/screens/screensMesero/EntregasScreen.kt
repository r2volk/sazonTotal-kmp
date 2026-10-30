package org.sazontotal.project.screens.screensMesero

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.sazontotal.project.components.FiltroButton
import org.sazontotal.project.data.maps.ESTADO_ENTREGADA
import org.sazontotal.project.data.maps.ESTADO_EN_CAMINO
import org.sazontotal.project.data.maps.EntregaReparto
import org.sazontotal.project.utils.ahoraMillis

// paleta propia del modulo de reparto (igual que RepartoScreen).
private val VerdeModulo = Color(0xFF7BE382)
private val VerdeModuloOscuro = Color(0xFF143D2B)
private val Ambar = Color(0xFFFFB340)

// valores de los filtros de la lista.
private const val FILTRO_TODAS = "Todas"
private const val FILTRO_EN_CAMINO = ESTADO_EN_CAMINO
private const val FILTRO_ENTREGADAS = ESTADO_ENTREGADA

/**
 * entregas de domicilio del dia: lista filtrable (Todas / En camino /
 * Entregadas) con direccion, distancia/tiempo, quien la registro y su estado.
 *
 * MODO EN MEMORIA: la lista viene de App.kt (sin Room todavia). acciones por
 * tarjeta: marcar entregada, deshacer (volver a En camino) y eliminar.
 */
@Composable
fun EntregasScreen(
    backEntregas: () -> Unit,
    entregas: List<EntregaReparto>,
    onMarcarEntregada: (Long) -> Unit = {},
    onDeshacerEntrega: (Long) -> Unit = {},
    onEliminarEntrega: (Long) -> Unit = {}
) {
    var filtro by remember { mutableStateOf(FILTRO_TODAS) }
    val enCamino = entregas.count { !it.entregada }
    val entregadas = entregas.count { it.entregada }
    val visibles = when (filtro) {
        FILTRO_EN_CAMINO -> entregas.filter { !it.entregada }
        FILTRO_ENTREGADAS -> entregas.filter { it.entregada }
        else -> entregas
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(start = 20.dp, end = 20.dp, top = 60.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        EntregasHeader(
            resumen = resumenDe(entregas),
            onBack = backEntregas
        )

        // filtros de la lista (solo si hay entregas que filtrar).
        if (entregas.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FiltroButton(
                    activo = filtro == FILTRO_TODAS,
                    texto = "Todas (${entregas.size})",
                    onClick = { filtro = FILTRO_TODAS },
                    textoColor = VerdeModulo,
                    fondoTextoColor = VerdeModuloOscuro
                )
                FiltroButton(
                    activo = filtro == FILTRO_EN_CAMINO,
                    texto = "En camino ($enCamino)",
                    onClick = { filtro = FILTRO_EN_CAMINO },
                    textoColor = VerdeModulo,
                    fondoTextoColor = VerdeModuloOscuro
                )
                FiltroButton(
                    activo = filtro == FILTRO_ENTREGADAS,
                    texto = "Entregadas ($entregadas)",
                    onClick = { filtro = FILTRO_ENTREGADAS },
                    textoColor = VerdeModulo,
                    fondoTextoColor = VerdeModuloOscuro
                )
            }
        }

        if (entregas.isEmpty()) {
            EstadoVacioEntregas(modifier = Modifier.weight(1f))
        } else if (visibles.isEmpty()) {
            // hay entregas pero ninguna con el filtro activo.
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Nada en \"$filtro\" por ahora",
                    color = Color(0xFF8E8E93),
                    fontSize = 15.sp
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                visibles.forEach { entrega ->
                    EntregaCard(
                        entrega = entrega,
                        hace = textoHaceCuanto(entrega.fecha),
                        onAccion = {
                            if (entrega.entregada) onDeshacerEntrega(entrega.id)
                            else onMarcarEntregada(entrega.id)
                        },
                        onEliminar = { onEliminarEntrega(entrega.id) }
                    )
                }
            }
        }
    }
}

// linea de resumen del header: total y cuantas van en camino.
private fun resumenDe(entregas: List<EntregaReparto>): String {
    if (entregas.isEmpty()) return "Sin entregas todavía"
    val enCamino = entregas.count { !it.entregada }
    return if (enCamino == 0) "${entregas.size} entregas · todas entregadas"
    else "${entregas.size} entregas · $enCamino en camino"
}

// tiempo relativo simple para la tarjeta ("hace 15 min", "hace 1 h").
private fun textoHaceCuanto(fecha: Long): String {
    val minutos = ((ahoraMillis() - fecha) / 60000L).coerceAtLeast(0)
    return when {
        minutos < 1 -> "hace un momento"
        minutos < 60 -> "hace $minutos min"
        minutos < 24 * 60 -> "hace ${minutos / 60} h"
        else -> "hace ${minutos / 60 / 24} d"
    }
}

@Preview
@Composable
private fun EntregasVaciasPreview() {
    EntregasScreen(
        backEntregas = {},
        entregas = emptyList()
    )
}

@Composable
private fun EstadoVacioEntregas(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
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

@Composable
private fun EntregasHeader(
    resumen: String,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        IconButton(
            onClick = { onBack() },
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
                .background(VerdeModuloOscuro),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.History,
                contentDescription = null,
                tint = VerdeModulo,
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
    entrega: EntregaReparto,
    hace: String,
    onAccion: () -> Unit,
    onEliminar: () -> Unit
) {
    val entregada = entrega.entregada
    val acento = if (entregada) VerdeModulo else Ambar
    val badgeBg = if (entregada) Color(0xFF12331B) else Color(0xFF3A2B08)

    Column(
        modifier = Modifier
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
                    .background(if (entregada) VerdeModuloOscuro else Color(0xFF3D2905)),
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
                    text = entrega.direccion,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${entrega.kmTexto} · ${entrega.minutosTexto}",
                    color = Color(0xFF8E8E93),
                    fontSize = 13.sp
                )
            }
            Spacer(modifier = Modifier.size(8.dp))
            // chip de estado (En camino / Entregada).
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(badgeBg)
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (entregada) ESTADO_ENTREGADA else ESTADO_EN_CAMINO,
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

        Spacer(modifier = Modifier.size(8.dp))

        // fila de acciones: tiempo+mesero, eliminar y cambio de estado.
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$hace · ${entrega.meseroNombre}",
                color = Color(0xFF8E8E93),
                fontSize = 12.sp,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = onEliminar,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Eliminar entrega",
                    tint = Color(0xFFB4554E),
                    modifier = Modifier.size(18.dp)
                )
            }
            // accion principal de la tarjeta: marcar entregada / deshacer.
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(if (entregada) Color.Transparent else Color(0xFF12331B))
                    .border(
                        width = 1.dp,
                        color = if (entregada) Color(0xFF2C2C2E) else Color.Transparent,
                        shape = RoundedCornerShape(50)
                    )
                    .clickable { onAccion() }
                    .padding(horizontal = 12.dp, vertical = 7.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = if (entregada) Icons.Filled.Undo else Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = if (entregada) Color(0xFF8E8E93) else VerdeModulo,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = if (entregada) "Deshacer" else "Marcar entregada",
                        color = if (entregada) Color(0xFF8E8E93) else VerdeModulo,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun EntregasConDatosPreview() {
    val ahora = ahoraMillis()
    val MIN = 60_000L
    EntregasScreen(
        backEntregas = {},
        entregas = listOf(
            EntregaReparto(id = 1, direccion = "Calle Berlín 500, Miraflores", kmTexto = "2.7 km", minutosTexto = "~9 min", fecha = ahora - 15 * MIN, entregada = false, meseroNombre = "Ricardo S."),
            EntregaReparto(id = 2, direccion = "Av. Benavides 1200, Miraflores", kmTexto = "3.4 km", minutosTexto = "~11 min", fecha = ahora - 28 * MIN, entregada = false, meseroNombre = "José P."),
            EntregaReparto(id = 3, direccion = "Calle Schell 320, Miraflores", kmTexto = "1.2 km", minutosTexto = "~5 min", fecha = ahora - 60 * MIN, entregada = true, meseroNombre = "Ricardo S."),
            EntregaReparto(id = 4, direccion = "Av. Arequipa 2450, Lince", kmTexto = "4.8 km", minutosTexto = "~15 min", fecha = ahora - 120 * MIN, entregada = true, meseroNombre = "Luis T.")
        )
    )
}
