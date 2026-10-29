package org.sazontotal.project.utils

// abre una url fuera de la app: el navegador con la ruta de Google Maps o
// Waze. expect/actual como el resto del proyecto (Navegacion.kt, Reloj.kt).
expect fun abrirEnExterno(url: String)
