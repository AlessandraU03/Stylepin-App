package com.ale.stylepin.features.profile.data.datasources.remote.model

import com.google.gson.annotations.SerializedName

data class UserMeDto(
    val id: String,
    val username: String,
    val email: String,
    @SerializedName("full_name") val fullName: String,
    val bio: String? = null,
    @SerializedName("avatar_url") val avatarUrl: String? = null,
    val gender: String,
    @SerializedName("preferred_styles") val preferredStyles: List<String>,
    @SerializedName("is_verified") val isVerified: Boolean,
    val role: String,
    @SerializedName("created_at") val createdAt: String
)

data class UserStatsDto(
    @SerializedName("total_pins") val totalPins: Int,
    @SerializedName("total_followers") val totalFollowers: Int,
    @SerializedName("total_following") val totalFollowing: Int,
    @SerializedName("total_boards") val totalBoards: Int
)

data class UpdateProfileRequest(
    @SerializedName("full_name") val fullName: String? = null,
    val bio: String? = null,
    @SerializedName("avatar_url") val avatarUrl: String? = null,
    val gender: String? = null,
    @SerializedName("preferred_styles") val preferredStyles: List<String>? = null
)
