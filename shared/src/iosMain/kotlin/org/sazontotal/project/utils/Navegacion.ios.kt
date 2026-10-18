package org.sazontotal.project.utils

import androidx.compose.runtime.Composable

// iOS no tiene boton atras del sistema: el gesto de retroceso pertenece al
// NavigationController de UIKit. Como toda la app vive en una sola pantalla
// Compose, no hay nada que interceptar aqui.
@Composable
actual fun BackHandlerApp(enabled: Boolean, onBack: () -> Unit) {
    // sin-op
}
