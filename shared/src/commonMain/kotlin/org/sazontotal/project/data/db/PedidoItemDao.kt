package org.sazontotal.project.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PedidoItemDao {
    // Guarda varios renglones de un solo viaje
    @Insert
    suspend fun insertarTodos(items: List<PedidoItemEntity>)

    // Los renglones de UN pedido (se buscan por su número)
    @Query("SELECT * FROM pedido_items WHERE pedidoId = :pedidoId")
    fun observarPorPedido(pedidoId: Long): Flow<List<PedidoItemEntity>>
}