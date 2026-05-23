package com.buildingblocks.app.data.repository

import android.content.Context

interface AuthRepository {
    suspend fun signInWithGoogle(activityContext: Context): Result<Unit>
    suspend fun signOut()
    fun currentUserId(): String?
}
