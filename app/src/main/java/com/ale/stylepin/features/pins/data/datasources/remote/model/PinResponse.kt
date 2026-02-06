package com.ale.stylepin.features.pins.data.datasources.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class PinResponse(
    val id: String,
    val user_username: String,
    val image_url: String,
    val title: String,
    // CAMBIO: Agregamos ? a lo que tu API diga que puede ser (string | null)
    val category: String? = null,
    val season: String? = null,
    val occasions: List<String>? = emptyList(),
    val likes_count: Int = 0
)