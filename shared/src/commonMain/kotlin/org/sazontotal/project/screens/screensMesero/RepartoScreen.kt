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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Navigation
import androidx.compose.material.icons.outlined.Route
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// true = buscando dirección (sugerencias), false = dirección elegida (mapa + botones)
@Composable
fun RepartoScreen(
    modoBusqueda: Boolean = false
) {
    if (modoBusqueda) {
        EstadoBuscando()
    } else {
        EstadoDireccionElegida()
    }
}

@Preview
@Composable
private fun RepartoScreenPreview() {
    RepartoScreen()
}

@Preview
@Composable
private fun RepartoBusquedaPreview() {
    RepartoScreen(modoBusqueda = true)
}

@Composable
private fun EstadoDireccionElegida() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(start = 20.dp, end = 20.dp, top = 60.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        RepartoHeader()
        BannerDemo()

        // Buscador con dirección ya elegida
        BuscadorReparto(
            texto = "Calle Berlín 500, Miraflores",
            conBordeVerde = false,
            onSearch = {}
        )

        // Mapa: espacio reservado (aquí irá Google Maps API)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF141414))
        )

        // Distancia y tiempo
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatReparto(
                icono = Icons.Outlined.Navigation,
                valor = "2.7 km",
                etiqueta = "distancia",
                iconoBg = Color(0xFF143D2B),
                iconoTint = Color(0xFF7BE382),
                modifier = Modifier.weight(1f)
            )
            StatReparto(
                icono = Icons.Filled.Timer,
                valor = "~9 min",
                etiqueta = "en moto",
                iconoBg = Color(0xFF3D2905),
                iconoTint = Color(0xFFFFB340),
                modifier = Modifier.weight(1f)
            )
        }

        // Abrir ruta
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF7BE382))
                .clickable { },
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Navigation,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Abrir ruta en Google Maps",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Waze + Registrar entrega
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF1C1C1E))
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Route,
                        contentDescription = null,
                        tint = Color(0xFFB0B0B0),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Waze",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Box(
                modifier = Modifier
                    .weight(1.2f)
                    .height(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF0E2B18))
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF7BE382),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Registrar entrega",
                        color = Color(0xFF7BE382),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun EstadoBuscando() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(start = 20.dp, end = 20.dp, top = 60.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        RepartoHeader()
        BannerDemo()

        // Buscador escribiendo (borde verde)
        BuscadorReparto(
            texto = "Calle Ber",
            conBordeVerde = true,
            onSearch = {}
        )

        // Sugerencias
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF1C1C1E))
        ) {
            val sugerencias = listOf(
                "Calle Berlín 500, Miraflores",
                "Calle Berlín 245, Miraflores",
                "Calle Berlín 810, Miraflores",
                "Av. Benavides 1200, Miraflores",
                "Calle Bellavista 150, Miraflores"
            )
            sugerencias.forEachIndexed { index, direccion ->
                SugerenciaRow(texto = direccion, onClick = {})
                if (index < sugerencias.lastIndex) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 68.dp)
                            .height(1.dp)
                            .background(Color(0xFF2C2C2E))
                    )
                }
            }
        }

        // Mapa vacío: elige dirección (aquí irá Google Maps API)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .drawBehind {
                    drawRoundRect(
                        color = Color(0xFF3A3A3C),
                        style = Stroke(
                            width = 2f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
                        ),
                        cornerRadius = CornerRadius(48f, 48f)
                    )
                }
                .clip(RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Elige una dirección para ver la ruta",
                color = Color(0xFF5A5A5E),
                fontSize = 15.sp
            )
        }
    }
}

@Composable
private fun RepartoHeader() {
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
                imageVector = Icons.Filled.TwoWheeler,
                contentDescription = null,
                tint = Color(0xFF7BE382),
                modifier = Modifier.size(24.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Pedidos a domicilio",
                color = Color.White,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF7BE382))
                )
                Text(
                    text = "Sede: Av. Larco 512, Miraflores",
                    color = Color(0xFF8E8E93),
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun BannerDemo() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF3A2B08))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Info,
            contentDescription = null,
            tint = Color(0xFFFFB340),
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = "Módulo en modo demostración",
            color = Color(0xFFFFB340),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun BuscadorReparto(
    texto: String,
    conBordeVerde: Boolean,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                if (conBordeVerde) {
                    drawRoundRect(
                        color = Color(0xFF7BE382),
                        style = Stroke(
                            width = 6f,
                            pathEffect = PathEffect.cornerPathEffect(48f)
                        ),
                        cornerRadius = CornerRadius(size.height / 2f, size.height / 2f)
                    )
                }
            }
            .clip(RoundedCornerShape(30.dp))
            .background(Color(0xFF1C1C1E))
            .padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.LocationOn,
            contentDescription = null,
            tint = Color(0xFF8E8E93),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.size(12.dp))
        Text(
            text = texto,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        if (conBordeVerde) {
            Text(
                text = "|",
                color = Color(0xFF8E8E93),
                fontSize = 16.sp,
                modifier = Modifier.padding(end = 4.dp)
            )
        }
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(if (conBordeVerde) Color(0xFF7BE382) else Color(0xFF235B31))
                .clickable { onSearch() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Buscar",
                tint = if (conBordeVerde) Color(0xFF0D2818) else Color(0xFF7BE382),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun SugerenciaRow(
    texto: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF2A2A2C)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.LocationOn,
                contentDescription = null,
                tint = Color(0xFF8E8E93),
                modifier = Modifier.size(20.dp)
            )
        }
        Text(
            text = texto,
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun StatReparto(
    icono: ImageVector,
    valor: String,
    etiqueta: String,
    iconoBg: Color,
    iconoTint: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(72.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1C1C1E))
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(iconoBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = iconoTint,
                modifier = Modifier.size(20.dp)
            )
        }
        Column {
            Text(
                text = valor,
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = etiqueta,
                color = Color(0xFF8E8E93),
                fontSize = 12.sp
            )
        }
    }
}
