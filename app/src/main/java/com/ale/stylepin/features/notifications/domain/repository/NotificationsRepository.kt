package com.ale.stylepin.features.notifications.domain.repository

import com.ale.stylepin.features.notifications.data.datasources.remote.model.FCMTokenResponse
import com.ale.stylepin.features.notifications.domain.entities.Notification
import retrofit2.Response

interface NotificationsRepository {
    
    suspend fun saveFCMToken(
        token: String,
        deviceName: String
    ): Response<FCMTokenResponse>
    
    suspend fun getNotifications(): Result<List<Notification>>
}