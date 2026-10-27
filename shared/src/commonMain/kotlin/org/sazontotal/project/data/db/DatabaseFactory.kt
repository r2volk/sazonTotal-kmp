package org.sazontotal.project.data.db

import androidx.room.RoomDatabase

// cada plataforma define donde se crea el archivo sqlite de la aplicacion.
expect fun createDatabaseBuilder(): RoomDatabase.Builder<SazonDatabase>

fun buildDatabase(): SazonDatabase {
    return createDatabaseBuilder()
        // si se cambia el esquema, borra la base en vez de crashear
        .fallbackToDestructiveMigration(true)
        .build()
}
