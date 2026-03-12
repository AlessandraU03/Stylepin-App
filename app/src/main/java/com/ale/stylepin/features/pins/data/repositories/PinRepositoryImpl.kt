package com.ale.stylepin.features.pins.data.repositories

import android.content.Context
import android.net.Uri
import android.util.Log
import com.ale.stylepin.features.pins.data.datasources.remote.api.PinApi
import com.ale.stylepin.features.pins.data.datasources.remote.mapper.toDomain
import com.ale.stylepin.features.pins.data.datasources.remote.mapper.toPartMap
import com.ale.stylepin.features.pins.data.datasources.remote.mapper.toUpdateDto
import com.ale.stylepin.features.pins.data.datasources.remote.mapper.toCreateDto
import com.ale.stylepin.features.pins.domain.entities.Pin
import com.ale.stylepin.features.pins.domain.entities.PinCreate
import com.ale.stylepin.features.pins.domain.entities.PinUpdate
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

    override suspend fun addPin(pinCreate: PinCreate): Boolean {
        return try {
            val dto = pinCreate.toCreateDto()
            val imagePart = buildImagePart(dto.imageUrl) ?: return false
            val bodyMap = dto.toPartMap()
            val response = api.createPin(imagePart, bodyMap)
            if (!response.isSuccessful) {
                Log.e(TAG, "addPin error ${response.code()}: ${response.errorBody()?.string()}")
            }
            response.isSuccessful
        } catch (e: Exception) {
            Log.e(TAG, "addPin exception", e)
            false
        }
    }

    override suspend fun getPinById(pinId: String): Pin {
        try {
            val response = api.getPinById(pinId)
            if (response.isSuccessful) {
                return response.body()?.toDomain()
                    ?: throw Exception("Respuesta vacía para el pin $pinId")
            } else {
                throw Exception("Error ${response.code()} al obtener el pin $pinId")
            }
        } catch (e: Exception) {
            Log.e(TAG, "getPinById exception for id=$pinId", e)
            throw e
        }
    }

    override suspend fun updatePin(pinUpdate: PinUpdate): Boolean {
        return try {
            val dto = pinUpdate.toUpdateDto()
            val response = api.updatePin(dto.pinId, dto)
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
            val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                ?: return null
            val requestFile = bytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("image", "pin_image.jpg", requestFile)
        } catch (e: Exception) {
            Log.e(TAG, "buildImagePart error for url=$imageUrl", e)
            null
        }
    }

    companion object {
        private const val TAG = "PinRepositoryImpl"
    }
}
