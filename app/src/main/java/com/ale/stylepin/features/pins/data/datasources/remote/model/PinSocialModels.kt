package com.ale.stylepin.features.pins.data.datasources.remote.model

import com.google.gson.annotations.SerializedName

data class LikeRequest(@SerializedName("pin_id") val pinId: String)

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