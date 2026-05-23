package com.buildingblocks.app.domain.model

import java.time.Instant
import java.util.UUID

data class StorageLocation(
    val id: UUID = UUID.randomUUID(),
    val userId: UUID,
    val name: String,
    val type: StorageLocationType,
    val parentLocationId: UUID? = null,
    val notes: String? = null,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    val deletedAt: Instant? = null
)
