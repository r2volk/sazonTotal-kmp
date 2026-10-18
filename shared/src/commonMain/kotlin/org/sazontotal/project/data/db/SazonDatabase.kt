package org.sazontotal.project.data.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor

@Database(
    entities = [EmpleadoEntity::class, PlatoEntity::class, PedidoEntity::class, PedidoItemEntity::class, PromoEntity::class],
    version = 6,
    exportSchema = false)
@ConstructedBy(SazonDatabaseConstructor::class)
abstract class SazonDatabase : RoomDatabase() {
    abstract fun empleadoDao(): EmpleadoDao
    abstract fun platoDao(): PlatoDao
    abstract fun pedidoDao(): PedidoDao
    abstract fun pedidoItemDao(): PedidoItemDao
    abstract fun promoDao(): PromoDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object SazonDatabaseConstructor : RoomDatabaseConstructor<SazonDatabase> {
    override fun initialize(): SazonDatabase
}