package com.anhq.taskmanagement.core.sync

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkerParameters
import com.anhq.taskmanagement.core.data.repository.TaskRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted val appContext: Context,
    @Assisted val workParams: WorkerParameters,
    private val taskRepository: TaskRepository,
) : CoroutineWorker(appContext, workParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val isSyncSuccess = awaitAll(
            async { taskRepository.sync() },
        ).all { it }
        if (isSyncSuccess) {
            Log.d(javaClass.name, "Synchronization is successful")
            Result.success()
        } else {
            Log.d(javaClass.name, "Synchronization is failed")
            Result.retry()
        }
    }


    companion object {
        fun getSyncWorkerRequest() = OneTimeWorkRequestBuilder<DelegatingWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED).build()
            )
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setInputData(SyncWorker::class.delegatedData())
            .build()
    }
}