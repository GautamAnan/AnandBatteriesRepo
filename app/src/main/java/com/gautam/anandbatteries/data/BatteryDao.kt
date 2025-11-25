package com.gautam.anandbatteries.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BatteryDao {
    @Query("SELECT * FROM batteries ORDER BY name ASC")
    fun getAllBatteries(): Flow<List<Battery>>

    @Query("SELECT * FROM batteries WHERE id = :batteryId")
    suspend fun getBatteryById(batteryId: String): Battery?

    @Query("SELECT * FROM batteries WHERE category = :category ORDER BY name ASC")
    fun getBatteriesByCategory(category: String): Flow<List<Battery>>

    @Query("SELECT * FROM batteries WHERE name LIKE '%' || :query || '%' OR model LIKE '%' || :query || '%'")
    fun searchBatteries(query: String): Flow<List<Battery>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBatteries(batteries: List<Battery>)

    @Query("DELETE FROM batteries")
    suspend fun deleteAllBatteries()
}

