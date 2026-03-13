package com.ale.stylepin.features.profile.domain.repositories

import com.ale.stylepin.features.profile.domain.entities.*

interface ProfileRepository {
    suspend fun getMyProfile(): Profile
    suspend fun updateProfile(fullName: String, bio: String, gender: String, avatarUrl: String?): Result<Unit>
    suspend fun uploadAvatar(uriString: String): Result<String>
    suspend fun getUserProfile(userId: String): Result<PublicProfile>
    suspend fun getUserPins(userId: String): Result<List<ProfilePin>>
    suspend fun getUserBoards(userId: String): Result<List<ProfileBoard>>
    suspend fun getUserSavedPins(userId: String): Result<List<ProfileSavedPin>>
}