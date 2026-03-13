package com.ale.stylepin.features.profile.domain.entities

data class ProfilePin(
    val id: String,
    val imageUrl: String
)

data class ProfileBoard(
    val id: String,
    val name: String,
    val coverImageUrl: String,
    val pinsCount: Int
)

data class ProfileSavedPin(
    val pinId: String,
    val imageUrl: String
)