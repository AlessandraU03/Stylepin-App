package com.ale.stylepin.features.pins.data.datasources.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class AddPinRequest(
    val title: String,
    val imageUrl: String,
    val category: String,
    val season: String,
    val description: String?,
    val isPrivate: Boolean,
    val styles: List<String>,
    val occasions: List<String>,
    val brands: List<String>,
    val priceRange: String,
    val whereToBuy: String?,
    val purchaseLink: String?,
    val colors: List<String>,
    val tags: List<String>
)
