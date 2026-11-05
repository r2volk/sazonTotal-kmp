package org.sazontotal.project.utils

import androidx.compose.runtime.Composable

// atras del sistema con expect/actual: android lo escucha, en ios no aplica.
@Composable
expect fun BackHandlerApp(enabled: Boolean, onBack: () -> Unit)
