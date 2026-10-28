package org.sazontotal.project.data.ia

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.put

// error con mensaje amable para mostrar al mesero cuando la llamada a la IA falla.
class ErrorIA(mensaje: String) : Exception(mensaje)

/**
 * cliente que habla con la API REST de Gemini (generativelanguage.googleapis.com).
 *
 * no declara clases @Serializable para no agregar el plugin del compilador de
 * serializacion al proyecto: el JSON se arma y se lee a mano usando
 * kotlinx-serialization-json (parseToJsonElement y buildJsonObject funcionan
 * sin ese plugin).
 */
class GeminiService(
    private val apiKey: String,
    private val modelo: String = MODELO_GEMINI
) {
    private val cliente = HttpClient {
        expectSuccess = false // el estado HTTP se revisa a mano tras cada respuesta
        // los modelos que razonan tardan en responder: se dan hasta 90 segundos
        // (por omision ktor corta mucho antes y la app creeria que ya fallo).
        install(HttpTimeout) {
            connectTimeoutMillis = 20_000
            socketTimeoutMillis = 90_000
            requestTimeoutMillis = 90_000
        }
    }

    suspend fun enviar(sistema: String, historial: List<MensajeChat>): String {
        // reintentos solo para fallos temporales del servidor (503 "high demand"):
        // los modelos recien estrenados se saturan y a veces responden a la segunda.
        var ultimoError: Exception? = null
        repeat(3) { intento ->
            if (intento > 0) delay(2000L)
            try {
                return llamadaUnica(sistema, historial)
            } catch (e: ErrorIA) {
                if (!e.message.toString().startsWith("Saturado")) throw e
                ultimoError = e
            } catch (e: SocketTimeoutException) {
                throw ErrorIA("La IA tardó demasiado en responder y se cortó la espera. Intenta de nuevo.")
            }
        }
        throw ErrorIA("Saturado: el modelo está con alta demanda ahora mismo. Espera unos segundos e intenta de nuevo.")
    }

    private suspend fun llamadaUnica(sistema: String, historial: List<MensajeChat>): String {
        val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/$modelo:generateContent"

        val cuerpo = buildJsonObject {
            // instrucciones fijas del rol del asistente.
            put("systemInstruction", encabezadoDe(sistema))
            // todo el diálogo visto hasta ahora; el ultimo mensaje debe ser del mesero.
            put("contents", buildJsonArray {
                historial.forEach { mensaje ->
                    add(mensajeDe(
                        rol = if (mensaje.rol == MensajeChat.Rol.ASISTENTE) "model" else "user",
                        texto = mensaje.texto
                    ))
                }
            })
            put("generationConfig", buildJsonObject {
                put("temperature", 0.4)          // sobrio: casi siempre la misma respuesta
                put("maxOutputTokens", 1024)     // respuestas cortas pero con margen para el "pensamiento"
                // los modelos Gemini 3 series razonan antes de responder (thinking);
                // en LOW gasta menos tokens en pensar y responde mas rapido.
                // MINIMAL no existe en 3.8 Flash.
                if (modelo.startsWith("gemini-3")) {
                    put("thinkingConfig", buildJsonObject {
                        put("thinkingLevel", "LOW")
                    })
                }
            })
        }

        val respuesta = cliente.post(endpoint) {
            header("x-goog-api-key", apiKey) // la key va en el encabezado, no en la url
            contentType(ContentType.Application.Json)
            setBody(cuerpo.toString())
        }
        val textoRespuesta = respuesta.bodyAsText()

        if (!respuesta.status.isSuccess()) {
            val mensaje = leerMensajeError(textoRespuesta, respuesta.status.value)
            // los 503 (saturacion) se marcan para que enviar() reintente; el resto avisa al instante.
            if (respuesta.status.value >= 500) throw ErrorIA("Saturado: $mensaje")
            throw ErrorIA(mensaje)
        }

        val textoIA = leerTextoRespuesta(textoRespuesta)
        if (textoIA.isBlank()) {
            throw ErrorIA("La IA devolvió una respuesta vacía. Intenta de nuevo.")
        }
        return textoIA.trim()
    }

    // envoltorios del formato {"role": ..., "parts":[{"text": ...}]} que usa la API.
    private fun mensajeDe(rol: String, texto: String): JsonObject = buildJsonObject {
        put("role", rol)
        put("parts", buildJsonArray {
            add(buildJsonObject { put("text", texto) })
        })
    }

    private fun encabezadoDe(texto: String): JsonObject = buildJsonObject {
        put("parts", buildJsonArray {
            add(buildJsonObject { put("text", texto) })
        })
    }

    // saca candidates[0].content.parts[*].text y une los fragmentos.
    private fun leerTextoRespuesta(json: String): String = try {
        val raiz = Json.parseToJsonElement(json).jsonObject
        val primerCandidato = raiz["candidates"]?.jsonArray?.firstOrNull() ?: return ""
        val partes = primerCandidato.jsonObject
            .getJsonObject("content")?.get("parts")?.jsonArray ?: return ""
        partes.mapNotNull { parte ->
            parte.jsonObject["text"]?.jsonPrimitive?.contentOrNull
        }.joinToString("")
    } catch (e: Exception) {
        ""
    }

    // convierte el JSON de error de Google en un texto entendible en español.
    private fun leerMensajeError(json: String, codigo: Int): String = try {
        val raiz = Json.parseToJsonElement(json).jsonObject
        val detalle = raiz["error"]?.jsonObject?.get("message")?.jsonPrimitive?.contentOrNull
        when (codigo) {
            400 -> if (detalle?.contains("API key not valid", ignoreCase = true) == true)
                "La API key no es válida. Revísala en data/ia/ApiKey.kt."
            else "Error de la API de Gemini: ${detalle ?: "solicitud rechazada"}."
            403 -> "La API key no tiene permiso para usar Gemini."
            429 -> "Se alcanzó el límite de uso gratuito de Gemini. Espera un momento e intenta de nuevo."
            else -> "Error al llamar a Gemini ($codigo): ${detalle ?: "sin detalle"}."
        }
    } catch (e: Exception) {
        "Error al llamar a Gemini (código $codigo)."
    }

    // utilitario de lectura defensiva: jsonElement seguro por clave.
    private fun JsonElement.getJsonObject(clave: String): JsonObject? =
        (this as? JsonObject)?.get(clave)?.jsonObject
}
