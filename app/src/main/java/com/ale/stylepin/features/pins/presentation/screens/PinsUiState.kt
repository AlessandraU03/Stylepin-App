package com.ale.stylepin.features.pins.presentation.screens

import com.ale.stylepin.features.pins.domain.entities.Pin

data class PinsUiState(
    // ── Lista de pins ──────────────────────────────────────
    val isLoading: Boolean = false,
    val pins: List<Pin> = emptyList(),
    val filteredPins: List<Pin> = emptyList(),
    val error: String? = null,

    // ── Detalle de un pin individual ───────────────────────
    val pinDetail: Pin? = null,
    val isLoadingDetail: Boolean = false,

    // ── Formulario de creación / edición ───────────────────
    val title: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val selectedCategory: String = "",
    val selectedSeason: String = "",
    val isPrivate: Boolean = false,
    val styles: List<String> = emptyList(),
    val occasions: List<String> = emptyList(),
    val brands: List<String> = emptyList(),
    val priceRange: String = "bajo_500",
    val whereToBuy: String = "",
    val purchaseLink: String = "",
    val colors: List<String> = emptyList(),
    val tags: List<String> = emptyList()
)