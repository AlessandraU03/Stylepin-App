package com.ale.stylepin.features.pins.domain.repository

interface PinSyncManager {
    fun schedulePeriodicSync()
}
