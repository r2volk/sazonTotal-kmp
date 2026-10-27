package org.sazontotal.project.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

// cada instancia representa un producto guardado en el menu.
@Entity(tableName = "platos")
data class PlatoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val descripcion: String = "",
    val precio: Double, // precio final mostrado al cliente, con igv incluido
    val categoria: String = "Platos", // categoria usada para filtrar el menu
    val activo: Boolean = true,
    // etiquetas separadas por coma, por ejemplo sin gluten o picante.
    val etiquetas: String = ""
)
