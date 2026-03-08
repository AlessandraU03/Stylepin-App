package com.ale.stylepin.features.pins.data.datasources.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class UpdatePinRequest(
    val title: String,
    val description: String? = null,
    val category: String,
    val season: String,
    val styles: List<String> = emptyList(),
    val occasions: List<String> = emptyList(),
    val is_private: Boolean = false
)