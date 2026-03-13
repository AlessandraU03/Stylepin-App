package com.ale.stylepin.features.pins.data.repositories

import android.content.Context
import android.net.Uri
import android.util.Log
import com.ale.stylepin.features.pins.data.datasources.remote.api.PinApi
import com.ale.stylepin.features.pins.data.datasources.remote.mapper.buildPartMap
import com.ale.stylepin.features.pins.data.datasources.remote.mapper.toDomain
import com.ale.stylepin.features.pins.data.datasources.remote.mapper.toUpdateRequest
import com.ale.stylepin.features.pins.domain.entities.Pin
import com.ale.stylepin.features.pins.domain.repository.PinsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class PinRepositoryImpl @Inject constructor(
    private val api: PinApi,
    @ApplicationContext private val context: Context
) : PinsRepository {

    override suspend fun getPins(): List<Pin> {
        return try {
            val response = api.getPins(emptyMap())
            if (response.isSuccessful) {
                response.body()?.pins?.map { it.toDomain() } ?: emptyList()
            } else {
                Log.e(TAG, "getPins error: ${response.code()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e(TAG, "getPins exception", e)
            emptyList()
        }
    }

    override suspend fun getPinById(pinId: String): Pin {
        val response = api.getPinById(pinId)
        if (response.isSuccessful) {
            return response.body()?.toDomain()
                ?: throw Exception("Pin no encontrado")
        } else {
            throw Exception("Error ${response.code()} al obtener el pin")
        }
    }

    override suspend fun addPin(
        title: String,
        imageUrl: String,
        category: String,
        season: String,
        description: String?,
        isPrivate: Boolean,
        styles: List<String>,
        occasions: List<String>,
        brands: List<String>,
        priceRange: String,
        whereToBuy: String?,
        purchaseLink: String?,
        colors: List<String>,
        tags: List<String>
    ): Boolean {
        return try {
            val imagePart = buildImagePart(imageUrl) ?: return false
            val fields = buildPartMap(
                title = title,
                category = category,
                season = season,
                description = description,
                isPrivate = isPrivate,
                styles = styles,
                occasions = occasions,
                brands = brands,
                priceRange = priceRange,
                whereToBuy = whereToBuy,
                purchaseLink = purchaseLink,
                colors = colors,
                tags = tags
            )
            val response = api.createPin(imagePart, fields)
            if (!response.isSuccessful) {
                Log.e(TAG, "addPin error ${response.code()}: ${response.errorBody()?.string()}")
            }
            response.isSuccessful
        } catch (e: Exception) {
            Log.e(TAG, "addPin exception", e)
            false
        }
    }

    override suspend fun updatePin(
        pinId: String,
        title: String,
        imageUrl: String?,
        category: String,
        season: String,
        description: String?,
        isPrivate: Boolean
    ): Boolean {
        return try {
            val request = toUpdateRequest(
                pinId = pinId,
                title = title,
                description = description,
                category = category,
                season = season,
                isPrivate = isPrivate,
                imageUrl = imageUrl
            )
            val response = api.updatePin(pinId, request)
            if (!response.isSuccessful) {
                Log.e(TAG, "updatePin error ${response.code()}: ${response.errorBody()?.string()}")
            }
            response.isSuccessful
        } catch (e: Exception) {
            Log.e(TAG, "updatePin exception", e)
            false
        }
    }

    override suspend fun deletePin(pinId: String): Boolean {
        return try {
            val response = api.deletePin(pinId)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e(TAG, "deletePin exception", e)
            false
        }
    }

    private fun buildImagePart(imageUrl: String): MultipartBody.Part? {
        return try {
            val uri = Uri.parse(imageUrl)
            val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: return null
            val requestFile = bytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("image", "pin_image.jpg", requestFile)
        } catch (e: Exception) {
            Log.e(TAG, "buildImagePart error", e)
            null
        }
    }

    companion object {
        private const val TAG = "PinRepositoryImpl"
    }

    override suspend fun toggleLike(pinId: String, isCurrentlyLiked: Boolean): Boolean {
        return try {
            val response = if (isCurrentlyLiked) {
                api.unlikePin(pinId)
            } else {
                api.likePin(com.ale.stylepin.features.pins.data.datasources.remote.model.LikeRequest(pinId))
            }
            response.isSuccessful
        } catch (e: Exception) { false }
    }

    override suspend fun getComments(pinId: String): List<com.ale.stylepin.features.pins.data.datasources.remote.model.CommentDto> {
        return try {
            val response = api.getComments(pinId)
            response.body()?.comments ?: emptyList()
        } catch (e: Exception) { emptyList() }
    }

    override suspend fun addComment(pinId: String, text: String): Boolean {
        return try {
            val request = com.ale.stylepin.features.pins.data.datasources.remote.model.CreateCommentRequest(pinId, text)
            api.addComment(request).isSuccessful
        } catch (e: Exception) { false }
    }
}