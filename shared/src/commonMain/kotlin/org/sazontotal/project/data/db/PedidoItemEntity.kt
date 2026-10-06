package org.sazontotal.project.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "pedido_items")
data class PedidoItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val pedidoId: Long,
    val platoId: Long,         // qué plato es
    val nombrePlato: String,   // copia del nombre (foto del momento)
    val cantidad: Int,
    val nota: String = "",
    val precioUnit: Double     // copia del precio (foto del momento)
)
