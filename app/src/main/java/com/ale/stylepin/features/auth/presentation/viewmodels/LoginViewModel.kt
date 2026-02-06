package com.ale.stylepin.features.auth.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ale.stylepin.features.auth.domain.usecases.LoginUseCase
import com.ale.stylepin.features.auth.presentation.screens.AuthUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(private val loginUseCase: LoginUseCase) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    fun login(identity: String, pass: String) {
        viewModelScope.launch {
            // 1. Actualización atómica para iniciar carga
            _uiState.update {
                it.copy(isLoading = true, error = null, isLoginSuccess = false)
            }

            try {
                val result = loginUseCase.execute(identity, pass)


                _uiState.update {
                    it.copy(
                        isLoading = false,
                        token = result.token,
                        isLoginSuccess = true
                    )
                }
            } catch (e: Exception) {

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Error desconocido",
                        isLoginSuccess = false
                    )
                }
            }
        }
    }

    // Función extra útil: Resetear el error al volver a escribir
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}