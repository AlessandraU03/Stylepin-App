package com.ale.stylepin.features.profile.data.datasources.remote.model
import com.google.gson.annotations.SerializedName

data class PublicUserProfileDto(
    val id: String,
    val username: String,
    @SerializedName("full_name") val fullName: String,
    val bio: String?,
    @SerializedName("avatar_url") val avatarUrl: String?,
    @SerializedName("total_pins") val totalPins: Int,
    @SerializedName("total_followers") val totalFollowers: Int,
    @SerializedName("total_following") val totalFollowing: Int
)

data class UserProfileResponseDto(
    val user: PublicUserProfileDto,
    @SerializedName("is_following") val isFollowing: Boolean,
    @SerializedName("is_followed_by") val isFollowedBy: Boolean
)