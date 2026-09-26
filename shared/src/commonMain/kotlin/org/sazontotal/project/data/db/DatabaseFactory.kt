package org.sazontotal.project.data.db

import androidx.room.RoomDatabase

expect fun createDatabaseBuilder(): RoomDatabase.Builder<SazonDatabase>

fun buildDatabase(): SazonDatabase {
    return createDatabaseBuilder()
        // Mientras programas: si cambias las tablas, borra y recrea
        // la base en vez de crashear la app. Los datos de prueba se pierden,
        // pero en desarrollo no importa.
        .fallbackToDestructiveMigration(true)
        .build()
}