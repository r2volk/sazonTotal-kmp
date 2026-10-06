package org.sazontotal.project.data.repository

import kotlinx.coroutines.flow.Flow
import org.sazontotal.project.data.db.EmpleadoEntity
import org.sazontotal.project.data.db.PedidoEntity
import org.sazontotal.project.data.db.PedidoItemEntity
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

    fun observarTodosLosPlatos(): Flow<List<PlatoEntity>>{
        return db.platoDao().observarTodos()
    }

    suspend fun guardarPlato(plato: PlatoEntity) {
        db.platoDao().guardar(plato)
    }

    suspend fun cambiarActivoPlato(id: Long, activo: Boolean){
        db.platoDao().cambiarActivo(id,activo)
    }

    suspend fun cambiarActivoEmpleado(id: String, activo: Boolean) {
        db.empleadoDao().cambiarActivo(id, activo)
    }

    suspend fun crearPedidoConItems(pedido: PedidoEntity, items: List<PedidoItemEntity>): Long {
        // 1. Guardamos la cabecera (mesa, mesero, estado).
        // La base le da un número y nos lo devuelve.
        val numeroPedido = db.pedidoDao().insertar(pedido)

        // 2. Recorremos los renglones y a cada uno le anotamos el número de su pedido.
        // .copy() = fotocopia del renglón cambiando solo ese número.
        val renglonesMarcados = items.map { renglon ->
            renglon.copy(pedidoId = numeroPedido)
        }

        // 3. Guardamos todos los renglones ya marcados, de un solo viaje.
        db.pedidoItemDao().insertarTodos(renglonesMarcados)

        // 4. Devolvemos el número, por si la pantalla lo necesita (ej: mostrar "Ticket #5").
        return numeroPedido
    }

    fun observarPedidos(): Flow<List<PedidoEntity>> {
        return db.pedidoDao().observarTodos()
    }

    fun observarItems(pedidoId: Long): Flow<List<PedidoItemEntity>> {
        return db.pedidoItemDao().observarPorPedido(pedidoId)
    }

    suspend fun cambiarEstadoPedido(id: Long, estado: String) {
        db.pedidoDao().cambiarEstado(id, estado)
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
