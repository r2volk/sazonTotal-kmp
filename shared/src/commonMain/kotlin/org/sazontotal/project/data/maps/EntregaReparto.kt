package org.sazontotal.project.data.maps

/**
 * una entrega de domicilio registrada en la pantalla de reparto.
 *
 * MODO EN MEMORIA: aun no se guarda en Room; la lista vive en App.kt y muere
 * al cerrar la app. cuando se conecte la base de datos se reemplaza por una
 * EntregaEntity sin cambiar la UI.
 */
data class EntregaReparto(
    val id: Long,
    val direccion: String,
    val kmTexto: String,
    val minutosTexto: String,
    val fecha: Long,          // epoch millis (util Reloj.ahoraMillis())
    val entregada: Boolean,
    val meseroNombre: String
)

// estado del chip de cada entrega en la lista.
const val ESTADO_EN_CAMINO = "En camino"
const val ESTADO_ENTREGADA = "Entregada"
