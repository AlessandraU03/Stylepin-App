package com.ale.stylepin.features.profile.data.datasources.remote.model

import com.google.gson.annotations.SerializedName

// PINS
data class ProfilePinDto(
    val id: String,
    @SerializedName("image_url") val imageUrl: String
)

data class ProfilePinListResponse(
    val pins: List<ProfilePinDto>
)

// TABLEROS
data class ProfileBoardDto(
    val id: String,
    val name: String,
    @SerializedName("cover_image_url") val coverImageUrl: String?,
    @SerializedName("pins_count") val pinsCount: Int
)

data class ProfileBoardListResponse(
    val boards: List<ProfileBoardDto>
)

// GUARDADOS (Likes)
data class ProfileLikeDto(
    @SerializedName("pin_id") val pinId: String,
    // La API de likes a veces no devuelve la imagen directa en la lista simple,
    // por seguridad usamos esto en caso de que el backend lo incluya
    @SerializedName("image_url") val imageUrl: String?
)

data class ProfileLikeListResponse(
    val likes: List<ProfileLikeDto>
)