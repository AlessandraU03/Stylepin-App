package com.ale.stylepin.features.pins.presentation.screens

import com.ale.stylepin.features.pins.domain.entities.Pin

data class PinsUiState(
    val pins: List<Pin> = emptyList(),          // Todos los que vienen de EC2
    val filteredPins: List<Pin> = emptyList(),  // Los que se muestran en pantalla
    val selectedSeason: String = "All",         // Temporada seleccionada
    val isLoading: Boolean = false,
    val error: String? = null
)