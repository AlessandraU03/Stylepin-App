package com.ale.stylepin.features.auth.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ale.stylepin.features.auth.domain.usecases.LoginUseCase
import com.ale.stylepin.features.auth.presentation.screens.AuthUiState
import kotlinx.coroutines.launch

class LoginViewModel(private val loginUseCase: LoginUseCase) : ViewModel() {

    // Se inicializa con los valores por defecto de la Data Class
    var uiState by mutableStateOf(AuthUiState())
        private set

    fun login(identity: String, pass: String) {
        viewModelScope.launch {
            // 1. Iniciamos carga: isLoading = true
            uiState = uiState.copy(isLoading = true, error = null, isLoginSuccess = false)

            try {
                val result = loginUseCase.execute(identity, pass)
                // 2. Éxito: isLoginSuccess = true
                uiState = uiState.copy(
                    isLoading = false,
                    token = result.token,
                    isLoginSuccess = true
                )
            } catch (e: Exception) {
                // 3. Error: pasamos el mensaje al String error
                uiState = uiState.copy(
                    isLoading = false,
                    error = e.message ?: "Error desconocido",
                    isLoginSuccess = false
                )
            }
        }
    }
}