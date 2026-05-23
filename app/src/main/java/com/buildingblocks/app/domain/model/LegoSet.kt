package com.buildingblocks.app.domain.model

import java.time.Instant
import java.time.LocalDate
import java.util.UUID

data class LegoSet(
    val id: UUID = UUID.randomUUID(),
    val userId: UUID,
    val legoSetNumber: String? = null,
    val name: String,
    val theme: String? = null,
    val year: Int? = null,
    val pieceCount: Int? = null,
    val imageUrl: String? = null,
    val status: SetStatus = SetStatus.SEALED,
    val addedAt: Instant = Instant.now(),
    val purchasedAt: LocalDate? = null,
    val difficulty: Difficulty? = null,
    val priority: Priority = Priority.MEDIUM,
    val storageLocationId: UUID? = null,
    val notes: String? = null,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    val deletedAt: Instant? = null
) {
    fun daysWaiting(): Long {
        val now = Instant.now()
        val seconds = now.epochSecond - addedAt.epochSecond
        return seconds / 86400
    }
}
