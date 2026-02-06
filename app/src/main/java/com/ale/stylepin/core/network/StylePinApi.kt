package com.ale.stylepin.core.network

import com.ale.stylepin.features.auth.data.datasources.remote.model.LoginRequest
import com.ale.stylepin.features.auth.data.datasources.remote.model.RegisterRequest
import com.ale.stylepin.features.auth.data.datasources.remote.model.AuthResponse
import com.ale.stylepin.features.pins.data.datasources.remote.model.AddPinRequest
import com.ale.stylepin.features.pins.data.datasources.remote.model.PinResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface StylePinApi {
    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("api/v1/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    // AGREGA ESTO: Debe coincidir con el nombre que usas en el repositorio
    @GET("api/v1/pins")
    suspend fun getPins(): List<PinResponse>

    // ... dentro de interface StylePinApi
    @POST("api/v1/pins")
    suspend fun addPin(@Body request: AddPinRequest): PinResponse

    @DELETE("api/v1/pins/{pinId}")
    suspend fun deletePin(
        @Path("pinId") pinId: String
    ): Response<Unit> // Usamos Unit porque usualmente el delete no devuelve contenido, solo código 204 o 200
}