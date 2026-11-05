package org.sazontotal.project.data.maps

import kotlin.math.ceil

// Cada kilometro iniciado cuesta S/ 3; incluso la ruta mas corta paga un tramo.
fun kilometrosTarifados(distanciaKm: Double): Int {
    require(distanciaKm.isFinite() && distanciaKm >= 0.0) { "Distancia de entrega no válida" }
    return ceil(distanciaKm).coerceAtLeast(1.0).toInt()
}

fun costoDelivery(distanciaKm: Double): Double = kilometrosTarifados(distanciaKm) * 3.0
