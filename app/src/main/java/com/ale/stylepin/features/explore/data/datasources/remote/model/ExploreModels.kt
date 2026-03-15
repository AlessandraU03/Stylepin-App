package com.ale.stylepin.features.explore.data.datasources.remote.model

import com.google.gson.annotations.SerializedName

// DTOs para la búsqueda de usuarios
data class UserSearchDto(
    val id: String,
    val username: String,
    @SerializedName("full_name") val fullName: String?,
    @SerializedName("avatar_url") val avatarUrl: String?,
    @SerializedName("is_verified") val isVerified: Boolean
)

data class UserSearchResponse(
    val users: List<UserSearchDto>,
    val total: Int,
    val limit: Int,
    val offset: Int,
    @SerializedName("has_more") val hasMore: Boolean
)