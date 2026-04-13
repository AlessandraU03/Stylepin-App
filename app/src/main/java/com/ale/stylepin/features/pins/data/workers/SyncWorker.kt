package com.ale.stylepin.features.pins.data.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import com.ale.stylepin.features.pins.domain.repository.PinsRepository
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: PinsRepository
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        private const val WORK_NAME_PERIODIC = "pin_sync_periodic"
        private const val WORK_NAME_MANUAL   = "pin_sync_manual"

        const val KEY_PROGRESS = "progress"  // Int 0–100
        const val KEY_STAGE    = "stage"     // String con el texto de la etapa

        fun schedulePeriodicSync(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .setRequiresBatteryNotLow(true)
                .build()
            val request = PeriodicWorkRequestBuilder<SyncWorker>(12, TimeUnit.HOURS)
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 15, TimeUnit.MINUTES)
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME_PERIODIC,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }

        fun runNow(context: Context) {
            val request = OneTimeWorkRequestBuilder<SyncWorker>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                WORK_NAME_MANUAL,
                ExistingWorkPolicy.REPLACE,
                request
            )
        }

        fun getManualSyncState(context: Context) =
            WorkManager.getInstance(context).getWorkInfosForUniqueWorkLiveData(WORK_NAME_MANUAL)
    }

    override suspend fun doWork(): Result {
        return try {
            reportProgress(5, "Iniciando sincronización...")
            delay(400)

            reportProgress(20, "Conectando con el servidor...")
            delay(300)

            reportProgress(45, "Descargando pines...")
            val result = repository.refreshPins()

            reportProgress(80, "Guardando datos locales...")
            delay(400)

            reportProgress(100, "Sincronización completada")
            delay(200)

            if (result.isSuccess) Result.success() else Result.retry()
        } catch (e: Exception) {
            reportProgress(0, "Error al sincronizar")
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }

    private suspend fun reportProgress(percent: Int, stage: String) {
        setProgress(workDataOf(KEY_PROGRESS to percent, KEY_STAGE to stage))
    }
}