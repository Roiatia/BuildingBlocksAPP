package com.buildingblocks.app.feature.backlog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buildingblocks.app.data.repository.LegoSetRepository
import com.buildingblocks.app.domain.model.LegoSet
import com.buildingblocks.app.domain.model.SetStatus
import com.buildingblocks.app.domain.model.SortOrder
import com.buildingblocks.app.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class BacklogUiState(
    val sets: List<LegoSet> = emptyList(),
    val filterStatus: SetStatus? = null,
    val sortOrder: SortOrder = SortOrder.DATE_ADDED_ASC,
    val isLoading: Boolean = true
)

@HiltViewModel
class BacklogViewModel @Inject constructor(
    private val repo: LegoSetRepository,
    private val session: SessionManager
) : ViewModel() {

    private val _filterStatus = MutableStateFlow<SetStatus?>(null)
    private val _sortOrder = MutableStateFlow(SortOrder.DATE_ADDED_ASC)

    val uiState: StateFlow<BacklogUiState> = combine(
        repo.getBacklogSets(session.currentUserId),
        _filterStatus,
        _sortOrder
    ) { sets, status, sort ->
        val filtered = sets
            .filter { status == null || it.status == status }
            .sort(sort)
        BacklogUiState(sets = filtered, filterStatus = status, sortOrder = sort, isLoading = false)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BacklogUiState())

    fun onFilterStatus(status: SetStatus?) { _filterStatus.value = status }
    fun onSortOrder(order: SortOrder) { _sortOrder.value = order }

    private fun List<LegoSet>.sort(order: SortOrder) = when (order) {
        SortOrder.DATE_ADDED_ASC -> sortedBy { it.addedAt }
        SortOrder.DATE_ADDED_DESC -> sortedByDescending { it.addedAt }
        SortOrder.PIECE_COUNT_DESC -> sortedByDescending { it.pieceCount ?: 0 }
        SortOrder.PIECE_COUNT_ASC -> sortedBy { it.pieceCount ?: Int.MAX_VALUE }
        SortOrder.PRIORITY_HIGH -> sortedByDescending { it.priority.ordinal }
        SortOrder.DIFFICULTY_HIGH -> sortedByDescending { it.difficulty?.ordinal ?: -1 }
        else -> sortedBy { it.addedAt }
    }
}
