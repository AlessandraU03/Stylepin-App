package com.ale.stylepin.features.profile.data.datasources.remote.model

import com.google.gson.annotations.SerializedName

data class UserStatsDto(
    @SerializedName("total_followers") val totalFollowers: Int,
    @SerializedName("total_following") val totalFollowing: Int,
    @SerializedName("total_pins") val totalPins: Int
)