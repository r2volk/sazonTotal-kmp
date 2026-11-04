package org.sazontotal.project.data.maps

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun MapaDelivery(
    modifier: Modifier = Modifier,
    destino: Coordenada?,
    direccion: String?
)
