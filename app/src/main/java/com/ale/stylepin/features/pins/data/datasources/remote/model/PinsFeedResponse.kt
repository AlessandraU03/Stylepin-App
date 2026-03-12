package com.ale.stylepin.features.pins.data.datasources.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class PinsFeedResponse(
    val pins: List<PinResponse>,
    val limit: Int,
    val offset: Int,
    val has_more: Boolean
)