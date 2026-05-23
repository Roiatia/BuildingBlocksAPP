package com.buildingblocks.app.session

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.SessionStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    private val auth: Auth
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // Fallback local user — used only when Supabase auth is unavailable (no secrets yet).
    val localUserId: UUID = UUID.fromString("00000000-0000-0000-0000-000000000001")

    private val _userId = MutableStateFlow<UUID?>(localUserId)
    val userId: StateFlow<UUID?> = _userId.asStateFlow()

    val currentUserId: UUID get() = _userId.value ?: localUserId

    val isSignedIn: Boolean get() = _userId.value != null && _userId.value != localUserId

    val userEmail: StateFlow<String?> = auth.sessionStatus
        .map { status ->
            when (status) {
                is SessionStatus.Authenticated -> auth.currentUserOrNull()?.email
                else -> null
            }
        }
        .stateIn(scope, SharingStarted.Eagerly, null)

    init {
        scope.launch {
            auth.sessionStatus.collect { status ->
                when (status) {
                    is SessionStatus.Authenticated -> {
                        val remoteId = auth.currentUserOrNull()?.id
                        if (remoteId != null) {
                            _userId.value = UUID.fromString(remoteId)
                        }
                    }
                    is SessionStatus.NotAuthenticated -> {
                        _userId.value = localUserId
                    }
                    else -> Unit
                }
            }
        }
    }

    fun signOut() { _userId.value = localUserId }
}
