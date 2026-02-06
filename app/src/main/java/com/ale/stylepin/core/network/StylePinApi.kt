package com.ale.stylepin.core.network

import com.ale.stylepin.features.auth.data.datasources.remote.model.LoginRequest
import com.ale.stylepin.features.auth.data.datasources.remote.model.AuthResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface StylePinApi {
    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse
}