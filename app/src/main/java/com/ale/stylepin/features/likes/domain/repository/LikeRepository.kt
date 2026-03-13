package com.ale.stylepin.features.likes.domain.repository

import com.ale.stylepin.features.likes.domain.entities.LikeStatus

interface LikeRepository {
    suspend fun toggleLike(pinId: String): Result<LikeStatus>
    suspend fun getLikeStatus(pinId: String): Result<LikeStatus>
}
