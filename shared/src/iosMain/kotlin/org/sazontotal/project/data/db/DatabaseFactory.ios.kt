package org.sazontotal.project.data.db

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

actual fun migration8To9(): Migration = object : Migration(8, 9) {
    override fun migrate(connection: SQLiteConnection) = migrarEntrega8A9(connection)
}

@OptIn(ExperimentalForeignApi::class)
actual fun createDatabaseBuilder(): RoomDatabase.Builder<SazonDatabase> {
    // ios guarda el archivo sqlite dentro del directorio Documents de la app.
    val docDir = NSFileManager.defaultManager.URLForDirectory(
        NSDocumentDirectory, NSUserDomainMask, null, true, null
    )!!.path!!
    val dbPath = "$docDir/sazontotal.db"
    return Room.databaseBuilder<SazonDatabase>(name = dbPath)
}
