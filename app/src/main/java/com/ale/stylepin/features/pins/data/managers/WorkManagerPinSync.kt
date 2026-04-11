package com.ale.stylepin.features.pins.data.managers

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.ale.stylepin.features.pins.data.workers.SyncWorker
import com.ale.stylepin.features.pins.domain.repository.PinSyncManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class WorkManagerPinSync @Inject constructor(
    @ApplicationContext private val context: Context
) : PinSyncManager {

    override fun schedulePeriodicSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED) // Restricción de red
            .setRequiresBatteryNotLow(true) // Restricción de batería (pide la lista de cotejo)
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            12, TimeUnit.HOURS
        ).setConstraints(constraints).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "StylePinPeriodicSync",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }
}