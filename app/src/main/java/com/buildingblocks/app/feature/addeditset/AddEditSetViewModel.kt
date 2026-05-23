package com.buildingblocks.app.feature.addeditset

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buildingblocks.app.data.repository.LegoSetRepository
import com.buildingblocks.app.data.repository.StorageLocationRepository
import com.buildingblocks.app.domain.model.*
import com.buildingblocks.app.domain.usecase.ValidateAddSetUseCase
import com.buildingblocks.app.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

data class AddEditSetFormState(
    val name: String = "",
    val legoSetNumber: String = "",
    val theme: String = "",
    val year: String = "",
    val pieceCount: String = "",
    val imageUrl: String = "",
    val status: SetStatus = SetStatus.SEALED,
    val purchasedAt: LocalDate? = null,
    val difficulty: Difficulty? = null,
    val priority: Priority = Priority.MEDIUM,
    val storageLocationId: UUID? = null,
    val notes: String = "",
    val nameError: String? = null,
    val isSaving: Boolean = false,
    val savedSuccessfully: Boolean = false,
    val limitReached: Boolean = false,
    // Preserved from original when editing
    val originalAddedAt: Instant? = null,
    val originalCreatedAt: Instant? = null
)

@HiltViewModel
class AddEditSetViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val setRepo: LegoSetRepository,
    private val storageRepo: StorageLocationRepository,
    private val session: SessionManager
) : ViewModel() {

    private val editSetId: UUID? = savedStateHandle.get<String>("setId")?.let { UUID.fromString(it) }
    val isEditMode: Boolean = editSetId != null

    private val _form = MutableStateFlow(AddEditSetFormState())
    val form: StateFlow<AddEditSetFormState> = _form.asStateFlow()

    val storageLocations: StateFlow<List<StorageLocation>> =
        storageRepo.getAllLocations(session.currentUserId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        if (editSetId != null) {
            viewModelScope.launch {
                setRepo.getSetById(editSetId).firstOrNull()?.let { set ->
                    _form.value = AddEditSetFormState(
                        name = set.name,
                        legoSetNumber = set.legoSetNumber ?: "",
                        theme = set.theme ?: "",
                        year = set.year?.toString() ?: "",
                        pieceCount = set.pieceCount?.toString() ?: "",
                        imageUrl = set.imageUrl ?: "",
                        status = set.status,
                        purchasedAt = set.purchasedAt,
                        difficulty = set.difficulty,
                        priority = set.priority,
                        storageLocationId = set.storageLocationId,
                        notes = set.notes ?: "",
                        originalAddedAt = set.addedAt,
                        originalCreatedAt = set.createdAt
                    )
                }
            }
        }
    }

    fun onName(v: String) = _form.update { it.copy(name = v, nameError = null) }
    fun onSetNumber(v: String) = _form.update { it.copy(legoSetNumber = v) }
    fun onTheme(v: String) = _form.update { it.copy(theme = v) }
    fun onYear(v: String) = _form.update { it.copy(year = v) }
    fun onPieceCount(v: String) = _form.update { it.copy(pieceCount = v) }
    fun onImageUrl(v: String) = _form.update { it.copy(imageUrl = v) }
    fun onStatus(v: SetStatus) = _form.update { it.copy(status = v) }
    fun onPurchasedAt(v: LocalDate?) = _form.update { it.copy(purchasedAt = v) }
    fun onDifficulty(v: Difficulty?) = _form.update { it.copy(difficulty = v) }
    fun onPriority(v: Priority) = _form.update { it.copy(priority = v) }
    fun onStorageLocation(v: UUID?) = _form.update { it.copy(storageLocationId = v) }
    fun onNotes(v: String) = _form.update { it.copy(notes = v) }

    fun save() {
        val f = _form.value
        val validation = ValidateAddSetUseCase(f.name, f.legoSetNumber)
        if (!validation.isValid) {
            _form.update { it.copy(nameError = validation.errorMessage) }
            return
        }
        _form.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            // 100-set limit for free users (only enforced on new sets, not edits)
            if (editSetId == null) {
                val count = setRepo.getSetCount(session.currentUserId).firstOrNull() ?: 0
                if (count >= FREE_PLAN_LIMIT) {
                    _form.update { it.copy(isSaving = false, limitReached = true) }
                    return@launch
                }
            }

            val now = Instant.now()
            val derivedName = f.name.trim().ifBlank {
                f.legoSetNumber.trim().let { "Set $it" }
            }
            val set = LegoSet(
                id = editSetId ?: UUID.randomUUID(),
                userId = session.currentUserId,
                name = derivedName,
                legoSetNumber = f.legoSetNumber.trim().takeIf { it.isNotBlank() },
                theme = f.theme.trim().takeIf { it.isNotBlank() },
                year = f.year.trim().toIntOrNull(),
                pieceCount = f.pieceCount.trim().toIntOrNull(),
                imageUrl = f.imageUrl.trim().takeIf { it.isNotBlank() },
                status = f.status,
                addedAt = f.originalAddedAt ?: now,
                purchasedAt = f.purchasedAt,
                difficulty = f.difficulty,
                priority = f.priority,
                storageLocationId = f.storageLocationId,
                notes = f.notes.trim().takeIf { it.isNotBlank() },
                createdAt = f.originalCreatedAt ?: now,
                updatedAt = now
            )
            setRepo.saveSet(set)
            _form.update { it.copy(isSaving = false, savedSuccessfully = true) }
        }
    }

    companion object {
        const val FREE_PLAN_LIMIT = 100
    }
}
