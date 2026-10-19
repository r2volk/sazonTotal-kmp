package org.sazontotal.project.utils

import kotlin.math.abs
import kotlin.math.round

// formatea un monto con siempre 2 decimales: 12.5 -> "S/ 12.50"
fun moneda(valor: Double): String {
    return "S/ ${decimales(valor)}"
}

// como moneda() pero sin "S/ ", para cuando el precio lleva otro texto adelante.
fun decimales(valor: Double): String {
    // redondeo a centimos con matematicas enteras: igual en Android, iOS y demas.
    val centimos = round(abs(valor) * 100.0).toLong()
    val entero = centimos / 100
    val resto = centimos % 100
    val signo = if (valor < 0) "-" else ""
    val decimalTexto = resto.toString().padStart(2, '0')
    return "$signo$entero.$decimalTexto"
}
