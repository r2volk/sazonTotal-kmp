package org.sazontotal.project.utils

import android.content.Intent
import android.net.Uri
import org.sazontotal.project.data.db.contextoAndroid

// Android lanzar el navegador con un Intent; usa el contexto de aplicacion
// que MainActivity ya guardo al iniciar (initAndroidContext).
actual fun abrirEnExterno(url: String) {
    val contexto = contextoAndroid() ?: return
    contexto.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
}
