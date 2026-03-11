package com.ale.stylepin.features.profile.data.datasources.remote.api

import com.ale.stylepin.features.profile.data.datasources.remote.model.UserMeDto
import com.ale.stylepin.features.profile.data.datasources.remote.model.UserStatsDto
import retrofit2.http.GET

interface ProfileApi {
    @GET("api/v1/users/me")
    suspend fun getMyProfile(): UserMeDto

    @GET("api/v1/users/me/stats")
    suspend fun getMyStats(): UserStatsDto
}