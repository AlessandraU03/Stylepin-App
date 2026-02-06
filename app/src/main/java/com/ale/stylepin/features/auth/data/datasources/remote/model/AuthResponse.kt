package com.ale.stylepin.features.auth.data.datasources.remote.model

data class LoginRequest(
    val identity: String, // Email o Username
    val password: String
)

data class AuthResponse(
    val token: String,
    val token_type: String,
    val user: UserDto
)

data class UserDto(
    val id: String,
    val username: String
)