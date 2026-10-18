package org.sazontotal.project.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "pedido_items")
data class PedidoItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val pedidoId: Long,
    val platoId: Long,         // que plato es
    val nombrePlato: String,   // copia al momento de pedir
    val cantidad: Int,
    val nota: String = "",
    val precioUnit: Double     // idem, para que no cambie si editan el plato
)
