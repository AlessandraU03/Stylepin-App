package com.ale.stylepin.features.pins.data.datasources.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class PinsTrendingResponse(
    val pins: List<PinResponse>,
    val hours: Int
)