package com.buildingblocks.app.data.repository

import android.content.Context
import com.buildingblocks.app.data.remote.AuthRemoteDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val remote: AuthRemoteDataSource
) : AuthRepository {

    override suspend fun signInWithGoogle(activityContext: Context): Result<Unit> =
        remote.signInWithGoogle(activityContext)

    override suspend fun signOut() = remote.signOut()

    override fun currentUserId(): String? = remote.currentUserId()
}
