package com.buildingblocks.app.data.repository

import com.buildingblocks.app.domain.model.StorageLocation
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface StorageLocationRepository {
    fun getAllLocations(userId: UUID): Flow<List<StorageLocation>>
    fun getLocationById(id: UUID): Flow<StorageLocation?>
    suspend fun saveLocation(location: StorageLocation)
    suspend fun deleteLocation(id: UUID)
}
