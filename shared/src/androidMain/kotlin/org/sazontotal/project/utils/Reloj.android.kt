package org.sazontotal.project.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

actual fun ahoraMillis(): Long {
    return System.currentTimeMillis()
}

actual fun formatearHora(millis: Long): String {
    val formato = SimpleDateFormat("h:mm a", Locale.getDefault())
    return formato.format(Date(millis))
}

actual fun partesFecha(millis: Long): Triple<Int, Int, Int> {
    val calendario = java.util.Calendar.getInstance()
    calendario.timeInMillis = millis
    return Triple(
        calendario.get(java.util.Calendar.DAY_OF_MONTH),
        calendario.get(java.util.Calendar.MONTH) + 1,
        calendario.get(java.util.Calendar.YEAR)
    )
}
