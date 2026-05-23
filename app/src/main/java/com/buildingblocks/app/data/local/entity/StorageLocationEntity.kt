package com.buildingblocks.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.buildingblocks.app.domain.model.StorageLocation
import com.buildingblocks.app.domain.model.StorageLocationType
import java.time.Instant
import java.util.UUID

@Entity(tableName = "storage_locations")
data class StorageLocationEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val name: String,
    val type: String,
    val parentLocationId: String?,
    val notes: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val deletedAt: Long?
) {
    fun toDomain() = StorageLocation(
        id = UUID.fromString(id),
        userId = UUID.fromString(userId),
        name = name,
        type = StorageLocationType.valueOf(type),
        parentLocationId = parentLocationId?.let { UUID.fromString(it) },
        notes = notes,
        createdAt = Instant.ofEpochMilli(createdAt),
        updatedAt = Instant.ofEpochMilli(updatedAt),
        deletedAt = deletedAt?.let { Instant.ofEpochMilli(it) }
    )

    companion object {
        fun fromDomain(loc: StorageLocation) = StorageLocationEntity(
            id = loc.id.toString(),
            userId = loc.userId.toString(),
            name = loc.name,
            type = loc.type.name,
            parentLocationId = loc.parentLocationId?.toString(),
            notes = loc.notes,
            createdAt = loc.createdAt.toEpochMilli(),
            updatedAt = loc.updatedAt.toEpochMilli(),
            deletedAt = loc.deletedAt?.toEpochMilli()
        )
    }
}
