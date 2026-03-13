package com.ale.stylepin.features.profile.domain.repositories

import com.ale.stylepin.features.profile.domain.entities.Profile
import com.ale.stylepin.features.profile.domain.entities.ProfileBoard
import com.ale.stylepin.features.profile.domain.entities.ProfilePin
import com.ale.stylepin.features.profile.domain.entities.ProfileSavedPin

interface ProfileRepository {
    suspend fun getMyProfile(): Profile
    suspend fun updateProfile(fullName: String, bio: String, gender: String): Result<Unit>

    // NUEVOS
    suspend fun getUserPins(userId: String): Result<List<ProfilePin>>
    suspend fun getUserBoards(userId: String): Result<List<ProfileBoard>>
    suspend fun getUserSavedPins(userId: String): Result<List<ProfileSavedPin>>
}