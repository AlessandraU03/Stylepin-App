package com.ale.stylepin.features.pins.data.repositories

import android.content.Context
import android.net.Uri
import android.util.Log
import com.ale.stylepin.features.pins.data.datasources.local.dao.PinDao
import com.ale.stylepin.features.pins.data.datasources.local.mapper.toDomain
import com.ale.stylepin.features.pins.data.datasources.local.mapper.toEntity
import com.ale.stylepin.features.pins.data.datasources.remote.api.PinApi
import com.ale.stylepin.features.pins.data.datasources.remote.mapper.buildPartMap
import com.ale.stylepin.features.pins.data.datasources.remote.mapper.toDomain
import com.ale.stylepin.features.pins.data.datasources.remote.mapper.toUpdateRequest
import com.ale.stylepin.features.pins.data.datasources.remote.model.CreateCommentRequest
import com.ale.stylepin.features.pins.data.datasources.remote.model.UpdatePinDto
import com.ale.stylepin.features.pins.domain.entities.Comment
import com.ale.stylepin.features.pins.domain.entities.Pin
import com.ale.stylepin.features.pins.domain.repository.PinsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class PinRepositoryImpl @Inject constructor(
    private val api: PinApi,
    private val pinDao: PinDao,
    @ApplicationContext private val context: Context
) : PinsRepository {

    override fun getPinsFlow(): Flow<List<Pin>> {
        return pinDao.getAllPins().map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun refreshPins(): Result<Unit> {
        return try {
            val response = api.getPins(emptyMap())
            if (response.isSuccessful) {
                val remotePins = response.body()?.pins ?: emptyList()
                pinDao.insertPins(remotePins.map { it.toEntity() })
                Result.success(Unit)
            } else Result.failure(Exception("Error al sincronizar"))
        } catch (e: Exception) { Result.failure(e) }
    }

    override suspend fun getPins(): List<Pin> {
        return try {
            val response = api.getPins(emptyMap())
            if (response.isSuccessful) {
                val dtos = response.body()?.pins ?: emptyList()
                pinDao.insertPins(dtos.map { it.toEntity() })
                dtos.map { it.toDomain() }
            } else emptyList()
        } catch (e: Exception) { emptyList() }
    }

    override suspend fun getPinById(pinId: String): Pin {
        val response = api.getPinById(pinId)
        return response.body()?.toDomain() ?: throw Exception("Pin no encontrado")
    }

    // GUARDA EL CAMBIO DE LIKE/GUARDADO EN TU TELÉFONO
    override suspend fun savePinLocal(pin: Pin) {
        pinDao.insertPins(listOf(pin.toEntity()))
    }

    override suspend fun addPin(
        title: String, imageUrl: String, category: String, season: String,
        description: String?, isPrivate: Boolean, styles: List<String>,
        occasions: List<String>, brands: List<String>, priceRange: String,
        whereToBuy: String?, purchaseLink: String?, colors: List<String>, tags: List<String>
    ): Boolean {
        return try {
            val imagePart = buildImagePart(imageUrl) ?: return false
            val fields = buildPartMap(
                title, category, season, description, isPrivate, styles, occasions,
                brands, priceRange, whereToBuy, purchaseLink, colors, tags
            )
            val response = api.createPin(imagePart, fields)
            if (response.isSuccessful) {
                response.body()?.let { pinDao.insertPins(listOf(it.toEntity())) }
            }
            response.isSuccessful
        } catch (e: Exception) { false }
    }

    override suspend fun updatePin(
        pinId: String, title: String, imageUrl: String?, category: String,
        season: String, description: String?, isPrivate: Boolean,
        styles: List<String>, occasions: List<String>, brands: List<String>,
        priceRange: String, whereToBuy: String?, purchaseLink: String?,
        colors: List<String>, tags: List<String>
    ): Boolean {
        return try {
            val request = UpdatePinDto(
                pinId = pinId,
                title = title,
                imageUrl = imageUrl,
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
            val response = api.updatePin(pinId, request)
            if (response.isSuccessful) response.body()?.let { pinDao.insertPins(listOf(it.toEntity())) }
            response.isSuccessful
        } catch (e: Exception) { false }
    }

    override suspend fun deletePin(pinId: String): Boolean {
        return try {
            val success = api.deletePin(pinId).isSuccessful
           if (success) pinDao.deleteById(pinId)
            success
        } catch (e: Exception) { false }
    }

    private fun buildImagePart(imageUrl: String): MultipartBody.Part? {
        return try {
            val uri = Uri.parse(imageUrl)
            val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: return null
            val requestFile = bytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("image", "pin_image.jpg", requestFile)
        } catch (e: Exception) { null }
    }

    override suspend fun getPinComments(pinId: String): List<Comment> {
        val response = api.getPinComments(pinId)
        return response.body()?.comments?.map { dto ->
            Comment(
                id = dto.id, pinId = dto.pin_id, userId = dto.user_id,
                username = dto.user_username, userFullName = dto.user_full_name,
                userAvatarUrl = dto.user_avatar_url ?: "", text = dto.text, createdAt = dto.created_at
            )
        } ?: emptyList()
    }

    override suspend fun addComment(pinId: String, text: String): Comment {
        val response = api.addComment(CreateCommentRequest(pinId, text))
        val dto = response.body() ?: throw Exception("Error al comentar")
        return Comment(
            id = dto.id, pinId = dto.pin_id, userId = dto.user_id,
            username = dto.user_username, userFullName = dto.user_full_name,
            userAvatarUrl = dto.user_avatar_url ?: "", text = dto.text, createdAt = dto.created_at
        )
    }
}