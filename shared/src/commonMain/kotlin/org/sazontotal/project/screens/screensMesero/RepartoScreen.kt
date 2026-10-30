package org.sazontotal.project.screens.screensMesero

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Navigation
import androidx.compose.material.icons.outlined.Route
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.sazontotal.project.data.maps.Coordenada
import org.sazontotal.project.data.maps.DIRECCION_SEDE
import org.sazontotal.project.data.maps.EntregaReparto
import org.sazontotal.project.data.maps.KEY_MAPS
import org.sazontotal.project.data.maps.GoogleMapsService
import org.sazontotal.project.data.maps.ResultadoDistancia
import org.sazontotal.project.data.maps.SEDE_COORDENADA
import org.sazontotal.project.utils.abrirEnExterno
import org.sazontotal.project.utils.ahoraMillis

// paleta propia del modulo de reparto (verde lima sobre fondo oscuro).
private val VerdeModulo = Color(0xFF7BE382)
private val VerdeModuloOscuro = Color(0xFF143D2B)
private val Ambar = Color(0xFFFFB340)
private val RojoSuave = Color(0xFFFF6B6B)

/**
 * pedidos a domicilio: el mesero escribe la direccion del cliente, elige una
 * sugerencia y la app muestra el mapa, la distancia y el tiempo del reparto,
 * con botones para abrir la ruta y registrar la entrega.
 *
 * todavia no usa la base de datos: las suggerencias/distancias salen del
 * servicio de maps (en modo demo devuelve datos de ejemplo si no hay key) y
 * las entregas registradas van a una lista en memoria en App.kt.
 */
