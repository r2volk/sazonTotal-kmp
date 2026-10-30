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
import kotlinx.serialization.json.Json
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
    val minutosTexto: String
)

/**
 * cliente de los servicios de Google Maps Platform, mismo patron del
 * GeminiService: HTTP puro con Ktor, sin SDKs nativos.
 *
 * servicios usados:
 *  - Geocoding API: direccion escrita -> coordenadas
 *  - Places API (New) Text Search: sugerencias mientras se escribe
 *  - Distance Matrix API: km y minutos entre la sede y el cliente
 *  - Maps Static API: URL de la imagen del mapa con los dos pines
 *
 * MODO DEMO: si no hay API key (constante vacia o placeholder "PEGA_...")
 * no se llama a la red: se devuelven datos de ejemplo y el mapa se dibuja
 * a mano en la pantalla, para que la app siga 100% jugable sin key.
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

    // sin key (ceficiente vacia o placeholder) todo se resuelve en modo demo.
    private val modoDemo: Boolean
        get() = apiKey.isBlank() || apiKey.startsWith("PEGA_")

    // sugerencias de direcciones mientras es el mesero (Places API (New) Text Search).
    suspend fun autocompletar(textoParcial: String): List<String> {
        if (modoDemo) return listaDemo.filter { contieneAlgunaPalabra(it, textoParcial) }

        val cuerpo = buildJsonObject {
            put("text", textoParcial.trim())
            put("pageSize", 5)  // el buscador muestra maximo 5 sugerencias
            put("languageCode", "es")
        }

        val respuesta = cliente.post("https://places.googleapis.com/v1/places:searchText") {
            header("X-Goog-Api-Key", apiKey)
            // la mascara define que campos baja la respuesta (menos datos = menos costo).
            header("X-Goog-FieldMask", "places.displayName,places.formattedAddress")
            contentType(ContentType.Application.Json)
            setBody(cuerpo.toString())
        }
        val textoRespuesta = respuesta.bodyAsText()

        if (!respuesta.status.isSuccess()) {
            throw ErrorMaps(mensajeErrorDe(textoRespuesta, respuesta.status.value))
        }

        // la respuesta trae {"places":[{"displayName":{"text":...},"formattedAddress":...}]}.
        return try {
            val raiz = Json.parseToJsonElement(textoRespuesta).jsonObject
            raiz.getArray("places").orEmpty().mapNotNull { lugar ->
                val objeto = lugar as? JsonObject ?: return@mapNotNull null
                // la direccion formal es la que se guardara; el displayName es el plano.
                objeto.get("formattedAddress")?.jsonPrimitive?.contentOrNull
                    ?: objeto.getJsonObject("displayName")?.get("text")?.jsonPrimitive?.contentOrNull
            }
        } catch (e: Exception) {
            throw ErrorMaps("No se pudieron leer las sugerencias de Google Maps. Intenta de nuevo.")
        }
    }

    // convierte la direccion elegida en coordenadas (Geocoding API).
    suspend fun geocodificar(direccion: String): Coordenada {
        if (modoDemo) {
            // en demo se reconocen las direcciones demo exactas; el resto cae
            // un poco lejos de la sede para que se vea la ruta dibujada.
            return mapaDemo.entries
                .firstOrNull { it.key.lowercase() == direccion.trim().lowercase() }
                ?.value
                ?: Coordenada(latitud = SEDE_COORDENADA.latitud + 0.005, longitud = SEDE_COORDENADA.longitud + 0.004)
        }

        val respuesta = cliente.get("https://maps.googleapis.com/maps/api/geocode/json") {
            parameter("address", direccion.trim())
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
                minutosTexto = "~$minutos min en moto"
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

        return ResultadoDistancia(kmTexto = kmTexto, minutosTexto = minutosTexto)
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

    // abre la navegacion guiada en Google Maps o Waze (deep links universales).
    fun urlRutaGoogleMaps(hasta: Coordenada): String =
        "https://www.google.com/maps/dir/?api=1&origin=${sede.latitud},${sede.longitud}" +
            "&destination=${hasta.latitud},${hasta.longitud}&travelmode=driving"

    fun urlRutaWaze(hasta: Coordenada): String =
        "https://waze.com/ul?ll=${hasta.latitud},${hasta.longitud}&navigate=yes"

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

    // demo: direcciones de Lima tipicas de una zona de entrega.
    private val listaDemo = listOf(
        "Parque Kennedy, Av. José Larco 900, Miraflores",
        "Calle Berlín 500, Miraflores",
        "Av. Arequipa 2500, Lince",
        "Jr. de la Unión 1000, Lima Centro",
        "Av. Oscar R. Benavides 2000, Cercado de Lima"
    )

    private val mapaDemo = mapOf(
        "Parque Kennedy, Av. José Larco 900, Miraflores" to Coordenada(latitud = -12.1207, longitud = -77.0297),
        "Calle Berlín 500, Miraflores" to Coordenada(latitud = -12.1199, longitud = -77.0359),
        "Av. Arequipa 2500, Lince" to Coordenada(latitud = -12.0900, longitud = -77.0321),
        "Jr. de la Unión 1000, Lima Centro" to Coordenada(latitud = -12.0463, longitud = -77.0297),
        "Av. Oscar R. Benavides 2000, Cercado de Lima" to Coordenada(latitud = -12.0523, longitud = -77.0846)
    )
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

private fun contieneAlgunaPalabra(texto: String, parcial: String): Boolean {
    val abajo = parcial.trim().lowercase()
    if (abajo.isEmpty()) return false
    return texto.lowercase().split(" ", ",").any { palabra ->
        palabra.startsWith(abajo.take(4))
    } || texto.lowercase().contains(abajo)
}
