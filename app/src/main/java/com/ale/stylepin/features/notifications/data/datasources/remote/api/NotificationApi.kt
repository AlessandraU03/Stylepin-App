package com.ale.stylepin.features.notifications.data.datasources.remote.api

import com.ale.stylepin.features.notifications.data.datasources.remote.model.NotificationResponse
import com.ale.stylepin.features.notifications.data.datasources.remote.model.FCMTokenResponse
import com.ale.stylepin.features.notifications.data.datasources.remote.model.SaveFCMTokenRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface NotificationApi {

    // ✅ Solo el path relativo — Retrofit ya tiene la base URL con /api/v1/
    // El backend recibe: POST /api/v1/users/fcm-token con Body JSON {token, device_name}
    @POST("api/v1/users/fcm-token")
    suspend fun saveFCMToken(
        @Body request: SaveFCMTokenRequest
    ): Response<FCMTokenResponse>

    // ✅ Sin trailing slash — el backend redirige /notifications/ → 307 → /notifications → 401
    // porque la redirección 307 no preserva el header Authorization
    @GET("api/v1/notifications")
    suspend fun getNotifications(
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): Response<List<NotificationResponse>>
}