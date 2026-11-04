package org.sazontotal.project.data.maps

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
actual fun MapaDelivery(modifier: Modifier, destino: Coordenada?, direccion: String?) {
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
        properties = MapProperties(isTrafficEnabled = false),
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
