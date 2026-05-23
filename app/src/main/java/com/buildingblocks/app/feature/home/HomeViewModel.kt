package com.buildingblocks.app.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buildingblocks.app.data.repository.LegoSetRepository
import com.buildingblocks.app.domain.model.LegoSet
import com.buildingblocks.app.domain.usecase.GetRecommendedSetUseCase
import com.buildingblocks.app.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class HomeUiState(
    val totalSets: Int = 0,
    val backlogCount: Int = 0,
    val sealedCount: Int = 0,
    val oldestBacklogSet: LegoSet? = null,
    val recommendedSet: LegoSet? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: LegoSetRepository,
    private val session: SessionManager
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        repo.getSetCount(session.currentUserId),
        repo.getBacklogCount(session.currentUserId),
        repo.getSealedCount(session.currentUserId),
        repo.getOldestBacklogSet(session.currentUserId),
        repo.getBacklogSets(session.currentUserId)
    ) { total, backlog, sealed, oldest, backlogSets ->
        val recommended = GetRecommendedSetUseCase(backlogSets)
        HomeUiState(
            totalSets = total,
            backlogCount = backlog,
            sealedCount = sealed,
            oldestBacklogSet = oldest,
            recommendedSet = recommended,
            isLoading = false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())
}
