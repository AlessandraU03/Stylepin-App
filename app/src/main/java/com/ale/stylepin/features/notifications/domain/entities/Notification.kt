package com.ale.stylepin.features.notifications.domain.entities


data class Notification(
    val id: String,
    val title: String,
    val body: String,
    val type: String,         // "like", "follow", "comment", "board_collaboration"
    val isRead: Boolean,
    val createdAt: String,
    val readAt: String?
)