package org.sazontotal.project.data.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor

@Database(entities = [EmpleadoEntity::class, PlatoEntity::class], version = 3, exportSchema = false)
@ConstructedBy(SazonDatabaseConstructor::class)
abstract class SazonDatabase : RoomDatabase() {
    abstract fun empleadoDao(): EmpleadoDao
    abstract fun platoDao(): PlatoDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object SazonDatabaseConstructor : RoomDatabaseConstructor<SazonDatabase> {
    override fun initialize(): SazonDatabase
}