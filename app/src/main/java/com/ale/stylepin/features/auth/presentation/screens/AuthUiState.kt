package com.ale.stylepin.features.auth.presentation.screens

data class AuthUiState(
    // Datos del formulario (Entradas del usuario)
    val fullName: String = "",
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val gender: String = "male",

    // Estados de la lógica de negocio (Como en Rick y Morty)
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoginSuccess: Boolean = false,
    val token: String? = null
)