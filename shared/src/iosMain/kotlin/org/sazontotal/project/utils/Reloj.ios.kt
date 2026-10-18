package org.sazontotal.project.utils

import platform.CoreFoundation.CFAbsoluteTimeGetCurrent
import platform.CoreFoundation.kCFAbsoluteTimeIntervalSince1970
import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitDay
import platform.Foundation.NSCalendarUnitMonth
import platform.Foundation.NSCalendarUnitYear
import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter

actual fun ahoraMillis(): Long {
    val segundosUnix = CFAbsoluteTimeGetCurrent() + kCFAbsoluteTimeIntervalSince1970
    return (segundosUnix * 1000).toLong()
}

actual fun formatearHora(millis: Long): String {
    val fecha = NSDate(timeIntervalSinceReferenceDate = millis / 1000.0 - kCFAbsoluteTimeIntervalSince1970)
    val formato = NSDateFormatter()
    formato.dateFormat = "h:mm a"
    return formato.stringFromDate(fecha)
}

actual fun partesFecha(millis: Long): Triple<Int, Int, Int> {
    val fecha = NSDate(timeIntervalSinceReferenceDate = millis / 1000.0 - kCFAbsoluteTimeIntervalSince1970)
    val partes = NSCalendar.currentCalendar.components(
        NSCalendarUnitDay or NSCalendarUnitMonth or NSCalendarUnitYear,
        fromDate = fecha
    )
    return Triple(partes.day.toInt(), partes.month.toInt(), partes.year.toInt())
}
