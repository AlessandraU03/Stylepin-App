package com.ale.stylepin.features.pins.data.datasources.local.dao

import androidx.room.*
import com.ale.stylepin.features.pins.data.datasources.local.entities.PinEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PinDao {
    @Query("SELECT * FROM pins ORDER BY createdAt DESC")
    fun getAllPins(): Flow<List<PinEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPins(pins: List<PinEntity>)

    @Query("DELETE FROM pins")
    suspend fun clearAll()
}
