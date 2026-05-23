package com.buildingblocks.app.domain.usecase

import com.buildingblocks.app.domain.model.LegoSet
import com.buildingblocks.app.domain.model.SortOrder

object SortSetsUseCase {
    operator fun invoke(sets: List<LegoSet>, order: SortOrder): List<LegoSet> = when (order) {
        SortOrder.DATE_ADDED_DESC  -> sets.sortedByDescending { it.addedAt }
        SortOrder.DATE_ADDED_ASC   -> sets.sortedBy { it.addedAt }
        SortOrder.NAME_ASC         -> sets.sortedBy { it.name.lowercase() }
        SortOrder.NAME_DESC        -> sets.sortedByDescending { it.name.lowercase() }
        SortOrder.PIECE_COUNT_DESC -> sets.sortedByDescending { it.pieceCount ?: 0 }
        SortOrder.PIECE_COUNT_ASC  -> sets.sortedBy { it.pieceCount ?: 0 }
        SortOrder.PRIORITY_HIGH    -> sets.sortedByDescending { it.priority.ordinal }
        SortOrder.DIFFICULTY_HIGH  -> sets.sortedByDescending { it.difficulty?.ordinal ?: -1 }
    }
}
