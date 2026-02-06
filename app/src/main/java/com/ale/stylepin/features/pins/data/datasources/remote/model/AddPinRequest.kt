package com.ale.stylepin.features.pins.data.datasources.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class AddPinRequest(
    val title: String,
    val image_url: String,
    val description: String?,
    val category: String,
    val season: String,
    val occasions: List<String> = emptyList(),
    val is_private: Boolean = false
)