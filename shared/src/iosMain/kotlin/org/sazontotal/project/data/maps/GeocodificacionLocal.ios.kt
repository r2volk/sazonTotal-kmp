package org.sazontotal.project.data.maps

actual suspend fun geocodificarDireccionLocal(direccion: String): Coordenada? = null

actual suspend fun sugerirDireccionesLocales(textoParcial: String): List<SugerenciaDireccion> = emptyList()
