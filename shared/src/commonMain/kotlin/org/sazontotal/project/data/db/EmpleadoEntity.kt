package org.sazontotal.project.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey


// cada instancia representa una fila de la tabla empleados.
@Entity(tableName = "empleados")
data class EmpleadoEntity(
    @PrimaryKey val id: String,
    val nombre: String,
    val pin: String,            // pin de 4 digitos usado al iniciar sesion
    val rol: String,            // etiqueta del perfil; la ruta inicial depende del prefijo del id
    val telefono: String = "",
    val activo: Boolean = true
)

fun esPinEmpleadoValido(pin: String): Boolean {
    return pin.length == 4 && pin.all { it in '0'..'9' }
}
