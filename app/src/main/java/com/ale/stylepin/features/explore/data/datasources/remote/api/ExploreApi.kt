package com.ale.stylepin.features.explore.data.datasources.remote.api

import com.ale.stylepin.features.explore.data.datasources.remote.model.PinSearchResponse
import com.ale.stylepin.features.explore.data.datasources.remote.model.UserSearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ExploreApi {
    @GET("api/v1/users/search")
    suspend fun searchUsers(
        @Query("q") query: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): Response<UserSearchResponse>

    @GET("api/v1/pins/search")
    suspend fun searchPins(
        @Query("q") query: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): Response<PinSearchResponse> // Usa el nuevo response recortado
}