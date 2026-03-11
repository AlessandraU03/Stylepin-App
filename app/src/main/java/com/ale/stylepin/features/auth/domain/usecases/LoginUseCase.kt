package com.ale.stylepin.features.auth.domain.usecases

import com.ale.stylepin.features.auth.domain.repositories.AuthRepository
import com.ale.stylepin.features.auth.domain.entities.UserToken
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend fun execute(identity: String, pass: String): UserToken {
        return repository.login(identity, pass)
    }
}