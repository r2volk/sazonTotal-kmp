package org.sazontotal.project.data.ia

import org.sazontotal.project.data.db.PlatoEntity
import org.sazontotal.project.data.db.VentaPlato
import org.sazontotal.project.utils.moneda

// un mensaje del chat: el rol de quien lo dice y su texto. la pantalla lleva la
// lista de mensajes y la envia completa en cada llamada para dar memoria a la IA.
data class MensajeChat(
    val rol: Rol,
    val texto: String
) {
    enum class Rol { ASISTENTE, MESERO }
}

/**
 * arma el contexto del restaurante y lo envia a Gemini. la pantalla guarda el
 * historial y lo pasa completo en cada llamada para que la IA recuerde la conversacion.
 */
class AsistenteIA(private val servicio: GeminiService) {

    suspend fun responder(
        historial: List<MensajeChat>,
        platos: List<PlatoEntity>,
        topPlatos: List<VentaPlato>,
        nombreMesero: String,
        mesa: String
    ): String {
        val consultaActual = historial.lastOrNull { it.rol == MensajeChat.Rol.MESERO }?.texto.orEmpty()
        respuestaRestriccionAlergenos(consultaActual, platos)?.let { return it }

        val sistema = construirPromptSistema(platos, topPlatos, nombreMesero, mesa)
        return servicio.enviar(sistema, historial)
    }

    /**
     * las consultas para evitar alergenos se responden usando etiquetas explicitas,
     * sin delegar esa afirmacion al modelo generativo. una etiqueta ausente significa
     * no confirmado, no libre de.
     */
    private fun respuestaRestriccionAlergenos(
        consulta: String,
        platos: List<PlatoEntity>
    ): String? {
        val consultaNormalizada = normalizarTexto(consulta)
        val alergenosSolicitados = ALERGENOS_RECONOCIDOS.filter { alergeno ->
            alergeno.variantes.any { consultaNormalizada.contains(it) }
        }
        val consultaSobreAlergias = alergenosSolicitados.isNotEmpty() || listOf(
            "alerg", "intoler", "celiac", "alergeno", "alergenos"
        ).any { consultaNormalizada.contains(it) }
        val pideEvitar = listOf(
            "sin ", "alerg", "intoler", "celiac", "apto", "libre de", "evita", "evitar",
            "no puede consumir", "no puede comer", "no consumir", "no contiene", "no tiene", "no lleva"
        ).any { consultaNormalizada.contains(it) }
        if (!consultaSobreAlergias || !pideEvitar) return null

        if (alergenosSolicitados.isEmpty()) {
            return "El menú no permite confirmar una opción libre de todos los alérgenos. " +
                "Confirma los ingredientes y posibles trazas con cocina antes de recomendar."
        }

        val menuActivo = platos.filter { it.activo }
        val opcionesConfirmadas = menuActivo.filter { plato ->
            val etiquetas = plato.etiquetas
                .split(',', ';', '|')
                .map(::normalizarTexto)
            alergenosSolicitados.all { alergeno ->
                etiquetas.any { etiqueta -> alergeno.variantes.any { variante ->
                    etiqueta == "sin $variante" ||
                        etiqueta == "libre de $variante" ||
                        etiqueta == "no contiene $variante"
                } }
            }
        }

        if (opcionesConfirmadas.isEmpty()) {
            val nombresAlergenos = alergenosSolicitados.joinToString(" y ") { it.nombre }
            return "No hay platos con etiquetas que confirmen que estén libres de $nombresAlergenos. " +
                "La falta de una etiqueta no confirma que un plato sea apto. Verifica ingredientes " +
                "y posibles trazas con cocina antes de recomendar."
        }

        val opciones = opcionesConfirmadas.joinToString(", ") { "${it.nombre} (${moneda(it.precio)})" }
        val nombresAlergenos = alergenosSolicitados.joinToString(" y ") { it.nombre }
        return "Según las etiquetas explícitas del menú, estas opciones figuran libres de " +
            "$nombresAlergenos: $opciones. Confirma los ingredientes y posibles trazas con cocina."
    }

    // todo lo que la IA sabe del restaurante vive en este texto. sin internet
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
            - Para alergias o restricciones, afirma que un plato está libre de un ingrediente SOLO si sus etiquetas contienen explícitamente "Sin <ingrediente>", "Libre de <ingrediente>" o "No contiene <ingrediente>" para cada restricción solicitada.
            - Una etiqueta ausente significa que no hay información confirmada; nunca infieras que un plato está libre de un alérgeno por su nombre, categoría, descripción o conocimiento general.
            - Si las etiquetas no confirman una opción para todas las restricciones, dilo claramente y recomienda verificar ingredientes y trazas con cocina; no inventes una alternativa apta.
            - Para maridajes, combina platos y bebidas del menú.
            - Puedes mencionar precios con "S/ " y calcular totales rápidos de varios platos.
            - No inventes datos que no estén aquí (horarios, promociones, disponibilidad, mesas).
            - No digas que eres un modelo de lenguaje de Google; te presentas solo como el Asistente IA de SazónTotal.
            - Eres asistente del PERSONAL (meseros), no chateas con clientes: habla de forma profesional y útil para atender una mesa.
        """.trimIndent()
    }
}

private data class AlergenoReconocido(
    val nombre: String,
    val variantes: List<String>
)

private val ALERGENOS_RECONOCIDOS = listOf(
    AlergenoReconocido("gluten", listOf("gluten", "celiaco", "celiaca", "celiaquia")),
    AlergenoReconocido("mariscos", listOf("marisco", "mariscos", "crustaceo", "crustaceos", "camaron", "langostino")),
    AlergenoReconocido("lácteos", listOf("lacteo", "lacteos", "leche")),
    AlergenoReconocido("lactosa", listOf("lactosa")),
    AlergenoReconocido("huevo", listOf("huevo", "huevos")),
    AlergenoReconocido("pescado", listOf("pescado", "pescados")),
    AlergenoReconocido("frutos secos", listOf("frutos secos", "nuez", "nueces", "almendra", "almendras", "avellana", "avellanas")),
    AlergenoReconocido("maní", listOf("mani", "cacahuate", "cacahuetes")),
    AlergenoReconocido("soja", listOf("soja", "soya"))
)

private fun normalizarTexto(texto: String): String = texto
    .lowercase()
    .replace('á', 'a')
    .replace('é', 'e')
    .replace('í', 'i')
    .replace('ó', 'o')
    .replace('ú', 'u')
    .replace('ü', 'u')
    .replace('ñ', 'n')
    .trim()
