package com.buildingblocks.app.domain.model

import java.time.Instant
import java.util.UUID

data class BuildLog(
    val id: UUID = UUID.randomUUID(),
    val userId: UUID,
    val setId: UUID,
    val startedAt: Instant? = null,
    val completedAt: Instant? = null,
    val buildTimeMinutes: Int? = null,
    val notes: String? = null,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)
