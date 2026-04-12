package com.ale.stylepin.features.notifications.data.datasources.remote.model

import com.google.gson.annotations.SerializedName

// Mapea la respuesta de POST /users/fcm-token
data class FCMTokenResponse(
    @SerializedName("id") val id: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("device_token") val deviceToken: String,
    @SerializedName("device_name") val deviceName: String?,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("message") val message: String
)

// Mapea la respuesta de GET /notifications/
data class NotificationResponse(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("body") val body: String,
    @SerializedName("type") val type: String,
    @SerializedName("is_read") val isRead: Boolean,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("read_at") val readAt: String?
)

data class SaveFCMTokenRequest(
    @SerializedName("token")
    val token: String,

    @SerializedName("device_name")
    val deviceName: String? = null
)