@Composable
fun RepartoScreen(
    backReparto: () -> Unit,
    onVerEntregas: () -> Unit,
    onRegistrarEntrega: (EntregaReparto) -> Unit,
    nombreMesero: String = "Mesero",
    entregasPendientes: Int = 0
) {
    // una sola imagen loader con descarga por red para AsyncImage (coil).
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .components { add(KtorNetworkFetcherFactory()) }
            .build()
    }

    val maps = remember { GoogleMapsService(apiKey = KEY_MAPS) }
    val modoDemo = KEY_MAPS.isBlank() || KEY_MAPS.startsWith("PEGA_")
    val scope = rememberCoroutineScope()

    // texto que el mesero esta escribiendo.
    var direccion by remember { mutableStateOf("") }
    // direccion fijada (null = aun elige); cuando se fija, deja de sugerir.
    var direccionElegida by remember { mutableStateOf<String?>(null) }
    var sugerencias by remember { mutableStateOf<List<String>>(emptyList()) }
    var cargandoRuta by remember { mutableStateOf(false) }
    var coordenada by remember { mutableStateOf<Coordenada?>(null) }
    var resultado by remember { mutableStateOf<ResultadoDistancia?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    // aviso breve de entrega registrada.
    var entregaToast by remember { mutableStateOf(false) }

    // sugerencias con pausa de 600ms para no pedir en cada tecla.
    LaunchedEffect(direccion, direccionElegida) {
        if (direccionElegida != null) {
            sugerencias = emptyList()
        } else {
            val texto = direccion.trim()
            if (texto.length >= 3) {
                delay(600)
                sugerencias = maps.autocompletar(texto)
            } else {
                sugerencias = emptyList()
            }
        }
    }

    LaunchedEffect(entregaToast) {
        if (entregaToast) {
            delay(2500)
            entregaToast = false
        }
    }

    // limpia el buscador, listo para el siguiente cliente.
    fun reiniciarBusqueda() {
        direccion = ""
        direccionElegida = null
        sugerencias = emptyList()
        coordenada = null
        resultado = null
    }

    // fija una direccion y calcula su ruta (coordenada + distancia/tiempo).
    fun elegir(texto: String) {
        val limpia = texto.trim()
        if (limpia.isEmpty() || cargandoRuta) return
        direccion = limpia
        direccionElegida = limpia
        sugerencias = emptyList()
        coordenada = null
        resultado = null
        error = null
        cargandoRuta = true
        scope.launch {
            try {
                val punto = maps.geocodificar(limpia)
                val dista = maps.distancia(punto)
                coordenada = punto
                resultado = dista
            } catch (e: Exception) {
                error = "No se pudo encontrar esa dirección. Prueba con otra."
                direccionElegida = null
            } finally {
                cargandoRuta = false
            }
        }
    }

    // boton de la lupa: con sugerencias toma la primera; si ya hay elegida, vuelve a editar.
    fun buscar() {
        if (direccionElegida != null) {
            // la lupa pasa a ser "editar": libera la direccion elegida.
            direccionElegida = null
            coordenada = null
            resultado = null
            error = null
            return
        }
        val candidata = sugerencias.firstOrNull() ?: direccion.takeIf { it.trim().length >= 3 }
        if (candidata == null) {
            error = "Escribe al menos 3 letras de la dirección."
            return
        }
        elegir(candidata)
    }

    // registrar la entrega, avisa y deja la pantalla lista para el siguiente cliente.
    fun registrarEntrega() {
        val dista = resultado ?: return
        val punto = coordenada ?: return
        val fecha = ahoraMillis()
        onRegistrarEntrega(
            EntregaReparto(
                id = fecha,
                direccion = direccionElegida ?: direccion,
                kmTexto = dista.kmTexto,
                minutosTexto = dista.minutosTexto,
                fecha = fecha,
                entregada = false,
                meseroNombre = nombreMesero
            )
        )
        entregaToast = true
        reiniciarBusqueda()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(start = 20.dp, end = 20.dp, top = 60.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        RepartoHeader(
            onBack = backReparto,
            onVerEntregas = onVerEntregas,
            entregasPendientes = entregasPendientes
        )
        if (modoDemo) BannerDemo()

        // Buscador: editable hasta fijar la direccion; al tocarlo de nuevo vuelve a editar.
        BuscadorReparto(
            texto = direccion,
            editable = direccionElegida == null,
            conBordeVerde = direccionElegida == null,
            onTextoChange = { nueva ->
                error = null
                if (direccionElegida != null) {
                    direccionElegida = null
                    coordenada = null
                    resultado = null
                }
                direccion = nueva
            },
            onSearch = { buscar() }
        )

        // Sugerencias mientras se escribe (max 5).
        if (direccionElegida == null && sugerencias.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1C1C1E))
            ) {
                sugerencias.take(5).forEachIndexed { index, sugerido ->
                    SugerenciaRow(texto = sugerido, onClick = { elegir(sugerido) })
                    if (index < sugerencias.lastIndex) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 68.dp)
                                .height(1.dp)
                                .background(Color(0xFF2C2C2E))
                        )
                    }
                }
            }
        }

        // aviso de error (geocoding fallo, texto corto, etc.).
        if (error != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF3A1414))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = null,
                    tint = RojoSuave,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = error ?: "",
                    color = RojoSuave,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Mapa: imagen real (url del servicio), mini-mapa dibujado en demo, o placeholder.
        MapaReparto(
            modifier = Modifier.weight(1f),
            maps = maps,
            modoDemo = modoDemo,
            coordenada = coordenada,
            cargandoRuta = cargandoRuta,
            direccionElegida = direccionElegida
        )

        // Distancia y tiempo (solo cuando ya se calculo la ruta).
        if (resultado != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatReparto(
                    icono = Icons.Outlined.Navigation,
                    valor = resultado!!.kmTexto,
                    etiqueta = "distancia",
                    iconoBg = VerdeModuloOscuro,
                    iconoTint = VerdeModulo,
                    modifier = Modifier.weight(1f)
                )
                StatReparto(
                    icono = Icons.Filled.Timer,
                    valor = resultado!!.minutosTexto,
                    etiqueta = "en moto",
                    iconoBg = Color(0xFF3D2905),
                    iconoTint = Ambar,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // aviso corto de entrega registrada.
        if (entregaToast) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF12331B))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = VerdeModulo,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Entrega registrada · En camino",
                    color = VerdeModulo,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Abrir ruta (desabilitado sin coordenada).
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (coordenada != null) VerdeModulo else Color(0xFF3A3A3C))
                .clickable(enabled = coordenada != null) {
                    abrirEnExterno(maps.urlRutaGoogleMaps(coordenada!!))
                },
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Navigation,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Abrir ruta en Google Maps",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Waze + Registrar entrega.
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF1C1C1E))
                    .clickable(enabled = coordenada != null) {
                        abrirEnExterno(maps.urlRutaWaze(coordenada!!))
                    },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Route,
                        contentDescription = null,
                        tint = Color(0xFFB0B0B0),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Waze",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Box(
                modifier = Modifier
                    .weight(1.2f)
                    .height(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (resultado != null) Color(0xFF0E2B18) else Color(0xFF1C1C1E))
                    .clickable(enabled = resultado != null) { registrarEntrega() },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = if (resultado != null) VerdeModulo else Color(0xFF5A5A5E),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Registrar entrega",
                        color = if (resultado != null) VerdeModulo else Color(0xFF5A5A5E),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun RepartoScreenPreview() {
    RepartoScreen(
        backReparto = {},
        onVerEntregas = {},
        onRegistrarEntrega = {}
    )
}

