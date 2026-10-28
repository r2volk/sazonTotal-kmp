package org.sazontotal.project.data.ia

import org.sazontotal.project.data.db.PlatoEntity
import org.sazontotal.project.data.db.VentaPlato
import org.sazontotal.project.utils.moneda

// un mensaje del chat: quien lo dice (rol) y su texto. la pantalla lleva la
// lista de mensajes y la envia completa en cada llamada para dar memoria a la IA.
data class MensajeChat(
    val rol: Rol,
    val texto: String
) {
    enum class Rol { ASISTENTE, MESERO }
}

/**
 * capa de logica del chatbot: convierte los datos del restaurante (menu,
 * etiquetas de alergenos, platos mas vendidos) en el prompt de sistema y
 * delega la llamada HTTP al GeminiService.
 *
 * la clase no guarda estado del chat: la pantalla es quien lleva el historial
 * de mensajes y lo pasa completo en cada llamada (asi Gemini recuerda la
 * conversacion dentro de la pantalla).
 */
class AsistenteIA(private val servicio: GeminiService) {

    suspend fun responder(
        historial: List<MensajeChat>,
        platos: List<PlatoEntity>,
        topPlatos: List<VentaPlato>,
        nombreMesero: String,
        mesa: String
    ): String {
        val sistema = construirPromptSistema(platos, topPlatos, nombreMesero, mesa)
        return servicio.enviar(sistema, historial)
    }

    // todo lo que la IA sabe del restaurante vive en este texto; sin internet
    // ni datos la IA no puede inventar precios de verdad.
    private fun construirPromptSistema(
        platos: List<PlatoEntity>,
        topPlatos: List<VentaPlato>,
        nombreMesero: String,
        mesa: String
    ): String {
        val listaPlatos = platos
            .filter { it.activo }
            .joinToString("\n") { plato ->
                val etiquetas = if (plato.etiquetas.isBlank()) "ninguna" else plato.etiquetas
                val notas = if (plato.descripcion.isBlank()) "" else " | notas: ${plato.descripcion}"
                "- ${plato.nombre} | ${plato.categoria} | ${moneda(plato.precio)}$notas | etiquetas: $etiquetas"
            }
            .ifBlank { "(el listado aún no está cargado)" }

        val rankingTop = topPlatos
            .take(5)
            .joinToString(", ") { "${it.nombrePlato} (${it.total} vendidos)" }
            .ifBlank { "(sin datos de ventas todavía)" }

        return """
            Eres el "Asistente IA" del restaurante SazónTotal, un restaurante peruano.
            Estás ayudando al mesero $nombreMesero en su diálogo con la MESA $mesa.
            Responde SIEMPRE en español, breve y práctico: máximo un párrafo corto o una lista.
            Usa la estructura y formato del saludo como referencia, no lo repitas.

            Menú disponible (precios en soles, IGV incluido):
            $listaPlatos

            Los platos más vendidos son: $rankingTop

            Reglas:
            - Recomienda SOLO platos del menú de arriba; si lo que piden no existe, dilo y sugiere alternativas del menú.
            - Para alergias, guíate por las etiquetas de cada plato y avisa siempre: "confirma los detalles con cocina".
            - Para maridajes, combina platos y bebidas del menú.
            - Puedes mencionar precios con "S/ " y calcular totales rápidos de varios platos.
            - No inventes datos que no estén aquí (horarios, promociones, disponibilidad, mesas).
            - No digas que eres un modelo de lenguaje de Google; te presentas solo como el Asistente IA de SazónTotal.
            - Eres asistente del PERSONAL (meseros), no chateas con clientes: habla de forma profesional y útil para atender una mesa.
        """.trimIndent()
    }
}
