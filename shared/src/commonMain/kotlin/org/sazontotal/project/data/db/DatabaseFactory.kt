package org.sazontotal.project.data.db

import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

// cada plataforma define donde se crea el archivo sqlite de la aplicacion.
expect fun createDatabaseBuilder(): RoomDatabase.Builder<SazonDatabase>
expect fun migration8To9(): Migration

fun migrarEntrega8A9(connection: SQLiteConnection) {
    connection.execSQL("ALTER TABLE entregas ADD COLUMN pedidoId INTEGER")
    connection.execSQL("CREATE UNIQUE INDEX index_entregas_pedidoId ON entregas (pedidoId)")
    connection.execSQL("ALTER TABLE pedidos ADD COLUMN solicitudId TEXT")
    connection.execSQL("CREATE UNIQUE INDEX index_pedidos_solicitudId ON pedidos (solicitudId)")
}

fun buildDatabase(): SazonDatabase {
    return createDatabaseBuilder()
        .addMigrations(migration8To9())
        // si se cambia el esquema, borra la base en vez de crashear
        .fallbackToDestructiveMigration(true)
        .build()
}
