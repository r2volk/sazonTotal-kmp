package org.sazontotal.project.data.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

private var appContext: Context? = null

fun initAndroidContext(context: Context) {
    appContext = context.applicationContext
}

// contexto de aplicacion para utilidades del proyecto (ej. abrir rutas de
// Google Maps con un Intent). nulo solo en vista previa.
fun contextoAndroid(): Context? = appContext

actual fun createDatabaseBuilder(): RoomDatabase.Builder<SazonDatabase> {
    // en la app real el contexto lo pone MainActivity al arrancar.
    // en la vista previa no hay app corriendo, asi que se avisa claro.
    val context = appContext ?: error("Sin contexto: en vista previa la base no existe")
    // android guarda la base en el directorio privado de la aplicacion.
    val dbFile = context.getDatabasePath("sazontotal.db")
    return Room.databaseBuilder<SazonDatabase>(
        context = context,
        name = dbFile.absolutePath
    )
}
