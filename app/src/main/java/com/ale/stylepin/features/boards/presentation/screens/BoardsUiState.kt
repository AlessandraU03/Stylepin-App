package com.ale.stylepin.features.boards.presentation.screens

import com.ale.stylepin.features.boards.domain.entities.Board
import com.ale.stylepin.features.boards.domain.entities.BoardCollaborator
import com.ale.stylepin.features.boards.domain.entities.BoardPin

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

    // ── Formulario ─────────────────────────────────────────
    val name: String = "",
    val description: String = "",
    val isPrivate: Boolean = false,
    val isCollaborative: Boolean = false,

    // ── Agregar pin al tablero ─────────────────────────────
    val addPinNotes: String = ""
)