package org.sazontotal.project.data.db

// Respuesta de la consulta "platos más vendidos".
// No es tabla: es solo la forma del resultado (nombre + total vendido).
data class VentaPlato(
    val nombrePlato: String,
    val total: Int
)
