package com.ale.stylepin.features.notifications.data.repositories

import android.util.Log
import com.ale.stylepin.features.notifications.data.datasources.remote.api.NotificationApi
import com.ale.stylepin.features.notifications.data.datasources.remote.mapper.toDomain
import com.ale.stylepin.features.notifications.data.datasources.remote.model.FCMTokenResponse
import com.ale.stylepin.features.notifications.data.datasources.remote.model.SaveFCMTokenRequest
import com.ale.stylepin.features.notifications.domain.entities.Notification
import com.ale.stylepin.features.notifications.domain.repository.NotificationsRepository
import retrofit2.Response
import javax.inject.Inject

class NotificationsRepositoryImpl @Inject constructor(
    private val notificationApi: NotificationApi
) : NotificationsRepository {

    override suspend fun saveFCMToken(token: String, deviceName: String): Response<FCMTokenResponse> {
        val request = SaveFCMTokenRequest(token = token, deviceName = deviceName)
        return notificationApi.saveFCMToken(request)
    }

    override suspend fun getNotifications(): Result<List<Notification>> {
        return runCatching {
            Log.d("NOTIF_REPO", "Haciendo petición GET /api/v1/notifications...")

            val response = notificationApi.getNotifications()

            Log.d("NOTIF_REPO", "code=${response.code()}")
            Log.d("NOTIF_REPO", "isSuccessful=${response.isSuccessful}")
            Log.d("NOTIF_REPO", "body=${response.body()}")
            Log.d("NOTIF_REPO", "errorBody=${response.errorBody()?.string()}")

            if (response.isSuccessful) {
                val body = response.body()
                Log.d("NOTIF_REPO", "body size=${body?.size}")
                val result = body?.map { it.toDomain() } ?: emptyList()
                Log.d("NOTIF_REPO", "mapped ${result.size} notificaciones")
                result
            } else {
                throw Exception("Error ${response.code()}: ${response.message()}")
            }
        }
    }
}