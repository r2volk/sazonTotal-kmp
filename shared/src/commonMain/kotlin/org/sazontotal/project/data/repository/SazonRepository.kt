package org.sazontotal.project.data.repository

import kotlinx.coroutines.flow.Flow
import org.sazontotal.project.data.db.EmpleadoEntity
import org.sazontotal.project.data.db.PedidoEntity
import org.sazontotal.project.data.db.PedidoItemEntity
import org.sazontotal.project.data.db.PlatoEntity
import org.sazontotal.project.data.db.PromoEntity
import org.sazontotal.project.data.db.SazonDatabase
import org.sazontotal.project.data.db.VentaPlato
import org.sazontotal.project.utils.ahoraMillis

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

    // borra un plato por su id. los pedidos viejos no se afectan
    // porque cada renglon guarda su propia copia de nombre y precio.
    suspend fun borrarPlato(id: Long) {
        db.platoDao().borrarPorId(id)
    }

    suspend fun cambiarActivoPlato(id: Long, activo: Boolean){
        db.platoDao().cambiarActivo(id,activo)
    }

    suspend fun cambiarActivoEmpleado(id: String, activo: Boolean) {
        db.empleadoDao().cambiarActivo(id, activo)
    }

    // Igual que cambiarActivoEmpleado, pero con una red de seguridad:
    // nunca se puede apagar al ULTIMO administrador activo, porque sin el
    // nadie podria entrar al panel admin y la app quedaria inutilizable
    // (habria que desinstalar para recuperarla).
    // Devuelve false cuando el cambio fue bloqueado, para que la pantalla avise.
    suspend fun cambiarActivoEmpleadoSeguro(id: String, activo: Boolean): Boolean {
        val dao = db.empleadoDao()
        if (!activo) {
            val empleado = dao.buscarPorId(id)
            val esAdmin = empleado != null &&
                (empleado.rol == "Administrador" || empleado.id.startsWith("ADM-"))
            // Es admin y es el unico activo -> no se deja apagar.
            if (esAdmin && dao.contarAdminsActivos() <= 1) {
                return false
            }
        }
        dao.cambiarActivo(id, activo)
        return true
    }

    // cambia el PIN de un empleado. la base solo aplica el cambio si el
    // PIN actual es correcto; devolvemos si se logro o no.
    suspend fun cambiarPin(id: String, pinActual: String, pinNuevo: String): Boolean {
        return db.empleadoDao().actualizarPin(id.trim(), pinActual, pinNuevo) > 0
    }

    // trae un pedido por su numero (no una lista)
    suspend fun buscarPedidoPorId(id: Long): PedidoEntity? {
        return db.pedidoDao().buscarPorId(id)
    }

    // crea el pedido con sus renglones, todo junto. si algo falla a la mitad
    // se borra la cabecera para no dejarla huerfana: Room no da transacciones
    // en codigo comun, asi que el rollback va a mano.
    suspend fun crearPedidoConItems(pedido: PedidoEntity, items: List<PedidoItemEntity>): Long {
        require(items.isNotEmpty()) { "El pedido no tiene renglones" }
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

    // top 5 platos mas vendidos, lista viva para reportes.
    fun observarTopPlatos(): Flow<List<VentaPlato>> {
        return db.pedidoItemDao().observarTopPlatos()
    }

    // busca un codigo de promo (sin importar mayusculas/minusculas).
    // devuelve nulo si no existe o esta apagado.
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

        sembrarPedidosDeEjemplo()
    }

    // pedidos de ejemplo para que la app no abra vacia. se meten solo si la
    // base no tiene ningun pedido: en cuanto el mesero carga uno real, ya no
    // se siembran mas y los de ejemplo se mezclan con los de verdad.
    private suspend fun sembrarPedidosDeEjemplo() {
        val pedidoDao = db.pedidoDao()
        if (pedidoDao.contar() > 0) return

        // se buscan por nombre y no por id: si el admin borro o edito un plato,
        // no se crea un renglon apuntando a algo que ya no existe.
        val platoDao = db.platoDao()
        val ceviche = platoDao.buscarPorNombre("Ceviche clásico") ?: return
        val lomo = platoDao.buscarPorNombre("Lomo saltado") ?: return
        val pisco = platoDao.buscarPorNombre("Pisco sour") ?: return
        val suspiro = platoDao.buscarPorNombre("Suspiro limeño") ?: return

        val ahora = ahoraMillis()
        val MIN = 60_000L
        val DIA = 86_400_000L

        // del mas viejo al mas nuevo, para que la lista (que sale por id
        // descendente) muestre primero lo recien hecho.
        crearPedidoDeEjemplo(
            estado = "ENTREGADO",
            mesa = "2",
            fecha = ahora - 10 * DIA,
            descuentoPorc = 15,
            lineas = listOf(
                linea(ceviche, 2, ""),
                linea(suspiro, 2, "")
            )
        )
        crearPedidoDeEjemplo(
            estado = "ENTREGADO",
            mesa = "3",
            fecha = ahora - 40 * MIN,
            descuentoPorc = 0,
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
            descuentoPorc = 10,
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
            descuentoPorc = 0,
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
            descuentoPorc = 0,
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

    // crea un pedido de ejemplo con su total ya calculado. el total usa la
    // misma cuenta que el carrito (subtotal - descuento, mas IGV de 18%), asi
    // que el ejemplo no descuadra con lo que veria el mesero al cobrar.
    private suspend fun crearPedidoDeEjemplo(
        estado: String,
        mesa: String,
        fecha: Long,
        descuentoPorc: Int,
        lineas: List<PedidoItemEntity>
    ) {
        val subtotal = lineas.sumOf { it.precioUnit * it.cantidad }
        val base = subtotal - subtotal * descuentoPorc / 100.0
        val total = base + base * 0.18

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
