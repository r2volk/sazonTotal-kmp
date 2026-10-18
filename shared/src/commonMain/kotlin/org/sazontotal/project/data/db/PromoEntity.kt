package org.sazontotal.project.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

// un codigo de descuento: ej SAZON10 = 10% off.
@Entity(tableName = "promos")
data class PromoEntity(
    @PrimaryKey val codigo: String, // siempre en mayusculas, ej "SAZON10"
    val porcentaje: Int,            // 10 = 10% de descuento
    val activa: Boolean = true
)
