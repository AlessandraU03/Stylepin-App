package com.ale.stylepin.features.community.data.datasources.remote.model

import com.google.gson.annotations.SerializedName

// DTO para la lista de Seguidores
data class FollowerProfileDto(
    @SerializedName("user_id") val userId: String,
    val username: String,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("avatar_url") val avatarUrl: String?,
    @SerializedName("is_verified") val isVerified: Boolean,
    @SerializedName("is_following_back") val isFollowingBack: Boolean
)

// DTO para la lista de Seguidos
data class FollowingProfileDto(
    @SerializedName("user_id") val userId: String,
    val username: String,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("avatar_url") val avatarUrl: String?,
    @SerializedName("is_verified") val isVerified: Boolean,
    @SerializedName("is_followed_by_me") val isFollowedByMe: Boolean
)

data class FollowersListResponse(
    val followers: List<FollowerProfileDto>,
    val total: Int,
    val limit: Int,
    val offset: Int,
    @SerializedName("has_more") val hasMore: Boolean
)

data class FollowingListResponse(
    val following: List<FollowingProfileDto>,
    val total: Int,
    val limit: Int,
    val offset: Int,
    @SerializedName("has_more") val hasMore: Boolean
)

data class FollowUserRequest(
    @SerializedName("user_id") val userId: String
)