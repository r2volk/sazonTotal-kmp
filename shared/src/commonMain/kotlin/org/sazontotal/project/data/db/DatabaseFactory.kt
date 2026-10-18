package org.sazontotal.project.data.db

import androidx.room.RoomDatabase

expect fun createDatabaseBuilder(): RoomDatabase.Builder<SazonDatabase>

fun buildDatabase(): SazonDatabase {
    return createDatabaseBuilder()
        // si cambias el esquema, borra la base en vez de crashear
        .fallbackToDestructiveMigration(true)
        .build()
}