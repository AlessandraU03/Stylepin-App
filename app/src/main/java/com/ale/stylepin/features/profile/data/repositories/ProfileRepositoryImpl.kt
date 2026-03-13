package com.ale.stylepin.features.profile.data.repositories

import android.content.Context
import android.net.Uri
import com.ale.stylepin.features.profile.data.datasources.remote.api.ProfileApi
import com.ale.stylepin.features.profile.data.datasources.remote.mapper.mapToDomain
import com.ale.stylepin.features.profile.data.datasources.remote.mapper.toDomain
import com.ale.stylepin.features.profile.data.datasources.remote.model.UpdateProfileRequest
import com.ale.stylepin.features.profile.data.datasources.remote.model.UserStatsDto
import com.ale.stylepin.features.profile.domain.entities.*
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
        val stats = try { api.getMyStats() } catch (e: Exception) { UserStatsDto(0, 0, 0) }
        return mapToDomain(user, stats)
    }

    override suspend fun updateProfile(fullName: String, bio: String, gender: String, avatarUrl: String?): Result<Unit> {
        return try {
            val request = UpdateProfileRequest(fullName.trim(), bio.trim(), avatarUrl, gender, null)
            api.updateProfile(request)
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    override suspend fun uploadAvatar(uriString: String): Result<String> {
        return try {
            val uri = Uri.parse(uriString)
            val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: throw Exception("Error")
            val requestFile = bytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", "avatar.jpg", requestFile)
            val response = api.uploadAvatar(body)
            Result.success(response.url)
        } catch (e: Exception) { Result.failure(e) }
    }

    override suspend fun getUserProfile(userId: String): Result<PublicProfile> {
        return try {
            val response = api.getUserProfile(userId)
            val user = response.user
            Result.success(
                PublicProfile(
                    id = user.id, username = user.username, fullName = user.fullName,
                    bio = user.bio ?: "", avatarUrl = user.avatarUrl ?: "",
                    pinsCount = user.totalPins, followersCount = user.totalFollowers,
                    followingCount = user.totalFollowing, isFollowing = response.isFollowing
                )
            )
        } catch (e: Exception) { Result.failure(e) }
    }

    override suspend fun getUserPins(userId: String): Result<List<ProfilePin>> {
        return try { Result.success(api.getUserPins(userId).pins.map { it.toDomain() }) } catch (e: Exception) { Result.failure(e) }
    }

    override suspend fun getUserBoards(userId: String): Result<List<ProfileBoard>> {
        return try { Result.success(api.getUserBoards(userId).boards.map { it.toDomain() }) } catch (e: Exception) { Result.failure(e) }
    }

    override suspend fun getUserSavedPins(userId: String): Result<List<ProfileSavedPin>> {
        return try { Result.success(api.getUserLikes(userId).likes.map { it.toDomain() }) } catch (e: Exception) { Result.failure(e) }
    }
}