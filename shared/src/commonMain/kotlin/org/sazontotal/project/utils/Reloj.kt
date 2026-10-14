package org.sazontotal.project.utils

// La hora actual en milisegundos. Cada plataforma la consigue a su manera
// (por eso es "expect": la promesa va aquí, el cómo va en cada plataforma).
expect fun ahoraMillis(): Long

// Convierte esos milisegundos en hora legible ("2:14 pm").
expect fun formatearHora(millis: Long): String

// Texto listo para la pantalla: hora real, o "—" si el pedido es viejo (sin hora).
fun textoHora(fecha: Long): String {
    if (fecha == 0L) return "—"
    return formatearHora(fecha)
}
