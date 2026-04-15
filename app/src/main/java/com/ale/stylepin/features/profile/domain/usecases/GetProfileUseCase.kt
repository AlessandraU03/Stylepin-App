package com.ale.stylepin.features.profile.domain.usecases

import com.ale.stylepin.features.profile.domain.entities.Profile
import com.ale.stylepin.features.profile.domain.repositories.ProfileRepository
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend fun execute(userId: String): Profile {
        return repository.getProfileById(userId).getOrThrow()
    }
}