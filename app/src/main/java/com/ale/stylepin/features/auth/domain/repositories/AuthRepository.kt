package com.ale.stylepin.features.auth.domain.repositories

import com.ale.stylepin.features.auth.domain.entities.UserToken

interface AuthRepository {
    // Asegúrate de que aquí diga UserToken
    suspend fun login(identity: String, pass: String): UserToken
}