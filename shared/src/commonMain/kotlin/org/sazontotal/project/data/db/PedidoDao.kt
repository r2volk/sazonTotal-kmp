package org.sazontotal.project.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PedidoDao {
    // al guardar te devuelve el numero que le toco al pedido.
    // con ese numero marcas despues los renglones.
    @Insert
    suspend fun insertar(pedido: PedidoEntity): Long

    @Query("SELECT * FROM pedidos ORDER BY id DESC")
    fun observarTodos(): Flow<List<PedidoEntity>>

    @Query("UPDATE pedidos SET estado = :estado WHERE id = :id")
    suspend fun cambiarEstado(id: Long, estado: String)

    @Query("SELECT * FROM pedidos WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: Long): PedidoEntity?

    // cuantos pedidos hay en total. lo usa el sembrado de datos de ejemplo
    // para no meterlos si el restaurante ya tiene pedidos reales.
    @Query("SELECT COUNT(*) FROM pedidos")
    suspend fun contar(): Int

    @Query("DELETE FROM pedidos WHERE id = :id")
    suspend fun borrarPorId(id: Long)
}
