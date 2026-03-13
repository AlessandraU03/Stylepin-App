package com.ale.stylepin.features.profile.domain.entities

data class PublicProfile(
    val id: String, val username: String, val fullName: String, val bio: String,
    val avatarUrl: String, val pinsCount: Int, val followersCount: Int,
    val followingCount: Int, val isFollowing: Boolean
)