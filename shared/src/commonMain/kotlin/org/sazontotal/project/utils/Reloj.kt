package org.sazontotal.project.utils

// expect/actual: la promesa va aqui, cada plataforma pone su "como"
expect fun ahoraMillis(): Long

// convierte esos milisegundos en hora legible ("2:14 pm").
expect fun formatearHora(millis: Long): String

// dia, mes (1-12) y ano de una fecha, en la zona horaria del telefono.
// sirve para agrupar ventas por dia o por mes en reportes.
expect fun partesFecha(millis: Long): Triple<Int, Int, Int>

// texto listo para la pantalla: hora real, o "—" si el pedido es viejo (sin hora).
fun textoHora(fecha: Long): String {
    if (fecha == 0L) return "—"
    return formatearHora(fecha)
}
