package com.ale.stylepin.features.profile.domain.usecases

import com.ale.stylepin.features.profile.domain.repositories.ProfileRepository
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend fun execute(fullName: String, bio: String, gender: String): Result<Unit> {
        return repository.updateProfile(fullName, bio, gender)
    }
}