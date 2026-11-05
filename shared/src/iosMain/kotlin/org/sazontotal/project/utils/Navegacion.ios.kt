package org.sazontotal.project.utils

import androidx.compose.runtime.Composable

// ios no tiene boton atras del sistema: el gesto de retroceso pertenece al
// NavigationController de UIKit. como toda la app vive en una sola pantalla
// Compose, no hay nada que interceptar aqui.
@Composable
actual fun BackHandlerApp(enabled: Boolean, onBack: () -> Unit) {
    // sin-op
}
