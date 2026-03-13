package com.ale.stylepin.features.likes.data.repository

import com.ale.stylepin.features.likes.data.datasources.remote.api.LikeApi
import com.ale.stylepin.features.likes.data.datasources.remote.model.LikeRequest
import com.ale.stylepin.features.likes.domain.entities.LikeStatus
import com.ale.stylepin.features.likes.domain.repository.LikeRepository
import javax.inject.Inject

class LikeRepositoryImpl @Inject constructor(
    private val api: LikeApi
) : LikeRepository {

    override suspend fun toggleLike(pinId: String): Result<LikeStatus> {
        return try {
            // Primero verificamos el estado actual para saber si dar o quitar like
            // O simplemente intentamos dar like, y si falla (o según la lógica de la API)
            // En este caso, la API tiene POST para dar y DELETE para quitar.
            
            val statusResponse = api.getLikeStatus(pinId)
            if (statusResponse.isSuccessful) {
                val currentStatus = statusResponse.body()
                val response = if (currentStatus?.is_liked == true) {
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
            } else {
                Result.failure(Exception("Error al obtener estado de like"))
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
                Result.failure(Exception("Error al obtener estado de like"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
