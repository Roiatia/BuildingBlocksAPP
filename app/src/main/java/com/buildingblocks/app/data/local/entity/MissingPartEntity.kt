package com.buildingblocks.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.buildingblocks.app.domain.model.MissingPart
import com.buildingblocks.app.domain.model.MissingPartStatus
import java.time.Instant
import java.util.UUID

@Entity(tableName = "missing_parts")
data class MissingPartEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val setId: String,
    val partNumber: String?,
    val color: String?,
    val quantity: Int,
    val status: String,
    val notes: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val deletedAt: Long?
) {
    fun toDomain() = MissingPart(
        id = UUID.fromString(id),
        userId = UUID.fromString(userId),
        setId = UUID.fromString(setId),
        partNumber = partNumber,
        color = color,
        quantity = quantity,
        status = MissingPartStatus.valueOf(status),
        notes = notes,
        createdAt = Instant.ofEpochMilli(createdAt),
        updatedAt = Instant.ofEpochMilli(updatedAt),
        deletedAt = deletedAt?.let { Instant.ofEpochMilli(it) }
    )

    companion object {
        fun fromDomain(part: MissingPart) = MissingPartEntity(
            id = part.id.toString(),
            userId = part.userId.toString(),
            setId = part.setId.toString(),
            partNumber = part.partNumber,
            color = part.color,
            quantity = part.quantity,
            status = part.status.name,
            notes = part.notes,
            createdAt = part.createdAt.toEpochMilli(),
            updatedAt = part.updatedAt.toEpochMilli(),
            deletedAt = part.deletedAt?.toEpochMilli()
        )
    }
}
