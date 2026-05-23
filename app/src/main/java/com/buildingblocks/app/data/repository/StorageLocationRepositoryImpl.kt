package com.buildingblocks.app.data.repository

import com.buildingblocks.app.data.local.dao.StorageLocationDao
import com.buildingblocks.app.data.local.entity.StorageLocationEntity
import com.buildingblocks.app.domain.model.StorageLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

class StorageLocationRepositoryImpl @Inject constructor(
    private val dao: StorageLocationDao
) : StorageLocationRepository {

    override fun getAllLocations(userId: UUID): Flow<List<StorageLocation>> =
        dao.getAllLocations(userId.toString()).map { it.map(StorageLocationEntity::toDomain) }

    override fun getLocationById(id: UUID): Flow<StorageLocation?> =
        dao.getLocationById(id.toString()).map { it?.toDomain() }

    override suspend fun saveLocation(location: StorageLocation) {
        dao.insertLocation(StorageLocationEntity.fromDomain(location))
    }

    override suspend fun deleteLocation(id: UUID) {
        val now = Instant.now().toEpochMilli()
        dao.softDeleteLocation(id.toString(), now, now)
    }
}
