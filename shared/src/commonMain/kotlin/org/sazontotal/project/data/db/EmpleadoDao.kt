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

    // cuantos administradores activos hay. si es 1, ese es el unico que puede
    // existir: apagarlo dejaria la app sin acceso al panel admin.
    // cuenta por rol O por prefijo de id (las dos formas de ser admin).
    @Query("SELECT COUNT(*) FROM empleados WHERE activo = 1 AND (rol = 'Administrador' OR id LIKE 'ADM-%')")
    suspend fun contarAdminsActivos(): Int

    @Query("SELECT * FROM empleados ORDER BY nombre")
    fun observarTodos(): Flow<List<EmpleadoEntity>>

    @Query("UPDATE empleados SET activo = :activo WHERE id = :id")
    suspend fun cambiarActivo(id: String, activo: Boolean)

    // solo cambia el PIN si el actual coincide (la verificacion vive en la misma consulta,
    // nadie puede cambiar el PIN de otro sin saber el suyo). devuelve las filas afectadas.
    @Query("UPDATE empleados SET pin = :pinNuevo WHERE id = :id AND pin = :pinActual")
    suspend fun actualizarPin(id: String, pinActual: String, pinNuevo: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(empleado: EmpleadoEntity)

}