package com.ale.stylepin.features.community.domain.entities

import com.google.gson.annotations.SerializedName

data class Notification(
    @SerializedName("type") val type: String,
    @SerializedName("message") val message: String,
    @SerializedName("actor_id") val actorId: String? = null,
    @SerializedName("actor_username") val actorUsername: String? = null,
    @SerializedName("pin_id") val pinId: String? = null,
    @SerializedName("created_at") val createdAt: String? = null
)
