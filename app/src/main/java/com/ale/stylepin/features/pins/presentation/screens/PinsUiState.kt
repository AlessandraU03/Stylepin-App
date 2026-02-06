package com.ale.stylepin.features.pins.presentation.screens

import com.ale.stylepin.features.pins.domain.entities.Pin

data class PinsUiState(
    val isLoading: Boolean = false,
    val pins: List<Pin> = emptyList(),
    val filteredPins: List<Pin> = emptyList(),
    val selectedSeason: String = "todo_el_ano", // <--- AGREGA ESTO
    val error: String? = null
)