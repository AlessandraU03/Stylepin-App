package com.ale.stylepin.features.profile.data.datasources.remote.model

import com.google.gson.annotations.SerializedName

data class UserMeDto(
    val id: String? = null,
    val username: String? = null,
    val email: String? = null,
    @SerializedName("full_name") val fullName: String? = null,
    val bio: String? = null,
    @SerializedName("avatar_url") val avatarUrl: String? = null,
    val gender: String? = null,
    @SerializedName("preferred_styles") val preferredStyles: List<String>? = emptyList(),
    @SerializedName("is_verified") val isVerified: Boolean? = false
)