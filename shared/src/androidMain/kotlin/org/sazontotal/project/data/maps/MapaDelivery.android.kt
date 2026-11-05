package org.sazontotal.project.data.maps

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.CameraPosition
import org.sazontotal.project.theme.LocalTemaOscuro
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
actual fun MapaDelivery(modifier: Modifier, destino: Coordenada?, direccion: String?) {
    val temaOscuro = LocalTemaOscuro.current
    val estiloMapa = remember(temaOscuro) {
        if (temaOscuro) MapStyleOptions(ESTILO_MAPA_OSCURO) else null
    }
    val sede = LatLng(SEDE_COORDENADA.latitud, SEDE_COORDENADA.longitud)
    val puntoDestino = destino?.let { LatLng(it.latitud, it.longitud) }
    var mapaListo by remember { mutableStateOf(false) }
    val camara = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(sede, 13f)
    }
    LaunchedEffect(puntoDestino, mapaListo) {
        if (!mapaListo) return@LaunchedEffect
        camara.animate(
            CameraUpdateFactory.newLatLngZoom(puntoDestino ?: sede, if (puntoDestino == null) 13f else 14f),
            700
        )
    }
    GoogleMap(
        modifier = modifier,
        cameraPositionState = camara,
        properties = MapProperties(
            isTrafficEnabled = false,
            mapStyleOptions = estiloMapa
        ),
        uiSettings = MapUiSettings(zoomControlsEnabled = true, compassEnabled = true),
        onMapLoaded = { mapaListo = true }
    ) {
        Marker(state = MarkerState(sede), title = NOMBRE_SEDE, snippet = DIRECCION_SEDE)
        if (puntoDestino != null) {
            Marker(state = MarkerState(puntoDestino), title = "Destino", snippet = direccion)
            Polyline(points = listOf(sede, puntoDestino), color = androidx.compose.ui.graphics.Color(0xFF30D158), width = 6f)
        }
    }
}

// estilo local: el SDK conserva rutas, marcadores, etiquetas y controles existentes.
private const val ESTILO_MAPA_OSCURO = """
[
  {"elementType":"geometry","stylers":[{"color":"#18181c"}]},
  {"elementType":"labels.text.fill","stylers":[{"color":"#d4d4d8"}]},
  {"elementType":"labels.text.stroke","stylers":[{"color":"#18181c"}]},
  {"featureType":"landscape","elementType":"geometry","stylers":[{"color":"#202024"}]},
  {"featureType":"road","elementType":"geometry","stylers":[{"color":"#34343c"}]},
  {"featureType":"road","elementType":"geometry.stroke","stylers":[{"color":"#24242b"}]},
  {"featureType":"road.highway","elementType":"geometry","stylers":[{"color":"#484852"}]},
  {"featureType":"poi.park","elementType":"geometry","stylers":[{"color":"#1a2b22"}]},
  {"featureType":"water","elementType":"geometry","stylers":[{"color":"#102832"}]}
]
"""
