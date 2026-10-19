package org.sazontotal.project.utils

import androidx.compose.runtime.Composable

// atras del sistema con expect/actual: Android lo escucha, en iOS no aplica.
@Composable
expect fun BackHandlerApp(enabled: Boolean, onBack: () -> Unit)
