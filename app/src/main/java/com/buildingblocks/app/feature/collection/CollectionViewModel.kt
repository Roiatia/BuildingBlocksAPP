package com.buildingblocks.app.feature.collection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buildingblocks.app.data.repository.LegoSetRepository
import com.buildingblocks.app.domain.model.LegoSet
import com.buildingblocks.app.domain.model.SetStatus
import com.buildingblocks.app.domain.model.SortOrder
import com.buildingblocks.app.domain.usecase.SortSetsUseCase
import com.buildingblocks.app.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class CollectionUiState(
    val sets: List<LegoSet> = emptyList(),
    val themes: List<String> = emptyList(),
    val searchQuery: String = "",
    val filterStatus: SetStatus? = null,
    val filterTheme: String? = null,
    val sortOrder: SortOrder = SortOrder.DATE_ADDED_DESC,
    val isLoading: Boolean = true
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CollectionViewModel @Inject constructor(
    private val repo: LegoSetRepository,
    private val session: SessionManager
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _filterStatus = MutableStateFlow<SetStatus?>(null)
    private val _filterTheme = MutableStateFlow<String?>(null)
    private val _sortOrder = MutableStateFlow(SortOrder.DATE_ADDED_DESC)

    private val rawSets = _searchQuery
        .debounce(200)
        .flatMapLatest { query ->
            if (query.isBlank()) repo.getAllSets(session.currentUserId)
            else repo.searchSets(session.currentUserId, query)
        }

    // Collapse filter/sort into one flow so the outer combine stays at 3 flows.
    private data class Filters(val status: SetStatus?, val theme: String?, val sort: SortOrder)
    private val filters = combine(_filterStatus, _filterTheme, _sortOrder) { s, t, o -> Filters(s, t, o) }

    val uiState: StateFlow<CollectionUiState> = combine(
        rawSets,
        filters,
        repo.getAllThemes(session.currentUserId)
    ) { sets, f, themes ->
        CollectionUiState(
            sets = SortSetsUseCase(
                sets.filter { f.status == null || it.status == f.status }
                    .filter { f.theme == null || it.theme == f.theme },
                f.sort
            ),
            themes = themes,
            searchQuery = _searchQuery.value,
            filterStatus = f.status,
            filterTheme = f.theme,
            sortOrder = f.sort,
            isLoading = false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CollectionUiState())

    fun onSearchQuery(query: String) { _searchQuery.value = query }
    fun onFilterStatus(status: SetStatus?) { _filterStatus.value = status }
    fun onFilterTheme(theme: String?) { _filterTheme.value = theme }
    fun onSortOrder(order: SortOrder) { _sortOrder.value = order }

    fun deleteSet(setId: String) {
        viewModelScope.launch { repo.deleteSet(UUID.fromString(setId)) }
    }

}
