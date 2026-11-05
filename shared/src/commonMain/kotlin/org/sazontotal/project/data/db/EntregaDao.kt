package org.sazontotal.project.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Embedded
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

data class EntregaConEstadoPedido(
    @Embedded val entrega: EntregaEntity,
    val estadoPedido: String?
)

@Dao
interface EntregaDao {
    // estas consultas leen y actualizan las entregas de domicilio.
    // al guardar devuelve el numero de la entrega generada por room.
    @Insert
    suspend fun insertar(entrega: EntregaEntity): Long

    // la mas reciente primero, por fecha. el id rompe empates.
    @Query("SELECT entregas.*, pedidos.estado AS estadoPedido FROM entregas LEFT JOIN pedidos ON pedidos.id = entregas.pedidoId ORDER BY entregas.fecha DESC, entregas.id DESC")
    fun observarTodas(): Flow<List<EntregaConEstadoPedido>>

    @Query("UPDATE entregas SET entregada = :entregada WHERE id = :id AND (pedidoId IS NULL OR EXISTS (SELECT 1 FROM pedidos WHERE pedidos.id = entregas.pedidoId AND pedidos.estado != 'ANULADO'))")
    suspend fun cambiarEntregada(id: Long, entregada: Boolean)

    @Query("DELETE FROM entregas WHERE id = :id AND pedidoId IS NULL")
    suspend fun borrarPorId(id: Long)

    // cuenta las entregas. el sembrado lo usa para no meter ejemplos si ya hay reales.
    @Query("SELECT COUNT(*) FROM entregas")
    suspend fun contar(): Int
}
