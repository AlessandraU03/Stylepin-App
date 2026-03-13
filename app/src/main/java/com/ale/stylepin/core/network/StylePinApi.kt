package com.ale.stylepin.core.network

import com.ale.stylepin.features.pins.data.datasources.remote.model.AddPinRequest
import com.ale.stylepin.features.pins.data.datasources.remote.model.PinResponse
import com.ale.stylepin.features.pins.data.datasources.remote.model.UpdatePinRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface StylePinApi {
    @GET("api/v1/pins")
    suspend fun getPins(): List<PinResponse>

    @POST("api/v1/pins")
    suspend fun addPin(@Body request: AddPinRequest): PinResponse

    @DELETE("api/v1/pins/{pin_id}")
    suspend fun deletePin(@Path("pin_id") pinId: String): Response<Unit>

    @PATCH("api/v1/pins/{pin_id}")
    suspend fun updatePin(
        @Path("pin_id") pinId: String,
        @Body request: UpdatePinRequest
    ): PinResponse
}