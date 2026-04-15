package com.ale.stylepin.features.notifications.data.datasources.remote.mapper

import com.ale.stylepin.features.notifications.data.datasources.remote.model.NotificationResponse
import com.ale.stylepin.features.notifications.domain.entities.Notification

fun NotificationResponse.toDomain(): Notification {
    return Notification(
        id = id,
        title = title,
        body = body,
        type = type,
        isRead = isRead,
        createdAt = createdAt,
        readAt = readAt
    )
}