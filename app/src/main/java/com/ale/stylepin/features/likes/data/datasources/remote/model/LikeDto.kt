package com.ale.stylepin.features.likes.data.datasources.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class LikeRequest(
    val pin_id: String
)

@Serializable
data class LikeResponse(
    val pin_id: String,
    val is_liked: Boolean,
    val likes_count: Int
)

@Serializable
data class LikeUserDto(
    val id: String,
    val user_id: String,
    val user_username: String,
    val user_full_name: String,
    val user_avatar_url: String?,
    val pin_id: String,
    val created_at: String
)

@Serializable
data class PinLikesResponse(
    val likes: List<LikeUserDto>,
    val total: Int,
    val limit: Int,
    val offset: Int,
    val has_more: Boolean
)
