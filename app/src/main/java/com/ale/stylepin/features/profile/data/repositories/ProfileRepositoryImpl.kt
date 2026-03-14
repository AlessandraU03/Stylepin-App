package com.ale.stylepin.features.profile.data.repositories

import android.util.Log
import com.ale.stylepin.features.profile.data.datasources.remote.api.ProfileApi
import com.ale.stylepin.features.profile.data.datasources.remote.mapper.mapToDomain
import com.ale.stylepin.features.profile.data.datasources.remote.model.UpdateProfileRequest
import com.ale.stylepin.features.profile.data.datasources.remote.model.UserStatsDto
import com.ale.stylepin.features.profile.domain.entities.Profile
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
                gender = gender, // Lo enviamos a la API
                avatarUrl = null,
                preferredStyles = null
            )
            api.updateProfile(request)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}