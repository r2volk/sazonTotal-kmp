package org.sazontotal.project.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface EmpleadoDao {
    // solo entran los activos: un desactivado ya no pasa aunque su PIN sea correcto.
    @Query("SELECT * FROM empleados WHERE id = :id AND pin = :pin AND activo = 1 LIMIT 1")
    suspend fun login(id: String, pin: String): EmpleadoEntity?

    // existe sin importar si esta activo (para el seed: no revivir desactivados).
    @Query("SELECT * FROM empleados WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: String): EmpleadoEntity?

    // cuenta admins activos por rol o prefijo de id; si es 1, no se puede apagar (la app quedaria sin panel).
    @Query("SELECT COUNT(*) FROM empleados WHERE activo = 1 AND (rol = 'Administrador' OR id LIKE 'ADM-%')")
    suspend fun contarAdminsActivos(): Int

    @Query("SELECT * FROM empleados ORDER BY nombre")
    fun observarTodos(): Flow<List<EmpleadoEntity>>

    @Query("UPDATE empleados SET activo = :activo WHERE id = :id")
    suspend fun cambiarActivo(id: String, activo: Boolean)

    // cambia el PIN solo si el actual coincide; devuelve las filas afectadas.
    @Query("UPDATE empleados SET pin = :pinNuevo WHERE id = :id AND pin = :pinActual")
    suspend fun actualizarPin(id: String, pinActual: String, pinNuevo: String): Int

    // no reemplazar a otro empleado si se repite un ID.
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun guardar(empleado: EmpleadoEntity)

}
