package com.ale.stylepin.features.profile.data.datasources.remote.api

import com.ale.stylepin.features.profile.data.datasources.remote.model.UpdateProfileRequest
import com.ale.stylepin.features.profile.data.datasources.remote.model.UploadResponse
import com.ale.stylepin.features.profile.data.datasources.remote.model.UserMeDto
import com.ale.stylepin.features.profile.data.datasources.remote.model.UserProfileResponse // <-- Importa el nuevo envoltorio
import com.ale.stylepin.features.profile.data.datasources.remote.model.UserStatsDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ProfileApi {
    @GET("api/v1/users/me")
    suspend fun getMyProfile(): UserMeDto

    @GET("api/v1/users/me/stats")
    suspend fun getMyStats(): UserStatsDto

    @PUT("api/v1/users/me")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): UserMeDto

    @DELETE("api/v1/users/me")
    suspend fun deleteMyAccount(): Response<Unit>

    @Multipart
    @POST("api/v1/upload/avatar")
    suspend fun uploadAvatar(@Part file: MultipartBody.Part): Response<UploadResponse>

    // 👇 CAMBIO: Ahora espera recibir el UserProfileResponse
    @GET("api/v1/users/{id}")
    suspend fun getUserProfileById(@Path("id") userId: String): Response<UserProfileResponse>
}