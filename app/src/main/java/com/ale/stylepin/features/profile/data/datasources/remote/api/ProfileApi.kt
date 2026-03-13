package com.ale.stylepin.features.profile.data.datasources.remote.api

import com.ale.stylepin.features.profile.data.datasources.remote.model.*
import okhttp3.MultipartBody
import retrofit2.http.*

data class ImageUploadResponse(val url: String)

interface ProfileApi {
    @GET("api/v1/users/me")
    suspend fun getMyProfile(): UserMeDto

    @GET("api/v1/users/me/stats")
    suspend fun getMyStats(): UserStatsDto

    @PUT("api/v1/users/me")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): UserMeDto

    @GET("api/v1/pins/user/{user_id}")
    suspend fun getUserPins(@Path("user_id") userId: String): ProfilePinListResponse

    @GET("api/v1/boards/user/{user_id}")
    suspend fun getUserBoards(@Path("user_id") userId: String): ProfileBoardListResponse

    @GET("api/v1/likes/user/{user_id}")
    suspend fun getUserLikes(@Path("user_id") userId: String): ProfileLikeListResponse

    // --- NUEVOS ENDPOINTS ---
    @Multipart
    @POST("api/v1/upload/avatar")
    suspend fun uploadAvatar(@Part file: MultipartBody.Part): ImageUploadResponse

    @GET("api/v1/users/{user_id}")
    suspend fun getUserProfile(@Path("user_id") userId: String): UserProfileResponseDto
}