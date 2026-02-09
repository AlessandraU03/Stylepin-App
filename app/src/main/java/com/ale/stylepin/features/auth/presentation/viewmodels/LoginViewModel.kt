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

    // Manejo de eventos de entrada (Igual que en Register)
    fun onIdentityChanged(value: String) {
        _uiState.update { it.copy(username = value, error = null) }
    }

    fun onPasswordChanged(value: String) {
        _uiState.update { it.copy(password = value, error = null) }
    }

    fun login() {
        val currentState = _uiState.value

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, isLoginSuccess = false) }

            try {
                // Usamos los datos guardados en el estado
                val result = loginUseCase.execute(currentState.username, currentState.password)

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
                        error = e.message ?: "Credenciales inválidas",
                        isLoginSuccess = false
                    )
                }
            }
        }
    }
}