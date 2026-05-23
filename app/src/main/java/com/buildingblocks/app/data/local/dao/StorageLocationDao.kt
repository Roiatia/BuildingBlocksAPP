package com.buildingblocks.app.data.local.dao

import androidx.room.*
import com.buildingblocks.app.data.local.entity.StorageLocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StorageLocationDao {

    @Query("SELECT * FROM storage_locations WHERE userId = :userId AND deletedAt IS NULL ORDER BY name ASC")
    fun getAllLocations(userId: String): Flow<List<StorageLocationEntity>>

    @Query("SELECT * FROM storage_locations WHERE id = :id AND deletedAt IS NULL")
    fun getLocationById(id: String): Flow<StorageLocationEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: StorageLocationEntity)

    @Update
    suspend fun updateLocation(location: StorageLocationEntity)

    @Query("UPDATE storage_locations SET deletedAt = :deletedAt, updatedAt = :updatedAt WHERE id = :id")
    suspend fun softDeleteLocation(id: String, deletedAt: Long, updatedAt: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocations(locations: List<StorageLocationEntity>)

    @Query("SELECT * FROM storage_locations WHERE userId = :userId AND updatedAt >= :sinceEpochMilli")
    suspend fun getLocationsSinceIncludeDeleted(userId: String, sinceEpochMilli: Long): List<StorageLocationEntity>

    @Query("SELECT * FROM storage_locations WHERE id = :id LIMIT 1")
    suspend fun getLocationByIdOnce(id: String): StorageLocationEntity?
}
