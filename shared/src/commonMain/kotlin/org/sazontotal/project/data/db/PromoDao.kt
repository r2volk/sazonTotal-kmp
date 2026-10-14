package org.sazontotal.project.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PromoDao {
    // Busca UN código (ya en mayúsculas y solo si está activo).
    @Query("SELECT * FROM promos WHERE codigo = :codigo AND activa = 1 LIMIT 1")
    suspend fun buscarPorCodigo(codigo: String): PromoEntity?

    @Query("SELECT * FROM promos WHERE activa = 1 ORDER BY codigo")
    fun observarActivas(): Flow<List<PromoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(promo: PromoEntity)
}
