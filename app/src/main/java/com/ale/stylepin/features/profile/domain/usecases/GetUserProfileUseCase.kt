package com.ale.stylepin.features.profile.domain.usecases
import com.ale.stylepin.features.profile.domain.entities.PublicProfile
import com.ale.stylepin.features.profile.domain.repositories.ProfileRepository
import javax.inject.Inject
class GetUserProfileUseCase @Inject constructor(private val repository: ProfileRepository) {
    suspend fun execute(userId: String): Result<PublicProfile> = repository.getUserProfile(userId)
}