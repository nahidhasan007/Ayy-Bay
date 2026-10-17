package com.ayybay.app.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.ayybay.app.domain.repository.StepRepository
import com.ayybay.app.util.StepPermission
import org.koin.core.context.GlobalContext
import java.util.concurrent.TimeUnit

/**
 * Periodically credits steps walked while the app wasn't open. The step counter is cumulative,
 * so how often this runs only affects how precisely steps land on the right day around midnight.
 */
class StepSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        if (!StepPermission.isGranted(applicationContext)) return Result.success()
        GlobalContext.get().get<StepRepository>().syncOnce()
        return Result.success()
    }

    companion object {
        private const val WORK_NAME = "step_sync"

        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<StepSyncWorker>(15, TimeUnit.MINUTES).build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request
            )
        }
    }
}
