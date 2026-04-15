package com.ale.stylepin.features.pins.presentation.screens

import com.ale.stylepin.features.boards.domain.entities.Board
import com.ale.stylepin.features.pins.domain.entities.Pin
import com.ale.stylepin.features.pins.domain.entities.Comment

data class PinsUiState(
    val currentUserId: String? = null,
    val isLoading: Boolean = false,
    val pins: List<Pin> = emptyList(),
    val filteredPins: List<Pin> = emptyList(),
    val error: String? = null,

    // -- Detalle del Pin --
    val pinDetail: Pin? = null,
    val isLoadingDetail: Boolean = false,
    val authorIsFollowed: Boolean = false,
    val comments: List<Comment> = emptyList(),
    val commentInput: String = "",

    // -- Guardar en Tablero --
    val myBoards: List<Board> = emptyList(),
    val isSaveSheetVisible: Boolean = false,

    // -- Formulario --
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