package com.ale.stylepin.features.auth.data.repositories

import com.ale.stylepin.core.network.StylePinApi
import com.ale.stylepin.features.auth.data.datasources.remote.model.LoginRequest
import com.ale.stylepin.features.auth.data.datasources.remote.model.RegisterRequest
import com.ale.stylepin.features.auth.domain.repositories.AuthRepository
import com.ale.stylepin.features.auth.domain.entities.UserToken
import org.json.JSONObject
import retrofit2.HttpException

class AuthRepositoryImpl(private val api: StylePinApi) : AuthRepository {

    override suspend fun login(identity: String, pass: String): UserToken {
        try {
            val response = api.login(LoginRequest(identity, pass))
            return UserToken(
                token = response.token,
                username = response.user.username
            )
        } catch (e: HttpException) {
            throw Exception(parseErrorMessage(e))
        }
    }

    override suspend fun register(
        username: String,
        email: String,
        pass: String,
        fullName: String,
        gender: String
    ): UserToken {
        try {
            val request = RegisterRequest(
                username = username.trim(),
                email = email.trim(),
                password = pass,
                fullName = fullName.trim(),
                gender = gender.lowercase().trim(),
                preferredStyles = emptyList()
            )

            val response = api.register(request)

            return UserToken(
                token = response.token,
                username = response.user.username
            )
        } catch (e: HttpException) {
            throw Exception(parseErrorMessage(e))
        }
    }

    private fun parseErrorMessage(exception: HttpException): String {
        return try {
            val errorBody = exception.response()?.errorBody()?.string()
            if (errorBody != null) {
                val json = JSONObject(errorBody)
                when {
                    json.has("details") -> {
                        val details = json.getJSONArray("details")
                        if (details.length() > 0) {
                            details.getJSONObject(0).getString("message")
                        } else {
                            json.optString("message", "Error desconocido")
                        }
                    }
                    json.has("message") -> json.getString("message")
                    else -> "Error: ${exception.code()}"
                }
            } else {
                "Error: ${exception.code()}"
            }
        } catch (e: Exception) {
            "Error en el servidor"
        }
    }
}