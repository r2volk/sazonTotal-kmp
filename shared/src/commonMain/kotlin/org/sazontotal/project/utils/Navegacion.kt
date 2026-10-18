package org.sazontotal.project.utils

import androidx.compose.runtime.Composable

// Boton atras del sistema.
//
// Cada plataforma lo resuelve distinto:
//  - Android: existe el gesto/boton fisico, hay que escucharlo.
//  - iOS: no hay "boton atras" del sistema (el gesto de retroceso es del
//    NavigationController de UIKit y no aplica a una sola pantalla Compose),
//    asi que aqui no hace falta hacer nada.
//
// Vive en utils/ con expect/actual, el mismo patron que usa el reloj.
@Composable
expect fun BackHandlerApp(enabled: Boolean, onBack: () -> Unit)
