package com.ale.stylepin.features.pins.presentation.screens

import com.ale.stylepin.features.pins.domain.entities.Pin

data class PinsUiState(
    val isLoading: Boolean = false,
    val pins: List<Pin> = emptyList(),
    val filteredPins: List<Pin> = emptyList(),
    val error: String? = null,
    val title: String = "",
    val imageUrl: String = "",
    val selectedCategory: String = "outfit_completo",
    val selectedSeason: String = "todo_el_ano"
)