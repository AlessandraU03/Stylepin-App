package com.ale.stylepin.features.pins.data.datasources.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class UpdatePinRequest(
    val pinId: String,
    val title: String,
    val imageUrl: String?,
    val category: String,
    val season: String,
    val description: String?,
    val isPrivate: Boolean
)
