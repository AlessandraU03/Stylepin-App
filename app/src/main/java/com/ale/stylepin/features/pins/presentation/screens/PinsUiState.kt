// com/ale/stylepin/features/pins/presentation/screens/PinsUiState.kt
package com.ale.stylepin.features.pins.presentation.screens

import com.ale.stylepin.features.boards.domain.entities.Board
import com.ale.stylepin.features.pins.data.datasources.remote.model.CommentDto
import com.ale.stylepin.features.pins.domain.entities.Pin

data class PinsUiState(
    val currentUserId: String? = null,
    val isLoading: Boolean = false,
    val pins: List<Pin> = emptyList(),
    val filteredPins: List<Pin> = emptyList(),
    val error: String? = null,

    val pinDetail: Pin? = null,
    val isLoadingDetail: Boolean = false,

    // COMENTARIOS
    val comments: List<CommentDto> = emptyList(),
    val newCommentText: String = "",

    // NUEVO: GUARDADOS Y TABLEROS
    val userBoards: List<Board> = emptyList(),
    val isSavingToBoard: Boolean = false,

    // Formulario de creación
    val title: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val selectedCategory: String = "outfit_completo",
    val selectedSeason: String = "todo_el_ano",
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