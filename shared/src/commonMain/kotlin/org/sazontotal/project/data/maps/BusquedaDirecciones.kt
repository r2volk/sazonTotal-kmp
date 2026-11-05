package org.sazontotal.project.data.maps

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.json.*

enum class FuenteDireccion(val etiqueta: String) {
    GOOGLE("Google Maps"), OSM("OpenStreetMap"), DISPOSITIVO("Geocodificador del dispositivo")
}

/** La selección conserva la identidad y el punto que devolvió el proveedor. */
data class SugerenciaDireccion(
    val nombre: String,
    val direccion: String,
    val coordenada: Coordenada? = null,
    val placeId: String? = null,
    val fuente: FuenteDireccion,
    val numero: String? = null,
    val aproximada: Boolean = false
) {
    val textoCompleto: String get() = when {
        direccion.isBlank() -> nombre
        nombre.isBlank() || normalizarDireccion(direccion).contains(normalizarDireccion(nombre)) -> direccion
        else -> "$nombre, $direccion"
    }
}

internal fun normalizarDireccion(texto: String): String = texto.lowercase()
    .replace(Regex("\\bav\\.?\\s+"), "avenida ")
    .replace(Regex("\\bjr\\.?\\s+"), "jiron ")
    .replace('á', 'a').replace('é', 'e').replace('í', 'i').replace('ó', 'o')
    .replace('ú', 'u').replace('ü', 'u').replace('ñ', 'n')
    .replace(Regex("[^a-z0-9]+"), " ").trim()

fun consultaDireccion(texto: String): String {
    val expandida = texto.trim()
        .replace(Regex("\\bupn\\b", RegexOption.IGNORE_CASE), "Universidad Privada del Norte")
        .replace(Regex("\\bav\\.?\\s+", RegexOption.IGNORE_CASE), "Avenida ")
        .replace(Regex("\\bjr\\.?\\s+", RegexOption.IGNORE_CASE), "Jirón ")
    val normal = normalizarDireccion(expandida)
    return if (Regex("\\b(lima|peru)\\b").containsMatchIn(normal)) expandida else "$expandida, Lima, Perú"
}

internal fun coordenadaValida(lat: Double, lon: Double): Boolean =
    lat.isFinite() && lon.isFinite() && lat in -90.0..90.0 && lon in -180.0..180.0

internal fun leerSugerenciasPhoton(json: String, consulta: String): List<SugerenciaDireccion> {
    val root = Json.parseToJsonElement(json).jsonObject
    val pideNumero = Regex("\\b\\d+[A-Za-z]?\\b").find(consulta)?.value
    return root["features"]?.jsonArray.orEmpty().mapNotNull { feature ->
        val obj = feature.jsonObject
        val p = obj["properties"]?.jsonObject ?: return@mapNotNull null
        fun campo(key: String) = p[key]?.jsonPrimitive?.contentOrNull?.trim()?.takeIf { it.isNotEmpty() }
        val xy = obj["geometry"]?.jsonObject?.get("coordinates")?.jsonArray ?: return@mapNotNull null
        val lon = xy.getOrNull(0)?.jsonPrimitive?.doubleOrNull ?: return@mapNotNull null
        val lat = xy.getOrNull(1)?.jsonPrimitive?.doubleOrNull ?: return@mapNotNull null
        if (!coordenadaValida(lat, lon)) return@mapNotNull null
        val calle = listOfNotNull(campo("street"), campo("housenumber")).joinToString(" ")
        val nombre = campo("name") ?: calle.takeIf { it.isNotBlank() } ?: return@mapNotNull null
        val direccion = listOfNotNull(calle.takeIf { it.isNotBlank() && it != nombre },
            campo("district"), campo("city"), campo("state"), campo("country"))
            .distinctBy(::normalizarDireccion).joinToString(", ")
        SugerenciaDireccion(nombre, direccion, Coordenada(lat, lon), fuente = FuenteDireccion.OSM,
            numero = campo("housenumber"), aproximada = pideNumero != null && campo("housenumber") != pideNumero)
    }
}

internal fun leerSugerenciasGoogle(json: String): List<SugerenciaDireccion> =
    Json.parseToJsonElement(json).jsonObject["suggestions"]?.jsonArray.orEmpty().mapNotNull { item ->
        val place = item.jsonObject["placePrediction"]?.jsonObject ?: return@mapNotNull null
        val format = place["structuredFormat"]?.jsonObject
        fun texto(obj: JsonObject?, field: String) = obj?.get(field)?.jsonObject?.get("text")?.jsonPrimitive?.contentOrNull
        val nombre = texto(format, "mainText") ?: texto(place, "text") ?: return@mapNotNull null
        SugerenciaDireccion(nombre, texto(format, "secondaryText").orEmpty(),
            placeId = place["placeId"]?.jsonPrimitive?.contentOrNull, fuente = FuenteDireccion.GOOGLE)
    }

fun ordenarSugerencias(consulta: String, lugares: List<SugerenciaDireccion>): List<SugerenciaDireccion> {
    val tokens = normalizarDireccion(consultaDireccion(consulta)).split(" ").filter { it.length > 2 }
    val numero = Regex("\\b\\d+[A-Za-z]?\\b").find(consulta)?.value
    val ordenadas = lugares.sortedByDescending { lugar ->
        val etiqueta = normalizarDireccion(lugar.textoCompleto)
        tokens.count { etiqueta.contains(it) } * 12 +
            (if (numero != null && (lugar.numero == numero || Regex("\\b$numero\\b").containsMatchIn(etiqueta))) 90 else 0) +
            (if (lugar.fuente == FuenteDireccion.GOOGLE) 20 else if (lugar.fuente == FuenteDireccion.DISPOSITIVO) 15 else 0) -
            (if (lugar.aproximada) 60 else 0)
    }
    val unicas = mutableListOf<SugerenciaDireccion>()
    ordenadas.forEach { lugar ->
        val repetido = unicas.any { anterior ->
            normalizarDireccion(anterior.textoCompleto) == normalizarDireccion(lugar.textoCompleto) ||
                (normalizarDireccion(anterior.nombre) == normalizarDireccion(lugar.nombre) &&
                    anterior.coordenada != null && lugar.coordenada != null &&
                    distanciaEnKilometros(anterior.coordenada, lugar.coordenada) < 0.10)
        }
        if (!repetido) unicas.add(lugar)
    }
    return unicas.take(7)
}

/** Publica cada respuesta sin esperar a los proveedores lentos; la cancelación se propaga. */
internal suspend fun buscarEnParalelo(
    consulta: String,
    proveedores: List<suspend () -> List<SugerenciaDireccion>>,
    onResultados: (List<SugerenciaDireccion>) -> Unit,
    timeoutMillis: Long = 5_000
): List<SugerenciaDireccion> = supervisorScope {
    val respuestas = Channel<List<SugerenciaDireccion>>(proveedores.size.coerceAtLeast(1))
    proveedores.forEach { proveedor -> launch {
        val resultado = try { withTimeoutOrNull(timeoutMillis) { proveedor() }.orEmpty() }
        catch (e: CancellationException) { throw e }
        catch (_: Exception) { emptyList() }
        respuestas.send(resultado)
    } }
    val todos = mutableListOf<SugerenciaDireccion>()
    repeat(proveedores.size) {
        todos.addAll(respuestas.receive())
        currentCoroutineContext().ensureActive()
        onResultados(ordenarSugerencias(consulta, todos))
    }
    respuestas.close()
    ordenarSugerencias(consulta, todos)
}
