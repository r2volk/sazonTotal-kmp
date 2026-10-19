package org.sazontotal.project.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PedidoDao {
    // al guardar devuelve el numero del pedido, con el se marcan luego los renglones.
    @Insert
    suspend fun insertar(pedido: PedidoEntity): Long

    @Query("SELECT * FROM pedidos ORDER BY id DESC")
    fun observarTodos(): Flow<List<PedidoEntity>>

    @Query("UPDATE pedidos SET estado = :estado WHERE id = :id")
    suspend fun cambiarEstado(id: Long, estado: String)

    @Query("SELECT * FROM pedidos WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: Long): PedidoEntity?

    // cuenta los pedidos; el sembrado lo usa para no meter ejemplos si ya hay reales.
    @Query("SELECT COUNT(*) FROM pedidos")
    suspend fun contar(): Int

    @Query("DELETE FROM pedidos WHERE id = :id")
    suspend fun borrarPorId(id: Long)
}
