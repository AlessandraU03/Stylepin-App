package com.ale.stylepin.features.profile.domain.repositories

import com.ale.stylepin.features.profile.domain.entities.Profile

interface ProfileRepository {
    suspend fun getMyProfile(): Profile
    // Agregamos gender a la firma
    suspend fun updateProfile(fullName: String, bio: String, gender: String): Result<Unit>
}