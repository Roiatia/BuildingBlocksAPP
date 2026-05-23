package com.buildingblocks.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.buildingblocks.app.data.local.dao.*
import com.buildingblocks.app.data.local.entity.*

@Database(
    entities = [
        LegoSetEntity::class,
        StorageLocationEntity::class,
        MissingPartEntity::class,
        BuildLogEntity::class,
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun legoSetDao(): LegoSetDao
    abstract fun storageLocationDao(): StorageLocationDao
    abstract fun missingPartDao(): MissingPartDao
    abstract fun buildLogDao(): BuildLogDao

    companion object {
        const val DATABASE_NAME = "building_blocks_db"
    }
}
