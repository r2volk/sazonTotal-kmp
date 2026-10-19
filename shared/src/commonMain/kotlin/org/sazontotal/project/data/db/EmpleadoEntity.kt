package org.sazontotal.project.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "empleados")
data class EmpleadoEntity(
    @PrimaryKey val id: String,
    val nombre: String,
    val pin: String,            // PIN de 4 digitos del login
    val rol: String,            // "ADMIN" o "COCINERO" (String, no enum, para SQLite)
    val telefono: String = "",
    val activo: Boolean = true
)

fun esPinEmpleadoValido(pin: String): Boolean {
    return pin.length == 4 && pin.all { it in '0'..'9' }
}
