package com.ale.stylepin.features.auth.presentation.screens

/**
 * Estado siguiendo el patrón de tu ejemplo de RickAndMorty.
 */
data class AuthUiState(
    val isLoading: Boolean = false,
    val token: String? = null,
    val error: String? = null,
    val isLoginSuccess: Boolean = false
)