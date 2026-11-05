package org.sazontotal.project.data.maps

import android.location.Address
import android.location.Geocoder
import android.os.Build
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runInterruptible
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import org.sazontotal.project.data.db.contextoAndroid
import java.util.Locale
import kotlin.coroutines.resume

@Suppress("DEPRECATION")
private suspend fun buscarConGeocoder(consulta: String, limite: Int): List<Address> {
    val context = contextoAndroid() ?: return emptyList()
    if (!Geocoder.isPresent()) return emptyList()
    val geocoder = Geocoder(context, Locale("es", "PE"))
    return try {
        withTimeoutOrNull(4_500) {
            if (Build.VERSION.SDK_INT >= 33) {
                suspendCancellableCoroutine { continuation ->
                    geocoder.getFromLocationName(consultaDireccion(consulta), limite,
                        -12.5, -77.4, -11.5, -76.6, object : Geocoder.GeocodeListener {
                            override fun onGeocode(addresses: MutableList<Address>) {
                                if (continuation.isActive) continuation.resume(addresses)
                            }
                            override fun onError(errorMessage: String?) {
                                if (continuation.isActive) continuation.resume(emptyList())
                            }
                        })
                }
            } else {
                runInterruptible(Dispatchers.IO) {
                    geocoder.getFromLocationName(consultaDireccion(consulta), limite,
                        -12.5, -77.4, -11.5, -76.6).orEmpty()
                }
            }
        }.orEmpty()
    } catch (e: CancellationException) { throw e }
    catch (_: Exception) { emptyList() }
}

actual suspend fun geocodificarDireccionLocal(direccion: String): Coordenada? =
    buscarConGeocoder(direccion, 1).firstOrNull()?.let { Coordenada(it.latitude, it.longitude) }

actual suspend fun sugerirDireccionesLocales(textoParcial: String): List<SugerenciaDireccion> {
    if (textoParcial.trim().length < 3) return emptyList()
    val numeroPedido = Regex("\\b\\d+[A-Za-z]?\\b").find(textoParcial)?.value
    return buscarConGeocoder(textoParcial, 6).mapNotNull { address ->
        if (!address.hasLatitude() || !address.hasLongitude() ||
            !coordenadaValida(address.latitude, address.longitude)) return@mapNotNull null
        val direccion = address.getAddressLine(0)?.trim()?.takeIf { it.isNotEmpty() } ?: return@mapNotNull null
        val calle = listOfNotNull(address.thoroughfare, address.subThoroughfare).joinToString(" ")
        val nombre = address.featureName?.takeUnless { it == address.subThoroughfare || it.all(Char::isDigit) }
            ?: calle.takeIf { it.isNotBlank() } ?: direccion.substringBefore(',')
        val numero = address.subThoroughfare
        SugerenciaDireccion(nombre, direccion, Coordenada(address.latitude, address.longitude),
            fuente = FuenteDireccion.DISPOSITIVO, numero = numero,
            aproximada = numeroPedido != null && numero != numeroPedido &&
                !Regex("\\b${Regex.escape(numeroPedido)}\\b").containsMatchIn(direccion))
    }
}
