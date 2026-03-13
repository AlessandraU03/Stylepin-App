package com.ale.stylepin.features.profile.domain.usecases

import com.ale.stylepin.features.profile.domain.entities.ProfileBoard
import com.ale.stylepin.features.profile.domain.entities.ProfilePin
import com.ale.stylepin.features.profile.domain.entities.ProfileSavedPin
import com.ale.stylepin.features.profile.domain.repositories.ProfileRepository
import javax.inject.Inject

class GetProfileContentUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend fun getPins(userId: String): Result<List<ProfilePin>> = repository.getUserPins(userId)
    suspend fun getBoards(userId: String): Result<List<ProfileBoard>> = repository.getUserBoards(userId)
    suspend fun getSavedPins(userId: String): Result<List<ProfileSavedPin>> = repository.getUserSavedPins(userId)
}