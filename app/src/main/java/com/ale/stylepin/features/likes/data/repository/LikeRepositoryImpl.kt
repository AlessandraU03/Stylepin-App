package com.ale.stylepin.features.likes.data.repository

import com.ale.stylepin.features.likes.data.datasources.remote.api.LikeApi
import com.ale.stylepin.features.likes.data.datasources.remote.model.LikeRequest
import com.ale.stylepin.features.likes.domain.entities.LikeStatus
import com.ale.stylepin.features.likes.domain.repository.LikeRepository
import javax.inject.Inject

class LikeRepositoryImpl @Inject constructor(
    private val api: LikeApi
) : LikeRepository {

    override suspend fun toggleLike(pinId: String, isCurrentlyLiked: Boolean): Result<LikeStatus> {
        return try {
            val response = if (isCurrentlyLiked) {
                api.unlikePin(pinId)
            } else {
                api.likePin(LikeRequest(pinId))
            }

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                Result.success(LikeStatus(body.pin_id, body.is_liked, body.likes_count))
            } else {
                Result.failure(Exception("Error al cambiar estado de like"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getLikeStatus(pinId: String): Result<LikeStatus> {
        return try {
            val response = api.getLikeStatus(pinId)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                Result.success(LikeStatus(body.pin_id, body.is_liked, body.likes_count))
            } else {
                Result.failure(Exception("Error"))
            }
        } catch (e: Exception) { Result.failure(e) }
    }
}