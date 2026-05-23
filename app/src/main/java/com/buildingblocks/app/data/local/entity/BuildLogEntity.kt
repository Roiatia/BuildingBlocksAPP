package com.buildingblocks.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.buildingblocks.app.domain.model.BuildLog
import java.time.Instant
import java.util.UUID

@Entity(tableName = "build_logs")
data class BuildLogEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val setId: String,
    val startedAt: Long?,
    val completedAt: Long?,
    val buildTimeMinutes: Int?,
    val notes: String?,
    val createdAt: Long,
    val updatedAt: Long
) {
    fun toDomain() = BuildLog(
        id = UUID.fromString(id),
        userId = UUID.fromString(userId),
        setId = UUID.fromString(setId),
        startedAt = startedAt?.let { Instant.ofEpochMilli(it) },
        completedAt = completedAt?.let { Instant.ofEpochMilli(it) },
        buildTimeMinutes = buildTimeMinutes,
        notes = notes,
        createdAt = Instant.ofEpochMilli(createdAt),
        updatedAt = Instant.ofEpochMilli(updatedAt)
    )

    companion object {
        fun fromDomain(log: BuildLog) = BuildLogEntity(
            id = log.id.toString(),
            userId = log.userId.toString(),
            setId = log.setId.toString(),
            startedAt = log.startedAt?.toEpochMilli(),
            completedAt = log.completedAt?.toEpochMilli(),
            buildTimeMinutes = log.buildTimeMinutes,
            notes = log.notes,
            createdAt = log.createdAt.toEpochMilli(),
            updatedAt = log.updatedAt.toEpochMilli()
        )
    }
}
