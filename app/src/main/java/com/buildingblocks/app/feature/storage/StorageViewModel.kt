package com.buildingblocks.app.feature.storage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buildingblocks.app.data.repository.StorageLocationRepository
import com.buildingblocks.app.domain.model.StorageLocation
import com.buildingblocks.app.domain.model.StorageLocationType
import com.buildingblocks.app.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

data class StorageUiState(
    val locations: List<StorageLocation> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class StorageViewModel @Inject constructor(
    private val repo: StorageLocationRepository,
    private val session: SessionManager
) : ViewModel() {

    val uiState: StateFlow<StorageUiState> =
        repo.getAllLocations(session.currentUserId)
            .map { StorageUiState(locations = it, isLoading = false) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), StorageUiState())

    fun addLocation(name: String, type: StorageLocationType) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val now = Instant.now()
            repo.saveLocation(
                StorageLocation(
                    id = UUID.randomUUID(),
                    userId = session.currentUserId,
                    name = name.trim(),
                    type = type,
                    createdAt = now,
                    updatedAt = now
                )
            )
        }
    }

    fun deleteLocation(id: UUID) {
        viewModelScope.launch { repo.deleteLocation(id) }
    }
}