@Composable
private fun RepartoHeader(
    onBack: () -> Unit,
    onVerEntregas: () -> Unit,
    entregasPendientes: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        IconButton(
            onClick = { onBack() },
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
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(VerdeModuloOscuro),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.TwoWheeler,
                contentDescription = null,
                tint = VerdeModulo,
                modifier = Modifier.size(24.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Pedidos a domicilio",
                color = Color.White,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(VerdeModulo)
                )
                Text(
                    text = "Sede: $DIRECCION_SEDE",
                    color = Color(0xFF8E8E93),
                    fontSize = 13.sp
                )
            }
        }
        // acceso al historial de entregas del dia, con badge de pendientes.
        Box(contentAlignment = Alignment.TopEnd) {
            IconButton(
                onClick = { onVerEntregas() },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.History,
                    contentDescription = "Entregas de hoy",
                    tint = VerdeModulo,
                    modifier = Modifier.size(22.dp)
                )
            }
            if (entregasPendientes > 0) {
                Box(
                    modifier = Modifier
                        .offset(x = (-2).dp, y = (-4).dp)
                        .clip(CircleShape)
                        .background(Ambar)
                        .padding(horizontal = 5.dp, vertical = 1.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = entregasPendientes.toString(),
                        color = Color.Black,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun BannerDemo() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF3A2B08))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Info,
            contentDescription = null,
            tint = Ambar,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = "Módulo en modo demostración",
            color = Ambar,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * el cuadro grande del centro: con API key muestra el mapa estatico de Google
 * (imagen descargada); en demo dibuja un mini-mapa ilustrativo con la ruta
 * punteada de la sede al cliente (sin red, puro Canvas).
 */
@Composable
private fun MapaReparto(
    maps: GoogleMapsService,
    modoDemo: Boolean,
    coordenada: Coordenada?,
    cargandoRuta: Boolean,
    direccionElegida: String?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                drawRoundRect(
                    color = Color(0xFF3A3A3C),
                    style = Stroke(
                        width = 2f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
                    ),
                    cornerRadius = CornerRadius(48f, 48f)
                )
            }
            .clip(RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        val url = coordenada?.let { maps.urlMapaEstatico(it) } ?: ""
        when {
            url.isNotEmpty() -> {
                AsyncImage(
                    model = url,
                    contentDescription = "Mapa de la ruta al cliente",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            coordenada != null && modoDemo -> {
                MapaDemoRuta(sede = SEDE_COORDENADA, cliente = coordenada)
            }

            else -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (cargandoRuta) {
                        CircularProgressIndicator(color = VerdeModulo, strokeWidth = 3.dp, modifier = Modifier.size(28.dp))
                        Text(
                            text = if (direccionElegida != null) "Calculando ruta…" else "",
                            color = Color(0xFFB0B0B0),
                            fontSize = 15.sp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.LocationOn,
                            contentDescription = null,
                            tint = Color(0xFF3A3A3C),
                            modifier = Modifier.size(34.dp)
                        )
                        Text(
                            text = "Escribe la dirección del cliente\npara dibujar la ruta",
                            color = Color(0xFF5A5A5E),
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

/**
 * mini-mapa 100% dibujado (sin red ni API): calles de fondo, ruta punteada de
 * la sede al cliente entre dos pines y etiquetas flotantes.
 */
@Composable
private fun MapaDemoRuta(sede: Coordenada, cliente: Coordenada) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF131719)) // tono mapa oscuro
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val ancho = size.width
            val alto = size.height

            // calles de fondo (3 horizontales, 3 verticales).
            listOf(0.18f, 0.52f, 0.84f).forEach { fraccion ->
                drawLine(
                    color = Color(0xFF20262A),
                    start = Offset(0f, alto * fraccion),
                    end = Offset(ancho, alto * fraccion),
                    strokeWidth = alto * 0.055f
                )
            }
            listOf(0.24f, 0.56f, 0.86f).forEach { fraccion ->
                drawLine(
                    color = Color(0xFF20262A),
                    start = Offset(ancho * fraccion, 0f),
                    end = Offset(ancho * fraccion, alto),
                    strokeWidth = ancho * 0.035f
                )
            }

            // puntos de la ruta (norte arriba: y contrario a la latitud).
            val inicio = puntoEnMapa(sede, sede, ancho, alto)
            val fin = puntoEnMapa(cliente, sede, ancho, alto)

            // ruta punteada con una curva suave.
            val control = Offset(
                x = (inicio.x + fin.x) / 2f + (fin.y - inicio.y) * 0.18f,
                y = (inicio.y + fin.y) / 2f - (fin.x - inicio.x) * 0.18f
            )
            val ruta = Path().apply {
                moveTo(inicio.x, inicio.y)
                quadraticBezierTo(control.x, control.y, fin.x, fin.y)
            }
            drawPath(
                path = ruta,
                color = VerdeModulo,
                style = Stroke(
                    width = 5f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(22f, 14f))
                )
            )

            // pin de la sede (verde) con halo.
            drawCircle(
                color = Color(0xFF30D158).copy(alpha = 0.25f),
                radius = 30f,
                center = inicio
            )
            drawCircle(color = Color(0xFF30D158), radius = 14f, center = inicio)

            // pin del cliente (rojo) con halo.
            drawCircle(
                color = Color(0xFFFF453A).copy(alpha = 0.25f),
                radius = 30f,
                center = fin
            )
            drawCircle(color = Color(0xFFFF453A), radius = 14f, center = fin)
        }

        // etiquetas flotantes de los pines.
        EtiquetaMapa(
            texto = "Sede",
            colorPunto = Color(0xFF30D158),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
        )
        EtiquetaMapa(
            texto = "Cliente",
            colorPunto = Color(0xFFFF453A),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
        )
    }
}

@Composable
private fun EtiquetaMapa(
    texto: String,
    colorPunto: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xCC101214))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(colorPunto)
        )
        Text(
            text = texto,
            color = Color(0xFFB0B0B0),
            fontSize = 11.sp
        )
    }
}

