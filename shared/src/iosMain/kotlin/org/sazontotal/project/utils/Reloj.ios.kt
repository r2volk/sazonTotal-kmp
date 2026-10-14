package org.sazontotal.project.utils

import platform.CoreFoundation.CFAbsoluteTimeGetCurrent
import platform.CoreFoundation.kCFAbsoluteTimeIntervalSince1970
import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.dateWithTimeIntervalSince1970

actual fun ahoraMillis(): Long {
    val segundosUnix = CFAbsoluteTimeGetCurrent() + kCFAbsoluteTimeIntervalSince1970
    return (segundosUnix * 1000).toLong()
}

actual fun formatearHora(millis: Long): String {
    val fecha = NSDate.dateWithTimeIntervalSince1970(millis / 1000.0)
    val formato = NSDateFormatter()
    formato.dateFormat = "h:mm a"
    return formato.stringFromDate(fecha)
}
