package com.ale.stylepin.features.profile.domain.usecases

import com.ale.stylepin.features.profile.domain.repositories.ProfileRepository
import javax.inject.Inject

class DeleteAccountUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend fun execute(): Result<Unit> = repository.deleteMyAccount()
}