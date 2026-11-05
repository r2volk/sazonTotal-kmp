package org.sazontotal.project.data.maps

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.add
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.round
import kotlin.math.sin
import kotlin.math.sqrt

// error con mensaje amable cuando falla algun servicio de Google Maps.
class ErrorMaps(mensaje: String) : Exception(mensaje)

// verde de la marca para el pin de la sede en el mapa estatico (0xRRGGBB).
private const val VERDE_PIN = "0x30D158"

// un punto geografico (latitud y longitud, coordenadas WGS84).
data class Coordenada(val latitud: Double, val longitud: Double)

// resultado del calculo de distancia: texto listo para mostrar.
data class ResultadoDistancia(
    val kmTexto: String,
    val minutosTexto: String,
    val distanciaKm: Double = 0.0
)

/**
 * cliente de los servicios de Google Maps Platform, mismo patron del
 * GeminiService: HTTP puro con Ktor, sin SDKs nativos.
 *
 * servicios usados:
 *  - Geocoding API: direccion escrita -> coordenadas
 *  - Places Autocomplete (New): sugerencias mientras se escribe
 *  - Distance Matrix API: km y minutos entre la sede y el cliente
 *  - Maps Static API: URL de la imagen del mapa con los dos pines
 *
 * Las búsquedas siempre consultan proveedores reales. Si Google no está disponible,
 * Photon y el geocodificador de plataforma publican sus resultados en paralelo.
 */
