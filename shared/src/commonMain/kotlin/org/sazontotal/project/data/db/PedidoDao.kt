package org.sazontotal.project.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PedidoDao {
    // Al guardar te devuelve el número que le tocó al pedido.
    // Con ese número marcas después los renglones.
    @Insert
    suspend fun insertar(pedido: PedidoEntity): Long

    @Query("SELECT * FROM pedidos ORDER BY id DESC")
    fun observarTodos(): Flow<List<PedidoEntity>>

    @Query("SELECT * FROM pedidos WHERE estado = :estado ORDER BY id DESC")
    fun observarPorEstado(estado: String): Flow<List<PedidoEntity>>

    @Query("UPDATE pedidos SET estado = :estado WHERE id = :id")
    suspend fun cambiarEstado(id: Long, estado: String)

    @Query("SELECT * FROM pedidos WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: Long): PedidoEntity?

    @Query("DELETE FROM pedidos WHERE id = :id")
    suspend fun borrarPorId(id: Long)

    // Suma solo de lo ENTREGADO (0 si no hay). Lo pendiente/anulado no es venta.
    // Es lista viva: cambia sola al vender.
    @Query("SELECT IFNULL(SUM(total), 0.0) FROM pedidos WHERE estado = 'ENTREGADO'")
    fun observarSumaTotal(): Flow<Double>
}
