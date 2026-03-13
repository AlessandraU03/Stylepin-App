package com.ale.stylepin.features.community.data.datasources.remote.api

import com.ale.stylepin.features.community.data.datasources.remote.model.FollowUserRequest
import com.ale.stylepin.features.community.data.datasources.remote.model.FollowersListResponse
import com.ale.stylepin.features.community.data.datasources.remote.model.FollowingListResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CommunityApi {
    @GET("api/v1/follows/{user_id}/followers")
    suspend fun getFollowers(
        @Path("user_id") userId: String,
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): FollowersListResponse

    @GET("api/v1/follows/{user_id}/following")
    suspend fun getFollowing(
        @Path("user_id") userId: String,
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): FollowingListResponse

    @POST("api/v1/follows")
    suspend fun followUser(@Body request: FollowUserRequest): Response<Unit>

    @DELETE("api/v1/follows/{target_user_id}")
    suspend fun unfollowUser(@Path("target_user_id") targetUserId: String): Response<Unit>
}