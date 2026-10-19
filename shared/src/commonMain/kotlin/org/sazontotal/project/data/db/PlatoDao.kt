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

    @Query("SELECT * FROM platos ORDER BY nombre")
    fun observarTodos(): Flow<List<PlatoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(plato: PlatoEntity)

    @Query("UPDATE platos SET activo = :activo WHERE id = :id")
    suspend fun cambiarActivo(id: Long, activo: Boolean)

    @Query("SELECT COUNT(*) FROM platos")
    suspend fun contar(): Int

    @Query("SELECT COUNT(*) FROM platos WHERE id = :id AND activo = 1")
    suspend fun contarActivo(id: Long): Int

    // busca por nombre exacto para el sembrado: no depende de ids que el admin puede cambiar.
    @Query("SELECT * FROM platos WHERE nombre = :nombre LIMIT 1")
    suspend fun buscarPorNombre(nombre: String): PlatoEntity?

    @Query("DELETE FROM platos WHERE id = :id")
    suspend fun borrarPorId(id: Long)
}
