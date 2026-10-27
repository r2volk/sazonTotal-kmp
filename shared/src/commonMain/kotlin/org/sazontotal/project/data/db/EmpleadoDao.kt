package org.sazontotal.project.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface EmpleadoDao {
    // el dao contiene las consultas sql; el repositorio aplica reglas antes de llamarlas.
    // solo entran los activos; un empleado desactivado no pasa aunque su pin sea correcto.
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

    // cambia el pin solo si el actual coincide; devuelve las filas afectadas.
    @Query("UPDATE empleados SET pin = :pinNuevo WHERE id = :id AND pin = :pinActual")
    suspend fun actualizarPin(id: String, pinActual: String, pinNuevo: String): Int

    // no reemplazar a otro empleado si se repite un id.
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun guardar(empleado: EmpleadoEntity)

}
