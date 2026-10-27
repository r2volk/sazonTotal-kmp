package org.sazontotal.project.data.repository

import kotlinx.coroutines.flow.Flow
import org.sazontotal.project.data.db.EmpleadoEntity
import org.sazontotal.project.data.db.esPinEmpleadoValido
import org.sazontotal.project.data.db.PedidoEntity
import org.sazontotal.project.data.db.PedidoItemEntity
import org.sazontotal.project.data.db.PlatoEntity
import org.sazontotal.project.data.db.SazonDatabase
import org.sazontotal.project.data.db.VentaPlato
import org.sazontotal.project.utils.ahoraMillis

// las pantallas llaman al repositorio; aqui se validan reglas y se coordinan los dao.
class SazonRepository(private val db: SazonDatabase) {

    suspend fun login(id: String, pin: String): EmpleadoEntity? {
        return db.empleadoDao().login(id.trim(), pin)
    }

    fun observarEmpleados(): Flow<List<EmpleadoEntity>> {
        return db.empleadoDao().observarTodos()
    }

    suspend fun guardarEmpleado(empleado: EmpleadoEntity) {
        require(empleado.nombre.isNotBlank()) { "El nombre del empleado es obligatorio" }
        require(esPinEmpleadoValido(empleado.pin)) {
            "El PIN debe tener exactamente 4 dígitos numéricos"
        }
        check(db.empleadoDao().buscarPorId(empleado.id) == null) {
            "Ya existe un empleado con ese ID"
        }
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

    // borra un plato por su id; los pedidos viejos no se afectan porque guardan su propia copia.
    suspend fun borrarPlato(id: Long) {
        db.platoDao().borrarPorId(id)
    }

    suspend fun cambiarActivoPlato(id: Long, activo: Boolean){
        db.platoDao().cambiarActivo(id,activo)
    }

    // no deja apagar al ultimo admin activo (la app quedaria sin acceso al panel); devuelve false si se bloqueo.
    suspend fun cambiarActivoEmpleadoSeguro(id: String, activo: Boolean): Boolean {
        val dao = db.empleadoDao()
        if (!activo) {
            val empleado = dao.buscarPorId(id)
            val esAdmin = empleado != null &&
                (empleado.rol == "Administrador" || empleado.id.startsWith("ADM-"))
            // es admin y es el unico activo -> no se deja apagar.
            if (esAdmin && dao.contarAdminsActivos() <= 1) {
                return false
            }
        }
        dao.cambiarActivo(id, activo)
        return true
    }

    // cambia el PIN solo si el actual es correcto; devuelve si se logro.
    suspend fun cambiarPin(id: String, pinActual: String, pinNuevo: String): Boolean {
        return db.empleadoDao().actualizarPin(id.trim(), pinActual, pinNuevo) > 0
    }

    // trae un pedido por su numero (no una lista)
    suspend fun buscarPedidoPorId(id: Long): PedidoEntity? {
        return db.pedidoDao().buscarPorId(id)
    }


    suspend fun crearPedidoConItems(pedido: PedidoEntity, items: List<PedidoItemEntity>): Long {
        require(items.isNotEmpty()) { "El pedido no tiene renglones" }
        require(items.all { it.cantidad > 0 && it.precioUnit >= 0.0 }) {
            "El pedido contiene cantidades o precios no válidos"
        }
        val platosActivos = items.all { db.platoDao().contarActivo(it.platoId) > 0 }
        require(platosActivos) { "Uno o más platos ya no están disponibles" }
        val numeroPedido = db.pedidoDao().insertar(pedido)
        try {
            // .copy() = copia del renglon cambiando solo el id del pedido
            val renglonesMarcados = items.map { renglon ->
                renglon.copy(pedidoId = numeroPedido)
            }
            db.pedidoItemDao().insertarTodos(renglonesMarcados)
        } catch (e: Exception) {
            db.pedidoDao().borrarPorId(numeroPedido)
            throw e
        }
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

    // ranking del top 5 limitado al mismo periodo de los reportes.
    fun observarTopPlatosEnRango(desde: Long, hasta: Long): Flow<List<VentaPlato>> {
        return db.pedidoItemDao().observarTopPlatosEnRango(desde, hasta)
    }

    fun observarTopPlatosEnAnio(anio: Int): Flow<List<VentaPlato>> {
        return db.pedidoItemDao().observarTopPlatosEnAnio(anio)
    }

    // agrega datos de demostracion faltantes sin reemplazar empleados existentes.
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

        sembrarPedidosDeEjemplo()
    }

    // pedidos de ejemplo si la base esta vacia; al cargar uno real ya no se siembran y se mezclan.
    private suspend fun sembrarPedidosDeEjemplo() {
        val pedidoDao = db.pedidoDao()
        if (pedidoDao.contar() > 0) return

        // se buscan por nombre, no por id: no se crea un renglon apuntando a un plato ya borrado o editado.
        val platoDao = db.platoDao()
        val ceviche = platoDao.buscarPorNombre("Ceviche clásico") ?: return
        val lomo = platoDao.buscarPorNombre("Lomo saltado") ?: return
        val pisco = platoDao.buscarPorNombre("Pisco sour") ?: return
        val suspiro = platoDao.buscarPorNombre("Suspiro limeño") ?: return

        val ahora = ahoraMillis()
        val MIN = 60_000L
        val DIA = 86_400_000L

        // del mas viejo al mas nuevo para que la lista por id descendente muestre lo reciente primero.
        crearPedidoDeEjemplo(
            estado = "ENTREGADO",
            mesa = "2",
            fecha = ahora - 10 * DIA,
            lineas = listOf(
                linea(ceviche, 2, ""),
                linea(suspiro, 2, "")
            )
        )
        crearPedidoDeEjemplo(
            estado = "ENTREGADO",
            mesa = "3",
            fecha = ahora - 40 * MIN,
            lineas = listOf(
                linea(ceviche, 2, "sin cebolla"),
                linea(lomo, 1, ""),
                linea(pisco, 2, "sin hielo")
            )
        )
        crearPedidoDeEjemplo(
            estado = "ENTREGADO",
            mesa = "7",
            fecha = ahora - 25 * MIN,
            lineas = listOf(
                linea(lomo, 2, "pico de gallo aparte"),
                linea(suspiro, 2, "")
            )
        )
        // este queda LISTO: el mesero ya puede marcarlo entregado en la demo.
        crearPedidoDeEjemplo(
            estado = "LISTO",
            mesa = "5",
            fecha = ahora - 12 * MIN,
            lineas = listOf(
                linea(ceviche, 1, ""),
                linea(pisco, 1, "bien frio")
            )
        )
        // este queda PENDIENTE: la cocina tiene algo real para marcar "Listo".
        crearPedidoDeEjemplo(
            estado = "PENDIENTE",
            mesa = "1",
            fecha = ahora - 3 * MIN,
            lineas = listOf(
                linea(lomo, 3, "uno sin aji"),
                linea(suspiro, 1, "")
            )
        )
    }

    // una linea del pedido: que plato, cuantas veces y con que nota.
    private fun linea(plato: PlatoEntity, cantidad: Int, nota: String) =
        PedidoItemEntity(
            pedidoId = 0L,
            platoId = plato.id,
            nombrePlato = plato.nombre,
            cantidad = cantidad,
            nota = nota,
            precioUnit = plato.precio
        )

    // el total del ejemplo suma directo los precios de sus renglones (ya incluyen IGV).
    private suspend fun crearPedidoDeEjemplo(
        estado: String,
        mesa: String,
        fecha: Long,
        lineas: List<PedidoItemEntity>
    ) {
        val total = lineas.sumOf { it.precioUnit * it.cantidad }

        val pedidoId = db.pedidoDao().insertar(
            PedidoEntity(
                mesa = mesa,
                meseroID = "MSR-001",
                estado = estado,
                total = total,
                fecha = fecha
            )
        )
        db.pedidoItemDao().insertarTodos(
            lineas.map { renglon -> renglon.copy(pedidoId = pedidoId) }
        )
    }
}
