package com.ale.stylepin.features.auth.data.repositories

import android.content.SharedPreferences
import com.ale.stylepin.core.network.StylePinApi
import com.ale.stylepin.features.auth.data.datasources.remote.model.LoginRequest
import com.ale.stylepin.features.auth.domain.repositories.AuthRepository
import com.ale.stylepin.features.auth.domain.entities.UserToken

class AuthRepositoryImpl(
    private val api: StylePinApi,
    private val prefs: SharedPreferences // Inyectamos las preferencias
) : AuthRepository {
    override suspend fun login(identity: String, pass: String): UserToken {
        val response = api.login(LoginRequest(identity, pass))

        // ¡PASO CRUCIAL!: Guardar el token en el disco del celular
        prefs.edit().putString("auth_token", response.token).apply()

        return UserToken(
            token = response.token,
            username = response.user.username
        )
    }
}