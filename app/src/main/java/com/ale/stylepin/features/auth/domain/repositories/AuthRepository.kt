package com.ale.stylepin.features.auth.domain.repositories

import com.ale.stylepin.features.auth.domain.entities.UserToken

interface AuthRepository {
    suspend fun login(identity: String, pass: String): UserToken

    suspend fun register(
        username: String,
        email: String,
        pass: String,
        fullName: String,
        gender: String
    ): UserToken
}