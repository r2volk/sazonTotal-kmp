package org.sazontotal.project.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

// esta fila guarda una entrega de domicilio registrada en el modulo de reparto.
@Entity(tableName = "entregas", indices = [Index(value = ["pedidoId"], unique = true)])
data class EntregaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val direccion: String,
    val kmTexto: String,
    val minutosTexto: String,
    val fecha: Long = 0L,        // epoch millis (util Reloj.ahoraMillis())
    val entregada: Boolean = false,
    val meseroNombre: String,
    val pedidoId: Long? = null
)
