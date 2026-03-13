package com.ale.stylepin.features.community.domain.entities

data class CommunityUser(
    val id: String,
    val username: String,
    val fullName: String,
    val avatarUrl: String,
    val isVerified: Boolean,
    val isFollowing: Boolean
)