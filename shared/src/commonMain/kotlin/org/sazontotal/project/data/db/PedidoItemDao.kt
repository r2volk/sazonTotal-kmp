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

    // Top 5 platos más vendidos: junta los renglones por nombre,
    // suma cantidades, ordena del mayor y trae 5.
    // Solo cuenta renglones de pedidos ENTREGADOS (lo demás no se vendió).
    @Query("SELECT i.nombrePlato, SUM(i.cantidad) AS total FROM pedido_items i INNER JOIN pedidos p ON p.id = i.pedidoId WHERE p.estado = 'ENTREGADO' GROUP BY i.nombrePlato ORDER BY total DESC LIMIT 5")
    fun observarTopPlatos(): Flow<List<VentaPlato>>
}