package org.sazontotal.project.utils

import kotlin.math.abs
import kotlin.math.round

// formatea un monto con siempre 2 decimales: 12.5 -> "S/ 12.50"
fun moneda(valor: Double): String {
    return "S/ ${decimales(valor)}"
}

// Igual que moneda() pero sin el prefijo "S/ ", util cuando el precio va
// con otro texto adelante (ej: "Prom: 35.50").
fun decimales(valor: Double): String {
    // Se redondea a centimos y se separa entero/decimal con matematicas
    // enteras (funciona igual en Android, iOS y cualquier plataforma).
    val centimos = round(abs(valor) * 100.0).toLong()
    val entero = centimos / 100
    val resto = centimos % 100
    val signo = if (valor < 0) "-" else ""
    val decimalTexto = resto.toString().padStart(2, '0')
    return "$signo$entero.$decimalTexto"
}
