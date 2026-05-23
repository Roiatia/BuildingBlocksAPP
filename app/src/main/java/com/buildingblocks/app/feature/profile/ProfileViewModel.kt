package com.buildingblocks.app.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buildingblocks.app.data.repository.AuthRepository
import com.buildingblocks.app.session.SessionManager
import com.buildingblocks.app.sync.SyncManager
import com.buildingblocks.app.sync.SyncPreferences
import com.buildingblocks.app.sync.SyncResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val email: String? = null,
    val isSignedIn: Boolean = false,
    val lastSyncTime: String? = null,
    val isSyncing: Boolean = false,
    val syncError: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val authRepo: AuthRepository,
    private val syncManager: SyncManager,
    private val syncPrefs: SyncPreferences
) : ViewModel() {

    private val _isSyncing = MutableStateFlow(false)
    private val _syncError = MutableStateFlow<String?>(null)

    val uiState: StateFlow<ProfileUiState> = combine(
        sessionManager.userId,
        sessionManager.userEmail,
        _isSyncing,
        _syncError
    ) { userId, email, isSyncing, syncError ->
        val lastSync = userId?.let { syncPrefs.getLastSyncTime(it.toString()) }
            ?.takeIf { it != "1970-01-01T00:00:00Z" }
        ProfileUiState(
            email = email,
            isSignedIn = sessionManager.isSignedIn,
            lastSyncTime = lastSync,
            isSyncing = isSyncing,
            syncError = syncError
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProfileUiState())

    fun syncNow() {
        viewModelScope.launch {
            _isSyncing.value = true
            _syncError.value = null
            when (val result = syncManager.sync()) {
                is SyncResult.Error -> _syncError.value = result.message
                else -> Unit
            }
            _isSyncing.value = false
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepo.signOut()
            sessionManager.signOut()
        }
    }
}
