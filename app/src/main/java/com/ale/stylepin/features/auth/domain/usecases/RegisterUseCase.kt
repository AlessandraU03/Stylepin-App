package com.ale.stylepin.features.auth.domain.usecases

import com.ale.stylepin.features.auth.domain.repositories.AuthRepository
import com.ale.stylepin.features.auth.domain.entities.UserToken

class RegisterUseCase(private val repository: AuthRepository) {
    suspend fun execute(
        username: String,
        email: String,
        pass: String,
        fullName: String,
        gender: String
    ): UserToken {
        return repository.register(
            username = username,
            email = email,
            pass = pass,
            fullName = fullName,
            gender = gender
        )
    }
}