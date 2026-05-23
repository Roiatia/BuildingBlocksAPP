package com.buildingblocks.app.domain.model

import java.time.Instant
import java.util.UUID

data class MissingPart(
    val id: UUID = UUID.randomUUID(),
    val userId: UUID,
    val setId: UUID,
    val partNumber: String? = null,
    val color: String? = null,
    val quantity: Int = 1,
    val status: MissingPartStatus = MissingPartStatus.NEEDED,
    val notes: String? = null,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    val deletedAt: Instant? = null
)
