package com.ale.stylepin.features.profile.data.datasources.remote.model

import com.google.gson.annotations.SerializedName

data class UploadResponse(
    val url: String,
    @SerializedName("public_id") val publicId: String
)