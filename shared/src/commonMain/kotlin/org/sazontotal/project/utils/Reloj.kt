package org.sazontotal.project.utils

// La hora actual en milisegundos. Cada plataforma la consigue a su manera
// (por eso es "expect": la promesa va aquí, el cómo va en cada plataforma).
expect fun ahoraMillis(): Long

// Convierte esos milisegundos en hora legible ("2:14 pm").
expect fun formatearHora(millis: Long): String

// Día, mes (1-12) y año de una fecha, en la zona horaria del teléfono.
// Sirve para agrupar ventas por día o por mes en Reportes.
expect fun partesFecha(millis: Long): Triple<Int, Int, Int>

// Texto listo para la pantalla: hora real, o "—" si el pedido es viejo (sin hora).
fun textoHora(fecha: Long): String {
    if (fecha == 0L) return "—"
    return formatearHora(fecha)
}