class GoogleMapsService(
    private val apiKey: String,
    private val sede: Coordenada = SEDE_COORDENADA
) {
    private val cliente = HttpClient {
        expectSuccess = false // el estado HTTP se revisa a mano tras cada respuesta
        // geocoding y distance matrix deben responder rapido; con 20 segundos basta.
        install(HttpTimeout) {
            connectTimeoutMillis = 15_000
            requestTimeoutMillis = 20_000
        }
    }

    // Sin clave, la búsqueda continúa con los proveedores públicos y de plataforma.
    private val modoDemo: Boolean
        get() = apiKey.isBlank() || apiKey.startsWith("PEGA_")

    private var placesNoDisponible = false

    suspend fun buscarDirecciones(
        consulta: String,
        onResultados: (List<SugerenciaDireccion>) -> Unit
    ): List<SugerenciaDireccion> = buscarEnParalelo(consulta, listOf(
        { autocompletar(consulta) },
        { autocompletarPhoton(consulta) },
        { sugerirDireccionesLocales(consulta) }
    ), onResultados)

    // Autocomplete está pensado para prefijos, nombres de lugares y direcciones.
    suspend fun autocompletar(textoParcial: String): List<SugerenciaDireccion> {
        if (modoDemo || placesNoDisponible) return emptyList()
        val cuerpo = buildJsonObject {
            put("input", consultaDireccion(textoParcial))
            put("languageCode", "es")
            put("includedRegionCodes", buildJsonArray { add("pe") })
            put("locationBias", buildJsonObject {
                put("circle", buildJsonObject {
                    put("center", buildJsonObject {
                        put("latitude", sede.latitud)
                        put("longitude", sede.longitud)
                    })
                    put("radius", 40000.0)
                })
            })
        }
        val respuesta = cliente.post("https://places.googleapis.com/v1/places:autocomplete") {
            header("X-Goog-Api-Key", apiKey)
            header("X-Goog-FieldMask", "suggestions.placePrediction.placeId,suggestions.placePrediction.text,suggestions.placePrediction.structuredFormat")
            contentType(ContentType.Application.Json)
            setBody(cuerpo.toString())
        }
        val json = respuesta.bodyAsText()
        if (!respuesta.status.isSuccess()) {
            if (respuesta.status.value == 403) placesNoDisponible = true
            throw ErrorMaps("No se pudieron consultar las sugerencias de Google Maps.")
        }
        return leerSugerenciasGoogle(json)
    }

    // Fuente real de respaldo: conserva nombre, dirección y coordenadas OSM.
    suspend fun autocompletarPhoton(textoParcial: String): List<SugerenciaDireccion> {
        if (textoParcial.trim().length < 3) return emptyList()
        val respuesta = cliente.get("https://photon.komoot.io/api/") {
            parameter("q", consultaDireccion(textoParcial))
            parameter("limit", 8)
            parameter("lat", sede.latitud)
            parameter("lon", sede.longitud)
            parameter("bbox", "-77.4,-12.5,-76.6,-11.5")
        }
        if (!respuesta.status.isSuccess()) throw ErrorMaps("No se pudieron consultar las direcciones. Intenta nuevamente.")
        return leerSugerenciasPhoton(respuesta.bodyAsText(), textoParcial)
    }

    suspend fun resolverSugerencia(lugar: SugerenciaDireccion): Coordenada {
        lugar.coordenada?.let { return it }
        if (lugar.placeId != null && !modoDemo && !placesNoDisponible) {
            try {
                val respuesta = cliente.get("https://places.googleapis.com/v1/places/${lugar.placeId}") {
                    header("X-Goog-Api-Key", apiKey)
                    header("X-Goog-FieldMask", "location")
                }
                if (respuesta.status.isSuccess()) {
                    val location = leerJson(respuesta.bodyAsText())?.getJsonObject("location")
                    val lat = location?.get("latitude")?.jsonPrimitive?.doubleOrNull
                    val lon = location?.get("longitude")?.jsonPrimitive?.doubleOrNull
                    if (lat != null && lon != null && coordenadaValida(lat, lon)) return Coordenada(lat, lon)
                }
            } catch (e: CancellationException) { throw e }
            catch (_: Exception) { /* Resolver el mismo lugar por dirección, sin escoger otro resultado. */ }
        }
        return geocodificar(lugar.textoCompleto)
    }

    // convierte la direccion elegida en coordenadas (Geocoding API).
    suspend fun geocodificar(direccion: String): Coordenada {
        if (modoDemo) throw ErrorMaps("Selecciona un resultado que incluya una ubicación.")

        val respuesta = cliente.get("https://maps.googleapis.com/maps/api/geocode/json") {
            parameter("address", consultaDireccion(direccion))
            parameter("components", "country:PE")
            parameter("region", "pe")
            parameter("language", "es")  // la direccion formateada viene en español
            parameter("key", apiKey)
        }
        val textoRespuesta = respuesta.bodyAsText()

        if (!respuesta.status.isSuccess()) {
            throw ErrorMaps(mensajeErrorDe(textoRespuesta, respuesta.status.value))
        }

        // la respuesta trae {"status":"OK","results":[{"geometry":{"location":{"lat":..,"lng":..}}}]}
        val raiz = leerJson(textoRespuesta)
        val estado = raiz?.get("status")?.jsonPrimitive?.contentOrNull ?: ""
        if (estado == "ZERO_RESULTS") {
            throw ErrorMaps("No se pudo encontrar esa dirección. Prueba con otra.")
        }
        if (estado != "OK") {
            throw ErrorMaps(mensajeEstadoGeocoding(estado))
        }

        val punto = raiz?.getArray("results")?.firstOrNull()
            ?.getJsonObjectOrNull("geometry")?.getJsonObjectOrNull("location")
            ?: throw ErrorMaps("Google no devolvió coordenadas para esa dirección.")

        val latitud = punto["lat"]?.jsonPrimitive?.contentOrNull?.toDoubleOrNull()
            ?: throw ErrorMaps("Google no devolvió coordenadas para esa dirección.")
        val longitud = punto["lng"]?.jsonPrimitive?.contentOrNull?.toDoubleOrNull()
            ?: throw ErrorMaps("Google no devolvió coordenadas para esa dirección.")
        return Coordenada(latitud = latitud, longitud = longitud)
    }

    // distancia y tiempo reales desde la sede hasta el cliente (Distance Matrix API).
    suspend fun distancia(hasta: Coordenada): ResultadoDistancia {
        if (modoDemo) {
            val km = haversineKm(sede, hasta)
            val minutos = (km / 22.0 * 60).toInt().coerceAtLeast(2) // moto promedio en Lima
            // redondeo a 1 decimal con aritmetica entera, multiplataforma.
            val kmRedondeado = round(km * 10.0).toInt() / 10.0
            return ResultadoDistancia(
                kmTexto = "$kmRedondeado km",
                minutosTexto = "~$minutos min en moto",
                distanciaKm = km
            )
        }

        val respuesta = cliente.get("https://maps.googleapis.com/maps/api/distancematrix/json") {
            parameter("origins", "${sede.latitud},${sede.longitud}")
            parameter("destinations", "${hasta.latitud},${hasta.longitud}")
            parameter("mode", "driving")
            parameter("language", "es")
            parameter("key", apiKey)
        }
        val textoRespuesta = respuesta.bodyAsText()

        if (!respuesta.status.isSuccess()) {
            throw ErrorMaps(mensajeErrorDe(textoRespuesta, respuesta.status.value))
        }

        // la respuesta trae {"status":"OK","rows":[{"elements":[{"status","distance","duration"}]}]}
        val raiz = leerJson(textoRespuesta)
            ?: throw ErrorMaps("Google Maps no respondió bien. Intenta la ruta otra vez.")
        val estadoGeneral = raiz.get("status")?.jsonPrimitive?.contentOrNull ?: ""
        if (estadoGeneral != "OK") {
            throw ErrorMaps(mensajeEstadoGeocoding(estadoGeneral))
        }

        val punto = raiz.getArray("rows")?.firstOrNull()
            ?.getArray("elements")?.firstOrNull() as? JsonObject
            ?: throw ErrorMaps("La ruta no se pudo calcular para esa dirección.")
        val estadoPunto = punto.get("status")?.jsonPrimitive?.contentOrNull ?: ""
        if (estadoPunto != "OK") {
            throw ErrorMaps("La ruta hasta esa dirección no se pudo calcular. Prueba con otra.")
        }

        // se prefieren los valores crudos (metros/segundos) para formatear igual que el demo;
        // el texto de Google queda de respaldo por si el valor crudo no viene.
        val metros = punto.getJsonObjectOrNull("distance")
            ?.get("value")?.jsonPrimitive?.contentOrNull?.toDoubleOrNull()
        val segundos = punto.getJsonObjectOrNull("duration")
            ?.get("value")?.jsonPrimitive?.contentOrNull?.toDoubleOrNull()

        val km = (metros ?: 0.0) / 1000.0
        val kmTextoDuro = punto.getJsonObjectOrNull("distance")
            ?.get("text")?.jsonPrimitive?.contentOrNull ?: ""
        val distanciaKm = metros?.div(1000.0)
            ?: kmTextoDuro.substringBefore(' ').replace(',', '.').toDoubleOrNull()
            ?: 0.0
        val kmTexto = if (metros != null) {
            val redondeado = round(km * 10.0).toInt() / 10.0
            "$redondeado km"
        } else kmTextoDuro

        val minutos = segundos?.let { (it / 60.0).toInt().coerceAtLeast(2) }
        val minutosTexto = if (minutos != null) "~$minutos min en moto"
        else {
            val leyenda = punto.getJsonObjectOrNull("duration")
                ?.get("text")?.jsonPrimitive?.contentOrNull ?: ""
            "~$leyenda en moto"
        }

        return ResultadoDistancia(
            kmTexto = kmTexto,
            minutosTexto = minutosTexto,
            distanciaKm = distanciaKm
        )
    }

    /** Obtiene distancia y duración por calles desde OSRM como respaldo sin clave. */
    suspend fun distanciaPorCarretera(hasta: Coordenada): ResultadoDistancia {
        val respuesta = cliente.get(
            "https://router.project-osrm.org/route/v1/driving/" +
                "${sede.longitud},${sede.latitud};${hasta.longitud},${hasta.latitud}"
        ) {
            parameter("overview", "false")
        }
        val textoRespuesta = respuesta.bodyAsText()
        if (!respuesta.status.isSuccess()) {
            throw ErrorMaps("El servicio de rutas respondió con error (${respuesta.status.value}).")
        }

        val raiz = leerJson(textoRespuesta)
            ?: throw ErrorMaps("No se pudo leer la respuesta del servicio de rutas.")
        if (raiz["code"]?.jsonPrimitive?.contentOrNull != "Ok") {
            throw ErrorMaps("No se encontró una ruta por calles para esa dirección.")
        }

        val ruta = raiz.getArray("routes")?.firstOrNull()?.jsonObject
            ?: throw ErrorMaps("El servicio de rutas no devolvió una ruta.")
        val metros = ruta["distance"]?.jsonPrimitive?.contentOrNull?.toDoubleOrNull()
            ?: throw ErrorMaps("El servicio de rutas no devolvió la distancia.")
        val segundos = ruta["duration"]?.jsonPrimitive?.contentOrNull?.toDoubleOrNull()
            ?: throw ErrorMaps("El servicio de rutas no devolvió la duración.")
        val km = metros / 1000.0
        val minutos = round(segundos / 60.0).toInt().coerceAtLeast(1)
        val kmRedondeado = round(km * 10.0).toInt() / 10.0

        return ResultadoDistancia(
            kmTexto = "$kmRedondeado km",
            minutosTexto = "~$minutos min aprox. por carretera",
            distanciaKm = km
        )
    }

    // URL de la imagen del mapa estatico: un pin verde en la sede y uno rojo
    // en el cliente, unidos por una linea punteada (Maps Static API).
    // devuelve "" si no hay key: la pantalla muestra el mini-mapa dibujado a mano.
    fun urlMapaEstatico(cliente: Coordenada, zoom: Int = 13): String {
        if (modoDemo) return ""

        val local = pinDe(sede, VERDE_PIN, "S") // S de la sede SazonTotal
        val destino = pinDe(cliente, "0xFFFF453A", "C")
        val linea = "path=weight:3|color:$VERDE_PIN" +
            "|${sede.latitud},${sede.longitud}|${cliente.latitud},${cliente.longitud}"
        return "https://maps.googleapis.com/maps/api/staticmap" +
            "?size=600x300&scale=2&zoom=$zoom" +
            "&markers=$local&markers=$destino&$linea" +
            "&key=$apiKey"
    }

    private fun pinDe(coordenada: Coordenada, colorHex: String, letra: String): String =
        "color:$colorHex|label:$letra|${coordenada.latitud},${coordenada.longitud}"

    // parseo defensivo: devuelve nulo (no excepcion) si el JSON viene incompleto.
    private fun leerJson(texto: String): JsonObject? = try {
        Json.parseToJsonElement(texto).jsonObject
    } catch (e: Exception) {
        null
    }

    // convierte el JSON de error de Google ("error": {"message"}) en texto de español.
    private fun mensajeErrorDe(json: String, codigo: Int): String = try {
        val detalle = Json.parseToJsonElement(json).jsonObject
            .getJsonObject("error")?.get("message")?.jsonPrimitive?.contentOrNull
        when {
            codigo == 400 && detalle?.contains("API key not valid", ignoreCase = true) == true ->
                "La API key de Maps no es válida. Revísala en data/maps/DatosMaps.kt."
            codigo == 403 -> "La API key no tiene permiso para usar ese servicio de Maps."
            codigo == 429 -> "Se alcanzó el límite de uso de Google Maps. Espera un momento e intenta de nuevo."
            else -> "Error al llamar a Google Maps ($codigo): ${detalle ?: "sin detalle"}."
        }
    } catch (e: Exception) {
        "Error al llamar a Google Maps (código $codigo)."
    }

    // los servicios antiguos de Maps no usan HTTP 4xx: el error llega en el campo "status".
    private fun mensajeEstadoGeocoding(estado: String): String {
        return when {
            estado.isBlank() -> "Google Maps no respondió bien. Intenta de nuevo."
            estado == "REQUEST_DENIED" ->
                "La API key no tiene habilitado ese servicio de Maps (revisa la consola de Google)."
            estado == "OVER_QUERY_LIMIT" ->
                "Se alcanzó el límite de uso de Google Maps. Espera un momento e intenta de nuevo."
            else -> "Error al calcular con Google Maps ($estado). Intenta de nuevo."
        }
    }

    // utilitarios de lectura defensiva: acceso seguro por clave, sin excepciones.
    private fun JsonElement.getJsonObject(clave: String): JsonObject? =
        (this as? JsonObject)?.get(clave)?.jsonObject

    private fun JsonElement.getJsonObjectOrNull(clave: String): JsonObject? =
        (this as? JsonObject)?.get(clave) as? JsonObject

    private fun JsonElement.getArray(clave: String) =
        (this as? JsonObject)?.get(clave) as? kotlinx.serialization.json.JsonArray


}

// distancia en km entre dos coordenadas (formula de haversine).
// funciones de kotlin.math: iguales en Android, iOS y demas plataformas.
private fun haversineKm(a: Coordenada, b: Coordenada): Double {
    val tierra = 6371.0
    val dLat = (b.latitud - a.latitud) * PI / 180.0
    val dLng = (b.longitud - a.longitud) * PI / 180.0
    val h = sin(dLat / 2) * sin(dLat / 2) +
        cos(a.latitud * PI / 180.0) * cos(b.latitud * PI / 180.0) *
        sin(dLng / 2) * sin(dLng / 2)
    return 2 * tierra * asin(sqrt(h))
}
