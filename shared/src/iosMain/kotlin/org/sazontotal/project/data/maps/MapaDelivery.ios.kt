package org.sazontotal.project.data.maps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import org.sazontotal.project.theme.LocalTemaOscuro
import org.sazontotal.project.theme.UsoColor
import org.sazontotal.project.theme.colorDeTema
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

@Composable
actual fun MapaDelivery(modifier: Modifier, destino: Coordenada?, direccion: String?) {
    val temaOscuro = LocalTemaOscuro.current
    Box(modifier.fillMaxSize().background(colorDeTema(Color(0xFF161619), temaOscuro, UsoColor.FONDO)), contentAlignment = Alignment.Center) {
        Text("El mapa interactivo está disponible en Android", color = colorDeTema(Color.White, temaOscuro), fontSize = 14.sp)
    }
}
