package org.sazontotal.project.data.maps

// pega aqui tu API key de Google Maps Platform. la consigues en console.cloud.google.com.
// debe tener habilitadas: Geocoding API, Places API (New),
// Distance Matrix API y Maps Static API.
const val KEY_MAPS = "AIzaSyBMEsg4VvuPQXbkAN8RQlpiDhnH5cvl_eQ"

// campus verificado: direccion oficial UPN y punto del campus en OpenStreetMap.
// https://www.upn.edu.pe/universidad-en-los-olivos
// https://www.openstreetmap.org/way/405584319
const val DIRECCION_SEDE = "Av. Alfredo Mendiola 6062, Los Olivos, Lima, Perú"
const val NOMBRE_SEDE = "Sazón Total · UPN Los Olivos"
val SEDE_COORDENADA = Coordenada(latitud = -11.9595307, longitud = -77.0677791)
