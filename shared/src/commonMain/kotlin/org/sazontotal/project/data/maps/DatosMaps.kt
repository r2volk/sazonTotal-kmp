package org.sazontotal.project.data.maps

// pega aqui tu API key de Google Maps Platform (console.cloud.google.com).
// debe tener habilitadas: Geocoding API, Places API (New),
// Distance Matrix API y Maps Static API.
const val KEY_MAPS = "PEGA_AQUI_TU_API_KEY_DE_MAPS"

// sede ficticia de Sazontotal: direccion real de una avenida de Lima para
// que Google la geocodifique de verdad, aunque el restaurante sea de demostracion.
const val DIRECCION_SEDE = "Av. Larco 512, Miraflores 15074, Perú"
const val NOMBRE_SEDE = "SazónTotal · Sede Miraflores"

// coordenadas reales de esa direccion (aproximadas del geocoder de Google).
val SEDE_COORDENADA = Coordenada(latitud = -12.1241, longitud = -77.0295)
