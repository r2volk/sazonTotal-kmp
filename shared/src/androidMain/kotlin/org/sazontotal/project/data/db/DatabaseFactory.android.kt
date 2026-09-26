package org.sazontotal.project.data.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

private var appContext: Context? = null

fun initAndroidContext(context: Context) {
    appContext = context.applicationContext
}

actual fun createDatabaseBuilder(): RoomDatabase.Builder<SazonDatabase> {
    // En la app real el contexto lo pone MainActivity al arrancar.
    // En la vista previa no hay app corriendo, así que se avisa claro.
    val context = appContext ?: error("Sin contexto: en vista previa la base no existe")
    val dbFile = context.getDatabasePath("sazontotal.db")
    return Room.databaseBuilder<SazonDatabase>(
        context = context,
        name = dbFile.absolutePath
    )
}
