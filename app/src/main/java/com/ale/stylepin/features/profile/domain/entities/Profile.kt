package com.ale.stylepin.features.profile.domain.entities

data class Profile(
    val id: String,
    val username: String,
    val email: String,
    val fullName: String,
    val bio: String,
    val avatarUrl: String,
    val gender: String,
    val followersCount: Int,
    val followingCount: Int,
    val pinsCount: Int
)