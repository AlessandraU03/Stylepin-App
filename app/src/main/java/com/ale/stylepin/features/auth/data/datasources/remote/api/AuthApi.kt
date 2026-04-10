// com/ale/stylepin/features/auth/data/datasources/remote/api/AuthApi.kt
package com.ale.stylepin.features.auth.data.datasources.remote.api

import com.ale.stylepin.features.auth.data.datasources.remote.model.AuthResponse
import com.ale.stylepin.features.auth.data.datasources.remote.model.LoginRequest
import com.ale.stylepin.features.auth.data.datasources.remote.model.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("api/v1/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

}