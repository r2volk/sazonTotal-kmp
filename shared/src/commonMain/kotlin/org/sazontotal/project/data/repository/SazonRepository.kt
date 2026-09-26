package org.sazontotal.project.data.repository

import kotlinx.coroutines.flow.Flow
import org.sazontotal.project.data.db.EmpleadoEntity
import org.sazontotal.project.data.db.PlatoEntity
import org.sazontotal.project.data.db.SazonDatabase

class SazonRepository(private val db: SazonDatabase) {

    suspend fun login(id: String, pin: String): EmpleadoEntity? {
        return db.empleadoDao().login(id.trim(), pin)
    }

    fun observarEmpleados(): Flow<List<EmpleadoEntity>> {
        return db.empleadoDao().observarTodos()
    }

    suspend fun guardarEmpleado(empleado: EmpleadoEntity) {
        db.empleadoDao().guardar(empleado)
    }

    fun observarPlatos(): Flow<List<PlatoEntity>> {
        return db.platoDao().observarActivos()
    }

    suspend fun guardarPlato(plato: PlatoEntity) {
        db.platoDao().guardar(plato)
    }

    suspend fun seedIfEmpty() {
        val dao = db.empleadoDao()
        if (dao.login("ADM-001", "1234") == null) {
            dao.guardar(EmpleadoEntity("ADM-001", "Karina Castillo", "1234", "Mesero", "", true))
        }
        if (dao.login("EMP-001", "4321") == null) {
            dao.guardar(EmpleadoEntity("EMP-001", "Ricardo Sanchez", "4321", "Cocina", "", true))
        }

        val platoDao = db.platoDao()
        if (platoDao.contar() == 0) {
            platoDao.guardar(PlatoEntity(id = 1, nombre = "Ceviche clásico", descripcion = "Sin gluten", precio = 12.0, categoria = "Platos"))
            platoDao.guardar(PlatoEntity(id = 2, nombre = "Lomo saltado", descripcion = "Picante leve", precio = 18.0, categoria = "Platos"))
            platoDao.guardar(PlatoEntity(id = 3, nombre = "Pisco sour", descripcion = "Con alcohol", precio = 20.0, categoria = "Bebidas"))
            platoDao.guardar(PlatoEntity(id = 4, nombre = "Suspiro limeño", descripcion = "Contiene lácteos", precio = 13.0, categoria = "Postres"))
        }
    }
}
