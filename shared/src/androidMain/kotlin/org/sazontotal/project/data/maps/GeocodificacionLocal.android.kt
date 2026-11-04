package org.sazontotal.project.data.maps

import android.location.Geocoder
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.sazontotal.project.data.db.contextoAndroid
import java.util.Locale

@Suppress("DEPRECATION")
actual suspend fun geocodificarDireccionLocal(direccion: String): Coordenada? = withContext(Dispatchers.IO) {
    val context = contextoAndroid() ?: return@withContext null
    val geocoder = Geocoder(context, Locale("es", "PE"))
    val address = if (Build.VERSION.SDK_INT >= 33) {
        kotlinx.coroutines.suspendCancellableCoroutine { continuation ->
            geocoder.getFromLocationName(direccion, 1) { result ->
                continuation.resume(result.firstOrNull()) {}
            }
        }
    } else {
        geocoder.getFromLocationName(direccion, 1)?.firstOrNull()
    }
    address?.let { Coordenada(it.latitude, it.longitude) }
}

@Suppress("DEPRECATION")
actual suspend fun sugerirDireccionesLocales(textoParcial: String): List<String> = withContext(Dispatchers.IO) {
    val context = contextoAndroid() ?: return@withContext emptyList()
    val consulta = textoParcial.trim()
    if (consulta.length < 3) return@withContext emptyList()

    val geocoder = Geocoder(context, Locale("es", "PE"))
    val resultados = if (Build.VERSION.SDK_INT >= 33) {
        kotlinx.coroutines.suspendCancellableCoroutine { continuation ->
            geocoder.getFromLocationName(consulta, 5) { addresses ->
                val sugerencias = addresses
                    .mapNotNull { it.getAddressLine(0)?.trim()?.takeIf(String::isNotEmpty) }
                    .distinct()
                if (continuation.isActive) continuation.resume(sugerencias) {}
            }
        }
    } else {
        geocoder.getFromLocationName(consulta, 5)
            ?.mapNotNull { it.getAddressLine(0)?.trim()?.takeIf(String::isNotEmpty) }
            ?.distinct()
            .orEmpty()
    }
    resultados
}
