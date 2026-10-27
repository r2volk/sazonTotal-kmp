package org.sazontotal.project.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PedidoItemDao {
    // este dao guarda y consulta los platos que pertenecen a cada pedido.
    // guarda varios renglones de un solo viaje
    @Insert
    suspend fun insertarTodos(items: List<PedidoItemEntity>)

    // los renglones de un pedido (se buscan por su numero)
    @Query("SELECT * FROM pedido_items WHERE pedidoId = :pedidoId")
    fun observarPorPedido(pedidoId: Long): Flow<List<PedidoItemEntity>>

    // ranking para una ventana de fechas (semana o mes en reportes).
    @Query("""
        SELECT i.nombrePlato, SUM(i.cantidad) AS total
        FROM pedido_items i INNER JOIN pedidos p ON p.id = i.pedidoId
        WHERE p.estado = 'ENTREGADO' AND p.fecha > :desde AND p.fecha <= :hasta
        GROUP BY i.nombrePlato ORDER BY total DESC LIMIT 5
    """)
    fun observarTopPlatosEnRango(desde: Long, hasta: Long): Flow<List<VentaPlato>>

    // ranking del ano calendario local (igual que el grafico anual).
    @Query("""
        SELECT i.nombrePlato, SUM(i.cantidad) AS total
        FROM pedido_items i INNER JOIN pedidos p ON p.id = i.pedidoId
        WHERE p.estado = 'ENTREGADO'
          AND CAST(strftime('%Y', p.fecha / 1000, 'unixepoch', 'localtime') AS INTEGER) = :anio
        GROUP BY i.nombrePlato ORDER BY total DESC LIMIT 5
    """)
    fun observarTopPlatosEnAnio(anio: Int): Flow<List<VentaPlato>>
}
