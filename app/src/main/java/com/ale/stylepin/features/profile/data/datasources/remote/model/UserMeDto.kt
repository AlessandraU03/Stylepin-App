package com.ale.stylepin.features.profile.data.datasources.remote.model

import com.google.gson.annotations.SerializedName

data class UserMeDto(
    @SerializedName("id") val id: String,
    @SerializedName("username") val username: String,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("bio") val bio: String?,
    @SerializedName("avatar_url") val avatarUrl: String?
)