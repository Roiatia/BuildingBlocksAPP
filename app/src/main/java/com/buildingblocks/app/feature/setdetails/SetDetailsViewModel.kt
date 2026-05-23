package com.buildingblocks.app.feature.setdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buildingblocks.app.data.repository.LegoSetRepository
import com.buildingblocks.app.data.repository.StorageLocationRepository
import com.buildingblocks.app.domain.model.LegoSet
import com.buildingblocks.app.domain.model.SetStatus
import com.buildingblocks.app.domain.model.StorageLocation
import com.buildingblocks.app.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

sealed class SetDetailsUiState {
    object Loading : SetDetailsUiState()
    data class Success(val set: LegoSet, val storageLocation: StorageLocation?) : SetDetailsUiState()
    object NotFound : SetDetailsUiState()
}

@HiltViewModel
class SetDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val setRepo: LegoSetRepository,
    private val storageRepo: StorageLocationRepository,
    private val session: SessionManager
) : ViewModel() {

    private val setId: UUID = UUID.fromString(checkNotNull(savedStateHandle["setId"]))

    val uiState: StateFlow<SetDetailsUiState> = setRepo.getSetById(setId)
        .flatMapLatest { set ->
            if (set == null) flowOf(SetDetailsUiState.NotFound)
            else {
                val locationFlow = set.storageLocationId
                    ?.let { storageRepo.getLocationById(it) }
                    ?: flowOf(null)
                locationFlow.map { location -> SetDetailsUiState.Success(set, location) }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SetDetailsUiState.Loading)

    fun updateStatus(newStatus: SetStatus) {
        val current = (uiState.value as? SetDetailsUiState.Success)?.set ?: return
        viewModelScope.launch {
            setRepo.saveSet(current.copy(status = newStatus, updatedAt = Instant.now()))
        }
    }

    fun deleteSet(onDeleted: () -> Unit) {
        viewModelScope.launch {
            setRepo.deleteSet(setId)
            onDeleted()
        }
    }
}
