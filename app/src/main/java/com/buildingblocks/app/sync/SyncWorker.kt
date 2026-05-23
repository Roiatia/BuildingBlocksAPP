package com.buildingblocks.app.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val syncManager: SyncManager
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return when (val result = syncManager.sync()) {
            is SyncResult.Success -> Result.success()
            is SyncResult.Skipped -> Result.success()
            is SyncResult.Error -> Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "brickvault_sync"
    }
}
