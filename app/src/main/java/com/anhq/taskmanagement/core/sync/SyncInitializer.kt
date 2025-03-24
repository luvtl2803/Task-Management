package com.anhq.taskmanagement.core.sync

import android.content.Context
import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager

private const val SYNC_WORKER_NAME = "main_sync_worker"

object SyncInitializer {
    fun init(context: Context) {
        Log.d("AAA", "Sync")
        WorkManager.getInstance(context).enqueueUniqueWork(
            SYNC_WORKER_NAME,
            ExistingWorkPolicy.REPLACE,
            SyncWorker.getSyncWorkerRequest()
        )
    }
}