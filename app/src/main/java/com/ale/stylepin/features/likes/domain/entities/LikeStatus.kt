package com.ale.stylepin.features.likes.domain.entities

data class LikeStatus(
    val pinId: String,
    val isLiked: Boolean,
    val likesCount: Int
)
