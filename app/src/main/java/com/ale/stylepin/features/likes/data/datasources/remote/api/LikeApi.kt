package com.ale.stylepin.features.likes.data.datasources.remote.api

import com.ale.stylepin.features.likes.data.datasources.remote.model.LikeRequest
import com.ale.stylepin.features.likes.data.datasources.remote.model.LikeResponse
import com.ale.stylepin.features.likes.data.datasources.remote.model.PinLikesResponse
import retrofit2.Response
import retrofit2.http.*

interface LikeApi {
    @POST("api/v1/likes")
    suspend fun likePin(@Body request: LikeRequest): Response<LikeResponse>

    @DELETE("api/v1/likes/{pin_id}")
    suspend fun unlikePin(@Path("pin_id") pinId: String): Response<LikeResponse>

    @GET("api/v1/likes/pin/{pin_id}")
    suspend fun getPinLikes(
        @Path("pin_id") pinId: String,
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): Response<PinLikesResponse>

    @GET("api/v1/likes/user/{user_id}")
    suspend fun getUserLikedPins(
        @Path("user_id") userId: String,
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): Response<PinLikesResponse>

    @GET("api/v1/likes/status/{pin_id}")
    suspend fun getLikeStatus(@Path("pin_id") pinId: String): Response<LikeResponse>
}
