package com.ale.stylepin.features.pins.domain.entities

data class Comment(
    val id: String,
    val pinId: String,
    val userId: String,
    val username: String,
    val userFullName: String,
    val userAvatarUrl: String,
    val text: String,
    val createdAt: String
)