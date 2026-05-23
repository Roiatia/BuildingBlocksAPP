package com.buildingblocks.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.buildingblocks.app.domain.model.*
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

@Entity(tableName = "lego_sets")
data class LegoSetEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val legoSetNumber: String?,
    val name: String,
    val theme: String?,
    val year: Int?,
    val pieceCount: Int?,
    val imageUrl: String?,
    val status: String,
    val addedAt: Long,
    val purchasedAt: String?,
    val difficulty: String?,
    val priority: String,
    val storageLocationId: String?,
    val notes: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val deletedAt: Long?
) {
    fun toDomain() = LegoSet(
        id = UUID.fromString(id),
        userId = UUID.fromString(userId),
        legoSetNumber = legoSetNumber,
        name = name,
        theme = theme,
        year = year,
        pieceCount = pieceCount,
        imageUrl = imageUrl,
        status = SetStatus.valueOf(status),
        addedAt = Instant.ofEpochMilli(addedAt),
        purchasedAt = purchasedAt?.let { LocalDate.parse(it) },
        difficulty = difficulty?.let { Difficulty.valueOf(it) },
        priority = Priority.valueOf(priority),
        storageLocationId = storageLocationId?.let { UUID.fromString(it) },
        notes = notes,
        createdAt = Instant.ofEpochMilli(createdAt),
        updatedAt = Instant.ofEpochMilli(updatedAt),
        deletedAt = deletedAt?.let { Instant.ofEpochMilli(it) }
    )

    companion object {
        fun fromDomain(set: LegoSet) = LegoSetEntity(
            id = set.id.toString(),
            userId = set.userId.toString(),
            legoSetNumber = set.legoSetNumber,
            name = set.name,
            theme = set.theme,
            year = set.year,
            pieceCount = set.pieceCount,
            imageUrl = set.imageUrl,
            status = set.status.name,
            addedAt = set.addedAt.toEpochMilli(),
            purchasedAt = set.purchasedAt?.toString(),
            difficulty = set.difficulty?.name,
            priority = set.priority.name,
            storageLocationId = set.storageLocationId?.toString(),
            notes = set.notes,
            createdAt = set.createdAt.toEpochMilli(),
            updatedAt = set.updatedAt.toEpochMilli(),
            deletedAt = set.deletedAt?.toEpochMilli()
        )
    }
}
