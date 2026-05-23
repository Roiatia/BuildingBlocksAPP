package com.buildingblocks.app.domain.model

import java.time.Instant
import java.util.UUID

data class User(
    val id: UUID = UUID.randomUUID(),
    val email: String,
    val displayName: String? = null,
    val authProvider: AuthProvider,
    val premiumStatus: PremiumStatus = PremiumStatus.FREE,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)