// convierte una coordenada en un punto de pantalla relativo a la sede
// (escala: 0.02 grados = media pantalla aprox).
private fun puntoEnMapa(coordenada: Coordenada, sede: Coordenada, ancho: Float, alto: Float): Offset {
    val dLat = coordenada.latitud - sede.latitud
    val dLng = coordenada.longitud - sede.longitud
    val x = ancho * (0.5f + (dLng / 0.02).toFloat().coerceIn(-0.40f, 0.40f))
    val y = alto * (0.5f - (dLat / 0.02).toFloat().coerceIn(-0.38f, 0.38f))
    return Offset(x, y)
}

@Composable
private fun BuscadorReparto(
    texto: String,
    editable: Boolean,
    conBordeVerde: Boolean,
    onTextoChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                if (conBordeVerde) {
                    drawRoundRect(
                        color = VerdeModulo,
                        style = Stroke(
                            width = 6f,
                            pathEffect = PathEffect.cornerPathEffect(48f)
                        ),
                        cornerRadius = CornerRadius(size.height / 2f, size.height / 2f)
                    )
                }
            }
            .clip(RoundedCornerShape(30.dp))
            .background(Color(0xFF1C1C1E))
            .padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.LocationOn,
            contentDescription = null,
            tint = Color(0xFF8E8E93),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.size(12.dp))
        BasicTextField(
            value = texto,
            onValueChange = { onTextoChange(it) },
            modifier = Modifier.weight(1f),
            enabled = editable,
            singleLine = true,
            textStyle = TextStyle(
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            ),
            cursorBrush = SolidColor(VerdeModulo),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearch() }),
            decorationBox = { campoInterno ->
                Box {
                    if (texto.isEmpty()) {
                        Text(
                            text = "Dirección del cliente…",
                            color = Color(0xFF5A5A5E),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
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
                .background(if (conBordeVerde) VerdeModulo else Color(0xFF235B31))
                .clickable { onSearch() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = if (conBordeVerde) "Buscar" else "Editar dirección",
                tint = if (conBordeVerde) Color(0xFF0D2818) else VerdeModulo,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun SugerenciaRow(
    texto: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF2A2A2C)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.LocationOn,
                contentDescription = null,
                tint = Color(0xFF8E8E93),
                modifier = Modifier.size(20.dp)
            )
        }
        Text(
            text = texto,
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun StatReparto(
    icono: ImageVector,
    valor: String,
    etiqueta: String,
    iconoBg: Color,
    iconoTint: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(72.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1C1C1E))
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(iconoBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = iconoTint,
                modifier = Modifier.size(20.dp)
            )
        }
        Column {
            Text(
                text = valor,
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = etiqueta,
                color = Color(0xFF8E8E93),
                fontSize = 12.sp
            )
        }
    }
}
