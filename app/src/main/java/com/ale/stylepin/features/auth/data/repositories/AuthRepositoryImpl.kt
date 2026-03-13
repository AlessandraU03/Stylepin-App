// com/ale/stylepin/features/auth/data/repositories/AuthRepositoryImpl.kt
package com.ale.stylepin.features.auth.data.repositories

import android.content.SharedPreferences
import com.ale.stylepin.features.auth.data.datasources.remote.api.AuthApi
import com.ale.stylepin.features.auth.data.datasources.remote.mapper.toDomain
import com.ale.stylepin.features.auth.data.datasources.remote.model.LoginRequest
import com.ale.stylepin.features.auth.data.datasources.remote.model.RegisterRequest
import com.ale.stylepin.features.auth.domain.entities.UserToken
import com.ale.stylepin.features.auth.domain.repositories.AuthRepository
import org.json.JSONObject
import retrofit2.HttpException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi, // Usamos AuthApi, no la global
    private val prefs: SharedPreferences
) : AuthRepository {

    override suspend fun login(identity: String, pass: String): UserToken {
        try {
            val response = api.login(LoginRequest(identity, pass))
            prefs.edit().putString("auth_token", response.token).apply()

            // Usamos tu mapper toDomain() en lugar de instanciarlo a mano
            return response.toDomain()
        } catch (e: HttpException) {
            throw Exception(parseErrorMessage(e))
        }
    }

    override suspend fun register(
        username: String, email: String, pass: String, fullName: String, gender: String
    ): UserToken {
        try {
            val request = RegisterRequest(
                username = username.trim(), email = email.trim(), password = pass,
                fullName = fullName.trim(), gender = gender.lowercase().trim(), preferredStyles = emptyList()
            )
            val response = api.register(request)
            prefs.edit().putString("auth_token", response.token).apply()

            return response.toDomain() // Usamos tu mapper
        } catch (e: HttpException) {
            throw Exception(parseErrorMessage(e))
        }
    }

    override fun getStoredToken(): String? {
        return prefs.getString("auth_token", null)
    }

    override fun hasStoredToken(): Boolean {
        return !prefs.getString("auth_token", null).isNullOrEmpty()
    }

    override fun clearToken() {
        prefs.edit().remove("auth_token").apply()
    }

    private fun parseErrorMessage(exception: HttpException): String {
        val code = exception.code()
        if (code == 401) return "Usuario o contraseña incorrectos."
        if (code == 409 || code == 422) return "Verifica tus datos. Es posible que el correo o usuario ya estén registrados."

        return try {
            val errorBody = exception.response()?.errorBody()?.string()
            if (errorBody != null) {
                val json = JSONObject(errorBody)
                when {
                    json.has("details") -> {
                        val details = json.getJSONArray("details")
                        if (details.length() > 0) details.getJSONObject(0).getString("message")
                        else json.optString("message", "Error desconocido")
                    }
                    json.has("message") -> json.getString("message")
                    else -> "Error en el servidor ($code)"
                }
            } else {
                "Error en el servidor ($code)"
            }
        } catch (e: Exception) {
            "No se pudo conectar con el servidor."
        }
    }
}
