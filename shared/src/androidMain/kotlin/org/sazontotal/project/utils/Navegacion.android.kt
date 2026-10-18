package org.sazontotal.project.utils

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

// Android si tiene boton atras (y gesto), asi que se escucha directamente.
@Composable
actual fun BackHandlerApp(enabled: Boolean, onBack: () -> Unit) {
    BackHandler(enabled = enabled, onBack = onBack)
}
