package com.ale.stylepin

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.ale.stylepin.features.pins.data.workers.SyncWorker
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class StylePinApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        // Programar sync periódica cada 12h (solo Wi-Fi, batería no baja)
        // Si ya estaba programada, KEEP no la interrumpe
        SyncWorker.schedulePeriodicSync(this)
    }
}