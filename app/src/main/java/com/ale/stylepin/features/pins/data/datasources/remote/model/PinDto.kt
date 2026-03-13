package com.ale.stylepin.features.pins.data.datasources.remote.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class PinDto(
    val id: String,
    val user_id: String,
    val user_username: String,
    val user_full_name: String,
    val user_avatar_url: String? = null,
    val user_is_verified: Boolean = false,
    val image_url: String,
    val title: String,
    val description: String? = null,
    val category: String,
    val styles: List<String> = emptyList(),
    val occasions: List<String> = emptyList(),
    val season: String,
    val brands: List<String> = emptyList(),
    val price_range: String,
    val where_to_buy: String? = null,
    val purchase_link: String? = null,
    val likes_count: Int = 0,
    val saves_count: Int = 0,
    val comments_count: Int = 0,
    val views_count: Int = 0,
    val colors: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val is_private: Boolean = false,
    val created_at: String,
    val updated_at: String,
    val is_liked_by_me: Boolean = false,
    val is_saved_by_me: Boolean = false
)

@Serializable
data class PinsListResponse(
    val pins: List<PinDto>,
    val total: Int = 0,
    val limit: Int = 20,
    val offset: Int = 0,
    val has_more: Boolean = false
)

@Serializable
data class UpdatePinDto(
    val pinId: String,
    val title: String,
    val imageUrl: String?,
    val category: String,
    val season: String,
    val description: String?,
    val isPrivate: Boolean
)

// --- MODELOS DE COMENTARIOS ---
data class CreateCommentRequest(
    @SerializedName("pin_id") val pinId: String,
    val text: String
)

data class CommentDto(
    val id: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("user_username") val userUsername: String,
    @SerializedName("user_full_name") val userFullName: String?,
    @SerializedName("user_avatar_url") val userAvatarUrl: String?,
    val text: String,
    @SerializedName("created_at") val createdAt: String
)

data class CommentListResponse(
    val comments: List<CommentDto>
)