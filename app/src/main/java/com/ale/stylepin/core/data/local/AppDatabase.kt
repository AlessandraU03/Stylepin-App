package com.ale.stylepin.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ale.stylepin.features.pins.data.datasources.local.dao.PinDao
import com.ale.stylepin.features.pins.data.datasources.local.entities.PinEntity

@Database(
    entities = [PinEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pinDao(): PinDao
}
