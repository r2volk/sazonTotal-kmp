package org.sazontotal.project.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface EmpleadoDao {
    // Solo entran los activos: un desactivado ya no pasa aunque su PIN sea correcto.
    @Query("SELECT * FROM empleados WHERE id = :id AND pin = :pin AND activo = 1 LIMIT 1")
    suspend fun login(id: String, pin: String): EmpleadoEntity?

    // Existe sin importar si está activo (para el seed: no revivir desactivados).
    @Query("SELECT * FROM empleados WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: String): EmpleadoEntity?

    @Query("SELECT * FROM empleados ORDER BY nombre")
    fun observarTodos(): Flow<List<EmpleadoEntity>>

    @Query("UPDATE empleados SET activo = :activo WHERE id = :id")
    suspend fun cambiarActivo(id: String, activo: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(empleado: EmpleadoEntity)

}