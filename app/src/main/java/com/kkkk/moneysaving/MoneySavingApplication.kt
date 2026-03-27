package com.kkkk.moneysaving

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.kkkk.moneysaving.data.worker.DatabaseCleanupWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class MoneySavingApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        scheduleDatabaseCleanup()
    }

    private fun scheduleDatabaseCleanup() {
        val cleanupRequest = PeriodicWorkRequestBuilder<DatabaseCleanupWorker>(
            1, TimeUnit.DAYS
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "DatabaseCleanupWorker",
            ExistingPeriodicWorkPolicy.KEEP,
            cleanupRequest
        )
    }
}
