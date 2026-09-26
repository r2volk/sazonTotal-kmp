package org.sazontotal.project.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "platos")
data class PlatoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val descripcion: String = "",
    val precio: Double,
    val categoria: String = "Platos", // "Platos", "Bebidas", "Postres" (tus filtros de MenuScreen)
    val activo: Boolean = true
)
