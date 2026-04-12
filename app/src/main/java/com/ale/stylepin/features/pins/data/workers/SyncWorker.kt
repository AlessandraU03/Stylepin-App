package com.ale.stylepin.features.pins.data.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import com.ale.stylepin.features.pins.domain.repository.PinsRepository
import java.util.concurrent.TimeUnit

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: PinsRepository // Inyección limpia gracias a hilt-work
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val result = repository.refreshPins()
            if (result.isSuccess) Result.success() else Result.retry()
        } catch (e: Exception) {
            // Reintentar hasta 3 veces, luego falla definitivamente
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }

    companion object {
        private const val WORK_NAME_PERIODIC = "pin_sync_periodic"
        private const val WORK_NAME_MANUAL   = "pin_sync_manual"

        /**
         * Programa la sincronización periódica automática cada 12 horas.
         * Solo corre con Wi-Fi y batería no baja.
         * Llama esto desde StylePinApp.onCreate()
         */
        fun schedulePeriodicSync(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED) // Solo Wi-Fi
                .setRequiresBatteryNotLow(true)
                .build()

            val request = PeriodicWorkRequestBuilder<SyncWorker>(12, TimeUnit.HOURS)
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 15, TimeUnit.MINUTES)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME_PERIODIC,
                ExistingPeriodicWorkPolicy.KEEP, // No interrumpe si ya estaba programado
                request
            )
        }

        /**
         * Sincronización MANUAL inmediata — para demostraciones.
         * Sin restricciones de red ni batería.
         * Como el botón "Hacer copia ahora" de WhatsApp.
         */
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

        /**
         * Observa el estado de la sincronización manual en tiempo real.
         * Úsalo en SyncSettingsScreen con observeAsState()
         */
        fun getManualSyncState(context: Context) =
            WorkManager.getInstance(context).getWorkInfosForUniqueWorkLiveData(WORK_NAME_MANUAL)
    }
}