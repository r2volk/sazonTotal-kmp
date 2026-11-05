package org.sazontotal.project.utils

// expect/actual: aqui va la promesa y cada plataforma pone su implementacion.
expect fun ahoraMillis(): Long

// convierte esos milisegundos en una hora legible, por ejemplo 2:14 pm.
expect fun formatearHora(millis: Long): String

// dia, mes de 1 a 12 y ano de una fecha local. agrupa ventas por dia o mes en reportes.
expect fun partesFecha(millis: Long): Triple<Int, Int, Int>

// usa la zona horaria local de cada plataforma y excluye fechas sin dato.
fun esHoy(fecha: Long, ahora: Long = ahoraMillis()): Boolean {
    return fecha > 0L && partesFecha(fecha) == partesFecha(ahora)
}

// texto listo para la pantalla: hora real o un guion si el pedido es viejo y no tiene hora.
fun textoHora(fecha: Long): String {
    if (fecha == 0L) return "—"
    return formatearHora(fecha)
}
