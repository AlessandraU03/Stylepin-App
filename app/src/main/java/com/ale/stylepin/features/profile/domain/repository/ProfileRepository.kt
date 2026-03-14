package com.ale.stylepin.features.profile.domain.repositories

import com.ale.stylepin.features.profile.domain.entities.Profile

interface ProfileRepository {
    suspend fun getMyProfile(): Profile
    suspend fun updateProfile(fullName: String, bio: String, gender: String, avatarUrl: String?): Result<Unit>
    suspend fun deleteMyAccount(): Result<Unit>
    suspend fun uploadAvatar(uriString: String): Result<String>
}