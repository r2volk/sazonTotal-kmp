package org.sazontotal.project.data.repository

import kotlinx.coroutines.flow.Flow
import org.sazontotal.project.data.db.EmpleadoEntity
import org.sazontotal.project.data.db.PedidoEntity
import org.sazontotal.project.data.db.PedidoItemEntity
import org.sazontotal.project.data.db.PlatoEntity
import org.sazontotal.project.data.db.PromoEntity
import org.sazontotal.project.data.db.SazonDatabase
import org.sazontotal.project.data.db.VentaPlato

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

    // Borra un plato por su id. Los pedidos viejos no se afectan
    // porque cada renglón guarda su propia copia de nombre y precio.
    suspend fun borrarPlato(id: Long) {
        db.platoDao().borrarPorId(id)
    }

    suspend fun cambiarActivoPlato(id: Long, activo: Boolean){
        db.platoDao().cambiarActivo(id,activo)
    }

    suspend fun cambiarActivoEmpleado(id: String, activo: Boolean) {
        db.empleadoDao().cambiarActivo(id, activo)
    }

    // Trae UN pedido por su numero (no una lista)
    suspend fun buscarPedidoPorId(id: Long): PedidoEntity? {
        return db.pedidoDao().buscarPorId(id)
    }

    // Crea el pedido con sus renglones, todo junto:
    // si algo falla a la mitad, se borra la cabecera para no dejarla huérfana.
    // (Tu Room no da transacciones en código común, este rollback manual logra lo mismo.)
    suspend fun crearPedidoConItems(pedido: PedidoEntity, items: List<PedidoItemEntity>): Long {
        require(items.isNotEmpty()) { "El pedido no tiene renglones" }
        // 1. Guardamos la cabecera (mesa, mesero, estado).
        // La base le da un número y nos lo devuelve.
        val numeroPedido = db.pedidoDao().insertar(pedido)
        try {
            // 2. Recorremos los renglones y a cada uno le anotamos el número de su pedido.
            // .copy() = fotocopia del renglón cambiando solo ese número.
            val renglonesMarcados = items.map { renglon ->
                renglon.copy(pedidoId = numeroPedido)
            }

            // 3. Guardamos todos los renglones ya marcados, de un solo viaje.
            db.pedidoItemDao().insertarTodos(renglonesMarcados)
        } catch (e: Exception) {
            db.pedidoDao().borrarPorId(numeroPedido)
            throw e
        }

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

    // Suma viva de ventas para el dashboard del admin.
    fun observarVentasTotales(): Flow<Double> {
        return db.pedidoDao().observarSumaTotal()
    }

    // Top 5 platos más vendidos, lista viva para Reportes.
    fun observarTopPlatos(): Flow<List<VentaPlato>> {
        return db.pedidoItemDao().observarTopPlatos()
    }

    // Busca UN código de promo (sin importar mayúsculas/minúsculas).
    // Devuelve nulo si no existe o está apagado.
    suspend fun buscarPromo(codigo: String): PromoEntity? {
        return db.promoDao().buscarPorCodigo(codigo.trim().uppercase())
    }

    suspend fun seedIfEmpty() {
        val dao = db.empleadoDao()
        if (dao.buscarPorId("ADM-001") == null) {
            dao.guardar(EmpleadoEntity("ADM-001", "Karina Castillo", "1234", "Administrador", "", true))
        }
        if (dao.buscarPorId("MSR-001") == null) {
            dao.guardar(EmpleadoEntity("MSR-001", "Ricardo Sanchez", "4321", "Mesero", "", true))
        }

        if (dao.buscarPorId("CCN-001") == null) {
            dao.guardar(EmpleadoEntity("CCN-001", "Joaquin Gutierrez", "4321", "Cocina", "", true))
        }

        val platoDao = db.platoDao()
        if (platoDao.contar() == 0) {
            platoDao.guardar(PlatoEntity(id = 1, nombre = "Ceviche clásico", descripcion = "Sin gluten", precio = 12.0, categoria = "Platos", etiquetas = "Sin Gluten"))
            platoDao.guardar(PlatoEntity(id = 2, nombre = "Lomo saltado", descripcion = "Picante leve", precio = 18.0, categoria = "Platos", etiquetas = "Picante"))
            platoDao.guardar(PlatoEntity(id = 3, nombre = "Pisco sour", descripcion = "Con alcohol", precio = 20.0, categoria = "Bebidas"))
            platoDao.guardar(PlatoEntity(id = 4, nombre = "Suspiro limeño", descripcion = "Contiene lácteos", precio = 13.0, categoria = "Postres", etiquetas = "Lácteo"))
        }

        val promoDao = db.promoDao()
        if (promoDao.buscarPorCodigo("SAZON10") == null) {
            promoDao.guardar(PromoEntity("SAZON10", 10))
        }
        if (promoDao.buscarPorCodigo("MESERO15") == null) {
            promoDao.guardar(PromoEntity("MESERO15", 15))
        }
    }
}
