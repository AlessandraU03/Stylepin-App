package com.ale.stylepin.features.explore.data.datasources.remote.model

import com.google.gson.annotations.SerializedName

// --- MODELOS PARA BÚSQUEDA DE USUARIOS ---
data class UserSearchDto(
    val id: String,
    val username: String,
    @SerializedName("full_name") val fullName: String?, // En Swagger es full_name
    @SerializedName("avatar_url") val avatarUrl: String?, // En Swagger es avatar_url
    @SerializedName("is_verified") val isVerified: Boolean?
)

data class UserSearchResponse(
    val users: List<UserSearchDto>?,
    val total: Int?,
    val limit: Int?,
    val offset: Int?,
    @SerializedName("has_more") val hasMore: Boolean?
)

// --- MODELOS PARA BÚSQUEDA DE PINES (PinSummary de Swagger) ---
// La búsqueda devuelve un pin recortado, no el pin completo.
data class PinSearchSummaryDto(
    val id: String,
    @SerializedName("user_id") val userId: String?,
    @SerializedName("user_username") val userUsername: String?,
    @SerializedName("user_avatar_url") val userAvatarUrl: String?,
    @SerializedName("image_url") val imageUrl: String?,
    val title: String?,
    val category: String?,
    @SerializedName("likes_count") val likesCount: Int?,
    @SerializedName("saves_count") val savesCount: Int?,
    @SerializedName("created_at") val createdAt: String?
)

data class PinSearchResponse(
    val pins: List<PinSearchSummaryDto>?,
    val total: Int?,
    val limit: Int?,
    val offset: Int?,
    @SerializedName("has_more") val hasMore: Boolean?
)