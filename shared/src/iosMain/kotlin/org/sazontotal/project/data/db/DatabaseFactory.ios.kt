package org.sazontotal.project.data.db

import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
actual fun createDatabaseBuilder(): RoomDatabase.Builder<SazonDatabase> {
    val docDir = NSFileManager.defaultManager.URLForDirectory(
        NSDocumentDirectory, NSUserDomainMask, null, true, null
    )!!.path!!
    val dbPath = "$docDir/sazontotal.db"
    return Room.databaseBuilder<SazonDatabase>(name = dbPath)
}
