package com.ale.stylepin.features.pins.domain.entities

data class PinCreate(
    val title: String,
    val imageUrl: String,
    val category: String,
    val season: String,
    val description: String? = null,
    val isPrivate: Boolean = false,
    val styles: List<String> = emptyList(),
    val occasions: List<String> = emptyList(),
    val brands: List<String> = emptyList(),
    val priceRange: String = "bajo_500",
    val whereToBuy: String? = null,
    val purchaseLink: String? = null,
    val colors: List<String> = emptyList(),
    val tags: List<String> = emptyList()
)
