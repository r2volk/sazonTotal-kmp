package org.sazontotal.project.data.maps

import org.sazontotal.project.data.db.EntregaEntity

/**
 * una entrega de domicilio registrada en la pantalla de reparto.
 *
 * MODELO DE PANTALLA: la fila real vive en Room como EntregaEntity; App.kt
 * convierte entre ambos con los maps de aqui abajo y la UI sigue igual.
 */
data class EntregaReparto(
    val id: Long,
    val direccion: String,
    val kmTexto: String,
    val minutosTexto: String,
    val fecha: Long,          // epoch millis (util Reloj.ahoraMillis())
    val entregada: Boolean,
    val meseroNombre: String,
    val pedidoId: Long? = null,
    val cancelada: Boolean = false,
    val preparando: Boolean = false
)

// estado del chip de cada entrega en la lista.
const val ESTADO_EN_CAMINO = "En camino"
const val ESTADO_ENTREGADA = "Entregada"
const val ESTADO_CANCELADA = "Cancelada"

// pasa una fila de Room al modelo que usan las pantallas.
fun EntregaEntity.aEntregaReparto(): EntregaReparto = EntregaReparto(
    id = id,
    direccion = direccion,
    kmTexto = kmTexto,
    minutosTexto = minutosTexto,
    fecha = fecha,
    entregada = entregada,
    meseroNombre = meseroNombre,
    pedidoId = pedidoId
)

// pasa el modelo de pantalla a fila de Room; el id va en 0 para que room genere el suyo.
fun EntregaReparto.aEntregaEntity(): EntregaEntity = EntregaEntity(
    id = 0L,
    direccion = direccion,
    kmTexto = kmTexto,
    minutosTexto = minutosTexto,
    fecha = fecha,
    entregada = entregada,
    meseroNombre = meseroNombre
)
