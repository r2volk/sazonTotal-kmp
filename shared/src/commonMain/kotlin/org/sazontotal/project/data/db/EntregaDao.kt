package org.sazontotal.project.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EntregaDao {
    // estas consultas leen y actualizan las entregas de domicilio.
    // al guardar devuelve el numero de la entrega generada por room.
    @Insert
    suspend fun insertar(entrega: EntregaEntity): Long

    // la mas reciente primero (por fecha; el id rompe empates).
    @Query("SELECT * FROM entregas ORDER BY fecha DESC, id DESC")
    fun observarTodas(): Flow<List<EntregaEntity>>

    @Query("UPDATE entregas SET entregada = :entregada WHERE id = :id")
    suspend fun cambiarEntregada(id: Long, entregada: Boolean)

    @Query("DELETE FROM entregas WHERE id = :id")
    suspend fun borrarPorId(id: Long)

    // cuenta las entregas; el sembrado lo usa para no meter ejemplos si ya hay reales.
    @Query("SELECT COUNT(*) FROM entregas")
    suspend fun contar(): Int
}
