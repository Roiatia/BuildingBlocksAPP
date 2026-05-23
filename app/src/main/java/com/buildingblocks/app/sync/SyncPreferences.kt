package com.buildingblocks.app.sync

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncPreferences @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs = context.getSharedPreferences("sync_prefs", Context.MODE_PRIVATE)

    fun getLastSyncTime(userId: String): String =
        prefs.getString("last_sync_$userId", "1970-01-01T00:00:00Z") ?: "1970-01-01T00:00:00Z"

    fun setLastSyncTime(userId: String, isoTime: String) {
        prefs.edit().putString("last_sync_$userId", isoTime).apply()
    }
}
