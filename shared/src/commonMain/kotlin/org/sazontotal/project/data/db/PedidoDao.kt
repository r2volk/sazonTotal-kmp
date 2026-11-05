package org.sazontotal.project.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface PedidoDao {
    // estas consultas leen y actualizan las cabeceras de pedidos.
    // al guardar devuelve el numero del pedido, con el se marcan luego los renglones.
    @Insert
    suspend fun insertar(pedido: PedidoEntity): Long

    @Insert
    suspend fun insertarItems(items: List<PedidoItemEntity>)

    @Insert
    suspend fun insertarEntrega(entrega: EntregaEntity): Long

    @Query("SELECT id FROM pedidos WHERE solicitudId = :solicitudId LIMIT 1")
    suspend fun buscarPorSolicitud(solicitudId: String): Long?

    @Transaction
    suspend fun insertarPedidoCompleto(pedido: PedidoEntity, items: List<PedidoItemEntity>, entrega: EntregaEntity?): Long {
        pedido.solicitudId?.let { buscarPorSolicitud(it)?.let { id -> return id } }
        val id = insertar(pedido)
        insertarItems(items.map { it.copy(pedidoId = id) })
        if (entrega != null) insertarEntrega(entrega.copy(id = 0, pedidoId = id))
        return id
    }

    @Query("SELECT * FROM pedidos ORDER BY id DESC")
    fun observarTodos(): Flow<List<PedidoEntity>>

    @Query("UPDATE pedidos SET estado = :nuevo WHERE id = :id AND estado = :anterior")
    suspend fun cambiarEstadoSiActual(id: Long, anterior: String, nuevo: String): Int

    @Query("SELECT * FROM pedidos WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: Long): PedidoEntity?

    // cuenta los pedidos; el sembrado lo usa para no meter ejemplos si ya hay reales.
    @Query("SELECT COUNT(*) FROM pedidos")
    suspend fun contar(): Int

    @Query("DELETE FROM pedidos WHERE id = :id")
    suspend fun borrarPorId(id: Long)
}
