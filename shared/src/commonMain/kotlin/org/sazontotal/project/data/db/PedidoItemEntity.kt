package org.sazontotal.project.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey


// cada fila es un renglon y conserva los datos del plato usados al ordenar.
@Entity(tableName = "pedido_items")
data class PedidoItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val pedidoId: Long,        // id de la cabecera en pedidos
    val platoId: Long,         // id original del plato en el menu
    val nombrePlato: String,   // copia del nombre al momento de pedir
    val cantidad: Int,
    val nota: String = "",
    val precioUnit: Double     // copia del precio unitario con igv incluido
)
