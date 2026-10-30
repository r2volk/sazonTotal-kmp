package org.sazontotal.project.utils

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

// iOS abre el enlace con UIApplication; la app Compose corre en el hilo
// principal, asi que no hace falta despachar a main.
actual fun abrirEnExterno(url: String) {
    val enlace = NSURL(string = url)
    UIApplication.sharedApplication.openURL(enlace)
}
