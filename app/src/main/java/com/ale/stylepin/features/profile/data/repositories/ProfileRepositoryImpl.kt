package com.ale.stylepin.features.profile.data.repositories

import android.util.Log
import com.ale.stylepin.features.profile.data.datasources.remote.api.ProfileApi
import com.ale.stylepin.features.profile.data.datasources.remote.mapper.toDomain
import com.ale.stylepin.features.profile.data.datasources.remote.mapper.mapToDomain
import com.ale.stylepin.features.profile.data.datasources.remote.model.UpdateProfileRequest
import com.ale.stylepin.features.profile.data.datasources.remote.model.UserStatsDto
import com.ale.stylepin.features.profile.domain.entities.Profile
import com.ale.stylepin.features.profile.domain.entities.ProfileBoard
import com.ale.stylepin.features.profile.domain.entities.ProfilePin
import com.ale.stylepin.features.profile.domain.entities.ProfileSavedPin
import com.ale.stylepin.features.profile.domain.repositories.ProfileRepository
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val api: ProfileApi
) : ProfileRepository {

    override suspend fun getMyProfile(): Profile {
        val user = api.getMyProfile()
        val stats = try {
            api.getMyStats()
        } catch (e: Exception) {
            Log.e("ProfileRepository", "El backend falló al traer las stats: ${e.message}")
            UserStatsDto(totalPins = 0, totalFollowers = 0, totalFollowing = 0)
        }
        return mapToDomain(user, stats)
    }

    override suspend fun updateProfile(fullName: String, bio: String, gender: String): Result<Unit> {
        return try {
            val request = UpdateProfileRequest(
                fullName = fullName.trim(),
                bio = bio.trim(),
                gender = gender,
                avatarUrl = null,
                preferredStyles = null
            )
            api.updateProfile(request)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserPins(userId: String): Result<List<ProfilePin>> {
        return try {
            val response = api.getUserPins(userId)
            Result.success(response.pins.map { it.toDomain() })
        } catch (e: Exception) { Result.failure(e) }
    }

    override suspend fun getUserBoards(userId: String): Result<List<ProfileBoard>> {
        return try {
            val response = api.getUserBoards(userId)
            Result.success(response.boards.map { it.toDomain() })
        } catch (e: Exception) { Result.failure(e) }
    }

    override suspend fun getUserSavedPins(userId: String): Result<List<ProfileSavedPin>> {
        return try {
            val response = api.getUserLikes(userId)
            Result.success(response.likes.map { it.toDomain() })
        } catch (e: Exception) { Result.failure(e) }
    }
}