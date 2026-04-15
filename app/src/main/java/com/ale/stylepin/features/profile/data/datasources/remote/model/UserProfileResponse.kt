package com.ale.stylepin.features.profile.data.datasources.remote.model

import com.google.gson.annotations.SerializedName

// Este es el envoltorio que dice tu Swagger
data class UserProfileResponse(
    val user: UserMeDto,
    @SerializedName("is_following") val isFollowing: Boolean,
    @SerializedName("is_followed_by") val isFollowedBy: Boolean
)