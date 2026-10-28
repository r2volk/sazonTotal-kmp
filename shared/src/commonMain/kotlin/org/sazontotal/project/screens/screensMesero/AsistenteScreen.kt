package org.sazontotal.project.screens.screensMesero

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.sazontotal.project.data.db.EmpleadoEntity
import org.sazontotal.project.data.ia.AsistenteIA
import org.sazontotal.project.data.ia.KEY_GEMINI
import org.sazontotal.project.data.ia.GeminiService
import org.sazontotal.project.data.ia.MensajeChat
import org.sazontotal.project.data.repository.SazonRepository
import org.sazontotal.project.theme.Colores
import org.sazontotal.project.utils.ahoraMillis
import kotlinx.coroutines.launch

/**
 * chat con la IA de Gemini. pregunta al asistente por recomendaciones,
 * ingredientes, precios o maridajes; el contexto (menu, alergenos, top
 * ventas) sale de la base de datos del restaurante en cada respuesta.
 *
 * el historial se conserva solo mientras la pantalla vive: el boton de
 * editar (nueva conversacion) empieza desde cero.
 */
@Composable
fun AsistenteScreen(
    backAsistente: () -> Unit,
    // mismo patron del proyecto: los parametros con default nulo permiten previews sin base de datos.
    repositorio: SazonRepository? = null,
    empleado: EmpleadoEntity? = null,
    mesa: String = "4",
) {
    var consulta by remember { mutableStateOf("") }
    var enviando by remember { mutableStateOf(false) }

    // historial de la conversacion; arranca con el saludo personalizado del asistente.
    val mensajes = remember { mutableStateListOf<MensajeChat>() }
    val saludoInicial = "Hola ${primerNombre(empleado?.nombre ?: "mesero")}, soy tu asistente. " +
        "Pregúntame por recomendaciones, ingredientes o maridajes."
    LaunchedEffect(Unit) {
        if (mensajes.isEmpty()) mensajes.add(MensajeChat(MensajeChat.Rol.ASISTENTE, saludoInicial))
    }

    // datos del restaurante que fluyen hacia el prompt de la IA (igual que las demas pantallas).
    val platos = repositorio?.observarPlatos()?.collectAsState(emptyList())?.value ?: emptyList()
    val topPlatos = repositorio
        ?.observarTopPlatosEnRango(desde = 0L, hasta = ahoraMillis())
        ?.collectAsState(emptyList())?.value ?: emptyList()

    val asistente = remember { AsistenteIA(GeminiService(apiKey = KEY_GEMINI)) }
    val scope = rememberCoroutineScope()

    // envia la consulta del mesero y muestra la respuesta de la IA como nueva burbuja.
    fun enviar(texto: String) {
        val textoLimpio = texto.trim()
        if (textoLimpio.isEmpty() || enviando) return
        if (repositorio == null) return // preview sin datos: no hay con quien conversar

        consulta = ""
        mensajes.add(MensajeChat(MensajeChat.Rol.MESERO, textoLimpio))

        if (KEY_GEMINI.isBlank() || KEY_GEMINI.startsWith("PEGA_")) {
            mensajes.add(
                MensajeChat(
                    MensajeChat.Rol.ASISTENTE,
                    "Aún falta configurar la API key de Gemini. Pégala en " +
                        "shared/.../data/ia/ApiKey.kt y vuelve a compilar."
                )
            )
            return
        }

        enviando = true
        scope.launch {
            try {
                val respuesta = asistente.responder(
                    historial = mensajes.toList(),
                    platos = platos,
                    topPlatos = topPlatos,
                    nombreMesero = empleado?.nombre ?: "Mesero",
                    mesa = mesa
                )
                mensajes.add(MensajeChat(MensajeChat.Rol.ASISTENTE, respuesta))
            } catch (e: Exception) {
                mensajes.add(
                    MensajeChat(
                        MensajeChat.Rol.ASISTENTE,
                        "No pude responder: ${e.message ?: "sin conexión con la IA"}. " +
                            "Revisa tu internet e intenta de nuevo."
                    )
                )
            } finally {
                enviando = false
            }
        }
    }

    // auto scroll del chat al llegar mensajes nuevos (o mientras escribe la IA).
    val scrollChat = rememberScrollState()
    LaunchedEffect(mensajes.size, enviando) {
        scrollChat.animateScrollTo(scrollChat.maxValue)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(start = 20.dp, end = 20.dp, top = 60.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(
                onClick = { backAsistente() },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Atrás",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF291B3D)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.AutoAwesome,
                    contentDescription = null,
                    tint = Color(0xFFB388FF),
                    modifier = Modifier.size(26.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Asistente IA",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (enviando) Colores.Naranja else Colores.VerdePrimario)
                    )
                    Text(
                        text = if (enviando) "Escribiendo · Mesa $mesa" else "En línea · Mesa $mesa",
                        color = Colores.TextoTenue,
                        fontSize = 13.sp
                    )
                }
            }
            // nueva conversacion: borra el historial y repone el saludo.
            IconButton(
                onClick = {
                    mensajes.clear()
                    mensajes.add(MensajeChat(MensajeChat.Rol.ASISTENTE, saludoInicial))
                },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Nueva conversación",
                    tint = Colores.TextoTenue,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // historial de burbujas de la conversacion real.
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollChat),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            mensajes.forEach { mensaje ->
                if (mensaje.rol == MensajeChat.Rol.ASISTENTE) {
                    BurbujaAsistente(texto = mensaje.texto)
                } else {
                    BurbujaMesero(texto = mensaje.texto)
                }
            }
            // indicador "escribiendo..." solo mientras espera la respuesta de la API.
            if (enviando) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    BurbujaAsistente(
                        texto = "",
                        contenidoPersonalizado = {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                repeat(3) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFB388FF))
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AtajoChip(
                texto = "Sugerir maridaje",
                onClick = { enviar("¿Qué maridaje me sugieres para el plato más vendido?") }
            )
            AtajoChip(
                texto = "Sin alérgenos",
                onClick = { enviar("Mi cliente busca algo sin mariscos ni gluten, ¿qué le recomiendas?") }
            )
            AtajoChip(
                texto = "Más vendidos",
                onClick = { enviar("¿Cuáles son los platos más vendidos y qué precio tienen?") }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(Colores.Superficie)
                .padding(start = 20.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = consulta,
                onValueChange = { consulta = it },
                modifier = Modifier.weight(1f),
                textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
                cursorBrush = SolidColor(Colores.VerdeAcento),
                enabled = !enviando,
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { enviar(consulta) }),
                decorationBox = { campoInterno ->
                    Box {
                        if (consulta.isEmpty() && !enviando) {
                            Text(
                                text = "Escribe tu consulta",
                                color = Color(0xFF5A5A5E),
                                fontSize = 16.sp
                            )
                        }
                        campoInterno()
                    }
                }
            )
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (enviando) Colores.GrisDeshabilitado else Colores.VerdeBoton)
                    .clickable { enviar(consulta) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowUpward,
                    contentDescription = "Enviar",
                    tint = Color.Black,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Text(
            text = "La IA puede equivocarse. Confirma alérgenos con cocina.",
            color = Color(0xFF5A5A5E),
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// primer nombre del empleado para el saludo ("Ricardo Sanchez" -> "Ricardo").
private fun primerNombre(nombreCompleto: String): String =
    nombreCompleto.trim().split(" ").firstOrNull().orEmpty().ifBlank { "mesero" }

// burbuja del asistente: izquierda. contenidoPersonalizado permite poner los tres
// puntitos del "escribiendo..." en el mismo estilo visual de la burbuja.
@Composable
private fun BurbujaAsistente(
    texto: String,
    modifier: Modifier = Modifier,
    contenidoPersonalizado: (@Composable () -> Unit)? = null
) {
    Row(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .widthIn(max = 340.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 4.dp,
                        topEnd = 20.dp,
                        bottomStart = 20.dp,
                        bottomEnd = 20.dp
                    )
                )
                .background(Color(0xFF232040))
                .padding(16.dp)
        ) {
            if (contenidoPersonalizado != null) contenidoPersonalizado() else {
                Text(
                    text = texto,
                    color = Color(0xFFE8E4F5),
                    fontSize = 16.sp
                )
            }
        }
    }
}

// burbuja del mesero: derecha
@Composable
private fun BurbujaMesero(
    texto: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 4.dp,
                        bottomStart = 20.dp,
                        bottomEnd = 20.dp
                    )
                )
                .background(Colores.Superficie)
                .padding(16.dp)
        ) {
            Text(
                text = texto,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun AtajoChip(
    texto: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Colores.Superficie)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            color = Color(0xFFB0B0B0),
            fontSize = 14.sp
        )
    }
}

@Composable
@Preview
fun AsistenteScreenPreview() {
    AsistenteScreen(
        backAsistente = {}
        // repositorio nulo: la preview muestra solo el saludo sin llamar a la API.
    )
}
