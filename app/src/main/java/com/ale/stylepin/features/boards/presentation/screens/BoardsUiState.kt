package com.ale.stylepin.features.boards.presentation.screens

import com.ale.stylepin.features.boards.domain.entities.Board
import com.ale.stylepin.features.boards.domain.entities.BoardCollaborator
import com.ale.stylepin.features.boards.domain.entities.BoardPin
import com.ale.stylepin.features.pins.domain.entities.Pin

data class BoardsUiState(
    // ── Lista ──────────────────────────────────────────────
    val isLoading: Boolean = false,
    val boards: List<Board> = emptyList(),
    val error: String? = null,

    // ── Detalle ────────────────────────────────────────────
    val boardDetail: Board? = null,
    val isLoadingDetail: Boolean = false,
    val boardPins: List<BoardPin> = emptyList(),
    val collaborators: List<BoardCollaborator> = emptyList(),
    val pinsDetails: Map<String, Pin> = emptyMap(),

    // ── Formulario ─────────────────────────────────────────
    val name: String = "",
    val description: String = "",
    val isPrivate: Boolean = false,
    val isCollaborative: Boolean = false,

    // ── Agregar pin al tablero ─────────────────────────────
    val addPinNotes: String = "",
    val userPins: List<Pin> = emptyList(),
    val isLoadingUserPins: Boolean = false
)
