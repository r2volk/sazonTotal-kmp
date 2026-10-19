package org.sazontotal.project.data.db

// forma del resultado de "platos mas vendidos" (nombre + total); no es una tabla.
data class VentaPlato(
    val nombrePlato: String,
    val total: Int
)
