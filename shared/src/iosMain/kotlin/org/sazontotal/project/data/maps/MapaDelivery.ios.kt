package org.sazontotal.project.data.maps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

@Composable
actual fun MapaDelivery(modifier: Modifier, destino: Coordenada?, direccion: String?) {
    Box(modifier.fillMaxSize().background(Color(0xFF161619)), contentAlignment = Alignment.Center) {
        Text("El mapa interactivo está disponible en Android", color = Color.White, fontSize = 14.sp)
    }
}
