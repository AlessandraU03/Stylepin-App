package com.ale.stylepin.features.explore.domain.entities

import com.ale.stylepin.features.boards.domain.entities.Board

// Entidad para los usuarios buscados
data class UserSearchResult(
    val id: String,
    val username: String,
    val fullName: String,
    val avatarUrl: String,
    val isVerified: Boolean
)

// Entidad combinada para la vista de Trending Boards
data class TrendingBoard(
    val board: Board,
    val previewUrls: List<String> // Contendrá hasta 3 URLs de imágenes
)