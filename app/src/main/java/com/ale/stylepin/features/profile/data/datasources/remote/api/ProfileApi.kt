package com.ale.stylepin.features.profile.data.datasources.remote.api

import com.ale.stylepin.features.profile.data.datasources.remote.model.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface ProfileApi {
    @GET("api/v1/users/me")
    suspend fun getMyProfile(): UserMeDto

    @GET("api/v1/users/me/stats")
    suspend fun getMyStats(): UserStatsDto

    @PUT("api/v1/users/me")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): UserMeDto

    // --- NUEVOS ENDPOINTS PARA EL GRID ---
    @GET("api/v1/pins/user/{user_id}")
    suspend fun getUserPins(@Path("user_id") userId: String): ProfilePinListResponse

    @GET("api/v1/boards/user/{user_id}")
    suspend fun getUserBoards(@Path("user_id") userId: String): ProfileBoardListResponse

    @GET("api/v1/likes/user/{user_id}")
    suspend fun getUserLikes(@Path("user_id") userId: String): ProfileLikeListResponse
}