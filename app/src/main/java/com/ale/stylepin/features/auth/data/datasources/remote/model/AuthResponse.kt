package com.ale.stylepin.features.auth.data.datasources.remote.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val identity: String,
    val password: String
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    @SerializedName("full_name") val fullName: String,
    val gender: String,
    @SerializedName("preferred_styles") val preferredStyles: List<String> = emptyList()
)

data class AuthResponse(
    val token: String,
    @SerializedName("token_type") val tokenType: String,
    val user: UserDto
)

data class UserDto(
    val id: String,
    val username: String,
    @SerializedName("full_name") val fullName: String? = null
)