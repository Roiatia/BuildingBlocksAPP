package com.buildingblocks.app.sync

import com.buildingblocks.app.data.local.dao.LegoSetDao
import com.buildingblocks.app.data.local.dao.StorageLocationDao
import com.buildingblocks.app.data.local.entity.LegoSetEntity
import com.buildingblocks.app.data.local.entity.StorageLocationEntity
import com.buildingblocks.app.data.remote.LegoSetRemoteDataSource
import com.buildingblocks.app.data.remote.StorageLocationRemoteDataSource
import com.buildingblocks.app.data.remote.dto.LegoSetDto
import com.buildingblocks.app.data.remote.dto.StorageLocationDto
import com.buildingblocks.app.session.SessionManager
import java.time.Instant
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

// MVP sync scope: lego_sets and storage_locations only.
// missing_parts and build_logs are local-only for now and not synced.
@Singleton
class SyncManager @Inject constructor(
    private val sessionManager: SessionManager,
    private val legoSetDao: LegoSetDao,
    private val storageLocationDao: StorageLocationDao,
    private val legoSetRemote: LegoSetRemoteDataSource,
    private val storageLocationRemote: StorageLocationRemoteDataSource,
    private val syncPrefs: SyncPreferences
) {
    private val iso = DateTimeFormatter.ISO_INSTANT

    suspend fun sync(): SyncResult {
        val userId = sessionManager.currentUserId.toString()
        if (!sessionManager.isSignedIn) return SyncResult.Skipped

        return try {
            val lastSync = syncPrefs.getLastSyncTime(userId)
            val syncStart = Instant.now()

            syncSets(userId, lastSync)
            syncLocations(userId, lastSync)

            syncPrefs.setLastSyncTime(userId, iso.format(syncStart))
            SyncResult.Success(iso.format(syncStart))
        } catch (e: Exception) {
            SyncResult.Error(e.message ?: "Unknown sync error")
        }
    }

    private suspend fun syncSets(userId: String, lastSync: String) {
        val sinceEpoch = Instant.parse(lastSync).toEpochMilli()

        // Upload: local rows updated since last sync
        val localChanges = legoSetDao.getSetsSinceIncludeDeleted(userId, sinceEpoch)
        if (localChanges.isNotEmpty()) {
            legoSetRemote.upsertSets(localChanges.map { it.toDto() })
        }

        // Download: remote rows updated since last sync, apply newer-updatedAt-wins
        val remoteChanges = legoSetRemote.getSetsSince(userId, lastSync)
        if (remoteChanges.isNotEmpty()) {
            val toInsert = remoteChanges.mapNotNull { dto ->
                val local = legoSetDao.getSetByIdOnce(dto.id)
                val remoteUpdatedMs = Instant.parse(dto.updatedAt).toEpochMilli()
                if (local == null || remoteUpdatedMs > local.updatedAt) dto.toEntity() else null
            }
            if (toInsert.isNotEmpty()) {
                legoSetDao.insertSets(toInsert)
            }
        }
    }

    private suspend fun syncLocations(userId: String, lastSync: String) {
        val sinceEpoch = Instant.parse(lastSync).toEpochMilli()

        // Upload: local rows updated since last sync
        val localChanges = storageLocationDao.getLocationsSinceIncludeDeleted(userId, sinceEpoch)
        if (localChanges.isNotEmpty()) {
            storageLocationRemote.upsertLocations(localChanges.map { it.toDto() })
        }

        // Download: remote rows updated since last sync, apply newer-updatedAt-wins (local only — no re-upload)
        val remoteChanges = storageLocationRemote.getLocationsSince(userId, lastSync)
        if (remoteChanges.isNotEmpty()) {
            val toInsert = remoteChanges.mapNotNull { dto ->
                val local = storageLocationDao.getLocationByIdOnce(dto.id)
                val remoteUpdatedMs = Instant.parse(dto.updatedAt).toEpochMilli()
                if (local == null || remoteUpdatedMs > local.updatedAt) dto.toEntity() else null
            }
            if (toInsert.isNotEmpty()) {
                storageLocationDao.insertLocations(toInsert)
            }
        }
    }

    // ── Entity <-> DTO mappers ─────────────────────────────────────────────────

    private fun LegoSetEntity.toDto() = LegoSetDto(
        id = id,
        userId = userId,
        legoSetNumber = legoSetNumber,
        name = name,
        theme = theme,
        year = year,
        pieceCount = pieceCount,
        imageUrl = imageUrl,
        status = status,
        notes = notes,
        priority = priority,
        difficulty = difficulty,
        storageLocationId = storageLocationId,
        purchasedAt = purchasedAt,
        addedAt = iso.format(Instant.ofEpochMilli(addedAt)),
        updatedAt = iso.format(Instant.ofEpochMilli(updatedAt)),
        createdAt = iso.format(Instant.ofEpochMilli(createdAt)),
        deletedAt = deletedAt?.let { iso.format(Instant.ofEpochMilli(it)) }
    )

    private fun LegoSetDto.toEntity() = LegoSetEntity(
        id = id,
        userId = userId,
        legoSetNumber = legoSetNumber,
        name = name,
        theme = theme,
        year = year,
        pieceCount = pieceCount,
        imageUrl = imageUrl,
        status = status,
        notes = notes,
        priority = priority,
        difficulty = difficulty,
        storageLocationId = storageLocationId,
        purchasedAt = purchasedAt,
        addedAt = Instant.parse(addedAt).toEpochMilli(),
        updatedAt = Instant.parse(updatedAt).toEpochMilli(),
        createdAt = Instant.parse(createdAt).toEpochMilli(),
        deletedAt = deletedAt?.let { Instant.parse(it).toEpochMilli() }
    )

    private fun StorageLocationEntity.toDto() = StorageLocationDto(
        id = id,
        userId = userId,
        name = name,
        type = type,
        parentLocationId = parentLocationId,
        notes = notes,
        updatedAt = iso.format(Instant.ofEpochMilli(updatedAt)),
        createdAt = iso.format(Instant.ofEpochMilli(createdAt)),
        deletedAt = deletedAt?.let { iso.format(Instant.ofEpochMilli(it)) }
    )

    private fun StorageLocationDto.toEntity() = StorageLocationEntity(
        id = id,
        userId = userId,
        name = name,
        type = type,
        parentLocationId = parentLocationId,
        notes = notes,
        updatedAt = Instant.parse(updatedAt).toEpochMilli(),
        createdAt = Instant.parse(createdAt).toEpochMilli(),
        deletedAt = deletedAt?.let { Instant.parse(it).toEpochMilli() }
    )
}

sealed class SyncResult {
    data class Success(val syncedAt: String) : SyncResult()
    data class Error(val message: String) : SyncResult()
    object Skipped : SyncResult()
}
