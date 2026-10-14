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
