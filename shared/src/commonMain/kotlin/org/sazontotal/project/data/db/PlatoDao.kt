package org.sazontotal.project.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlatoDao {
    @Query("SELECT * FROM platos WHERE activo = 1 ORDER BY nombre")
    fun observarActivos(): Flow<List<PlatoEntity>>

    @Query("SELECT * FROM platos WHERE nombre LIKE '%' || :texto || '%'")
    fun buscar(texto: String): Flow<List<PlatoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(plato: PlatoEntity)

    @Query("UPDATE platos SET activo = :activo WHERE id = :id")
    suspend fun cambiarActivo(id: Long, activo: Boolean)

    @Query("SELECT COUNT(*) FROM platos")
    suspend fun contar(): Int
}