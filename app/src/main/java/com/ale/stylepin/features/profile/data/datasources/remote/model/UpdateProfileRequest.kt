package com.ale.stylepin.features.profile.data.datasources.remote.model

import com.google.gson.annotations.SerializedName

data class UpdateProfileRequest(
    @SerializedName("full_name") val fullName: String?,
    val bio: String?,
    @SerializedName("avatar_url") val avatarUrl: String? = null,
    val gender: String? = null,
    @SerializedName("preferred_styles") val preferredStyles: List<String>? = null
)