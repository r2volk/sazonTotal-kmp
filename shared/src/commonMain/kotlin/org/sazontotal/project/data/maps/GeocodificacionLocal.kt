package org.sazontotal.project.data.maps

import kotlin.math.*

/** resuelve una direccion con el geocodificador disponible en la plataforma. */
expect suspend fun geocodificarDireccionLocal(direccion: String): Coordenada?

/** busca direcciones parecidas para usarlas como sugerencias cuando Places no responde. */
expect suspend fun sugerirDireccionesLocales(textoParcial: String): List<SugerenciaDireccion>

fun distanciaEnKilometros(a: Coordenada, b: Coordenada): Double {
    val radio = 6371.0
    val lat = (b.latitud - a.latitud) * PI / 180.0
    val lon = (b.longitud - a.longitud) * PI / 180.0
    val formula = sin(lat / 2).pow(2) +
        cos(a.latitud * PI / 180.0) * cos(b.latitud * PI / 180.0) * sin(lon / 2).pow(2)
    return 2 * radio * asin(sqrt(formula.coerceIn(0.0, 1.0)))
}
