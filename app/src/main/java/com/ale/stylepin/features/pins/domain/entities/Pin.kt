package com.ale.stylepin.features.pins.domain.entities

data class Pin(
    val id: String,
    val username: String,
    val imageUrl: String,
    val title: String,
    val category: String,
    val occasions: List<String>,
    val likesCount: Int,
    val season: String // <--- AGREGA ESTA LÍNEA
)