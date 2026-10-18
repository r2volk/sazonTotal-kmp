package org.sazontotal.project.data.db

// respuesta de la consulta "platos mas vendidos".
// no es tabla: es solo la forma del resultado (nombre + total vendido).
data class VentaPlato(
    val nombrePlato: String,
    val total: Int
)
