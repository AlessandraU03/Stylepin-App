package com.ale.stylepin.features.profile.data.datasources.remote.model

import com.google.gson.annotations.SerializedName

data class UserMeDto(
    val id: String,
    val username: String,
    val email: String, // Agregado
    @SerializedName("full_name") val fullName: String,
    val bio: String?,
    @SerializedName("avatar_url") val avatarUrl: String?,
    val gender: String?, // Agregado
    @SerializedName("preferred_styles") val preferredStyles: List<String>?, // Agregado
    @SerializedName("is_verified") val isVerified: Boolean // Agregado
)