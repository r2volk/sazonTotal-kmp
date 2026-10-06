package org.sazontotal.project.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "pedidos")
data class PedidoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val mesa: String,
    val meseroID: String,
    val estado: String,
    val total: Double = 0.0,
    val fecha: Long = 0L
)
