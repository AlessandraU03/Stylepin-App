package com.ale.stylepin.features.pins.data.datasources.remote.api

import com.ale.stylepin.features.pins.data.datasources.remote.model.PinResponse
import com.ale.stylepin.features.pins.data.datasources.remote.model.PinsListResponse
import com.ale.stylepin.features.pins.data.datasources.remote.model.UpdatePinRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface PinApi {

    @GET("api/v1/pins")
    suspend fun getPins(
        @QueryMap filters: Map<String, String>
    ): Response<PinsListResponse>

    @GET("api/v1/pins/{pin_id}")
    suspend fun getPinById(
        @Path("pin_id") pinId: String
    ): Response<PinResponse>

    @Multipart
    @POST("api/v1/pins")
    suspend fun createPin(
        @Part image: MultipartBody.Part,
        @PartMap fields: Map<String, @JvmSuppressWildcards RequestBody>
    ): Response<PinResponse>

    @PUT("api/v1/pins/{pin_id}")
    suspend fun updatePin(
        @Path("pin_id") pinId: String,
        @Body request: UpdatePinRequest
    ): Response<PinResponse>

    @DELETE("api/v1/pins/{pin_id}")
    suspend fun deletePin(
        @Path("pin_id") pinId: String
    ): Response<Unit>
}