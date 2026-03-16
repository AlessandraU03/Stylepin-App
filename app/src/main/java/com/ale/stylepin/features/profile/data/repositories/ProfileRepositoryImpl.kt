package com.ale.stylepin.features.profile.data.repositories

import android.content.Context
import android.net.Uri
import android.util.Log
import com.ale.stylepin.features.profile.data.datasources.remote.api.ProfileApi
import com.ale.stylepin.features.profile.data.datasources.remote.mapper.mapToDomain
import com.ale.stylepin.features.profile.data.datasources.remote.model.UpdateProfileRequest
import com.ale.stylepin.features.profile.data.datasources.remote.model.UserStatsDto
import com.ale.stylepin.features.profile.domain.entities.Profile
import com.ale.stylepin.features.profile.domain.repositories.ProfileRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val api: ProfileApi,
    @ApplicationContext private val context: Context
) : ProfileRepository {

    override suspend fun getMyProfile(): Profile {
        val user = api.getMyProfile()
        val stats = try {
            api.getMyStats()
        } catch (e: Exception) {
            Log.e("ProfileRepository", "Fallo stats: ${e.message}")
            UserStatsDto(totalPins = 0, totalFollowers = 0, totalFollowing = 0)
        }
        return mapToDomain(user, stats)
    }

    // 👇 NUEVO: Implementación para traer otros perfiles
    override suspend fun getProfileById(userId: String): Result<Profile> {
        return try {
            val response = api.getUserProfileById(userId)
            if (response.isSuccessful) {
                response.body()?.let { user ->
                    // Usamos stats en 0 temporalmente para que mapee bien (si tu backend tiene endpoint de stats públicos, se agregaría aquí)
                    val dummyStats = UserStatsDto(totalPins = 0, totalFollowers = 0, totalFollowing = 0)
                    Result.success(mapToDomain(user, dummyStats))
                } ?: Result.failure(Exception("Perfil vacío"))
            } else {
                Result.failure(Exception("Error al obtener el perfil: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProfile(fullName: String, bio: String, gender: String, avatarUrl: String?): Result<Unit> {
        return try {
            val request = UpdateProfileRequest(
                fullName = fullName.trim(),
                bio = bio.trim(),
                gender = gender,
                avatarUrl = avatarUrl,
                preferredStyles = null
            )
            api.updateProfile(request)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteMyAccount(): Result<Unit> {
        return try {
            api.deleteMyAccount()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadAvatar(uriString: String): Result<String> {
        return try {
            val uri = Uri.parse(uriString)
            val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                ?: return Result.failure(Exception("No se pudo leer la imagen"))

            val requestFile = bytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("file", "avatar.jpg", requestFile)

            val response = api.uploadAvatar(part)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.url)
            } else {
                Result.failure(Exception("Error al subir imagen"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}