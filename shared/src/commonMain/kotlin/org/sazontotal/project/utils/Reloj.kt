package org.sazontotal.project.utils

// expect/actual: la promesa va aqui, cada plataforma pone su "como"
expect fun ahoraMillis(): Long

// convierte esos milisegundos en hora legible ("2:14 pm").
expect fun formatearHora(millis: Long): String

// dia, mes (1-12) y ano de una fecha, en la zona horaria del telefono.
// sirve para agrupar ventas por dia o por mes en reportes.
expect fun partesFecha(millis: Long): Triple<Int, Int, Int>

// Usa la zona horaria local de cada plataforma y excluye fechas sin dato.
fun esHoy(fecha: Long, ahora: Long = ahoraMillis()): Boolean {
    return fecha > 0L && partesFecha(fecha) == partesFecha(ahora)
}

// texto listo para la pantalla: hora real, o "—" si el pedido es viejo (sin hora).
fun textoHora(fecha: Long): String {
    if (fecha == 0L) return "—"
    return formatearHora(fecha)
}
