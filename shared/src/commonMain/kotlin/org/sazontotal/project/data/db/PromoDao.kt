package org.sazontotal.project.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PromoDao {
    // busca un codigo (ya en mayusculas y solo si esta activo).
    @Query("SELECT * FROM promos WHERE codigo = :codigo AND activa = 1 LIMIT 1")
    suspend fun buscarPorCodigo(codigo: String): PromoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(promo: PromoEntity)
}
