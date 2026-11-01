package org.sazontotal.project.data

private const val PREFIJO_DELIVERY = "Delivery: "

fun esDelivery(mesa: String): Boolean = mesa.startsWith(PREFIJO_DELIVERY)

fun direccionDelivery(mesa: String): String = mesa.removePrefix(PREFIJO_DELIVERY)

fun destinoPedido(mesa: String): String = if (esDelivery(mesa)) "Delivery" else "Mesa $mesa"

fun crearDestinoDelivery(direccion: String): String = PREFIJO_DELIVERY + direccion.trim()

fun transicionPedidoPermitida(mesa: String, actual: String, nuevo: String): Boolean = when (nuevo) {
    "LISTO" -> actual == "PENDIENTE"
    "EN_CAMINO" -> actual == "LISTO" && esDelivery(mesa)
    "ENTREGADO" -> if (esDelivery(mesa)) actual == "EN_CAMINO" else actual == "LISTO"
    "ANULADO" -> actual == "PENDIENTE" || actual == "LISTO" || actual == "EN_CAMINO"
    else -> false
}
