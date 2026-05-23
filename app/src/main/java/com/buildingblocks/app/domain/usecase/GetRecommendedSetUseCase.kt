package com.buildingblocks.app.domain.usecase

import com.buildingblocks.app.domain.model.LegoSet

// Recommends the highest-priority backlog set; among equals, the longest-waiting (oldest addedAt).
object GetRecommendedSetUseCase {
    operator fun invoke(sets: List<LegoSet>): LegoSet? =
        sets
            .filter { it.status.isBacklog() && it.deletedAt == null }
            .sortedWith(compareByDescending<LegoSet> { it.priority.ordinal }.thenBy { it.addedAt })
            .firstOrNull()
}
