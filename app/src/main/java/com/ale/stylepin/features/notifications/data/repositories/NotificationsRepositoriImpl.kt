package com.ale.stylepin.features.notifications.data.repositories

import android.os.Build
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
        val request = SaveFCMTokenRequest(
            token = token,
            deviceName = deviceName
        )
        return notificationApi.saveFCMToken(request)
    }

    override suspend fun getNotifications(): Result<List<Notification>> {
        return runCatching {
            // ✅ Sin trailing slash — evita el 307 que pierde el JWT
            val response = notificationApi.getNotifications()

            if (response.isSuccessful) {
                response.body()?.map { it.toDomain() } ?: emptyList()
            } else {
                throw Exception("Error al obtener notificaciones: ${response.code()} ${response.message()}")
            }
        }
    }
}