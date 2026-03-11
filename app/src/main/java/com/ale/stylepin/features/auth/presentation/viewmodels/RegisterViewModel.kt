package com.ale.stylepin.features.auth.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ale.stylepin.features.auth.domain.usecases.RegisterUseCase
import com.ale.stylepin.features.auth.presentation.screens.AuthUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    fun onFullNameChanged(value: String) = _uiState.update { it.copy(fullName = value) }
    fun onUsernameChanged(value: String) = _uiState.update { it.copy(username = value) }
    fun onEmailChanged(value: String) = _uiState.update { it.copy(email = value) }
    fun onPasswordChanged(value: String) = _uiState.update { it.copy(password = value) }
    fun onGenderChanged(value: String) = _uiState.update { it.copy(gender = value) }

    fun register() {
        val state = _uiState.value

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, isLoginSuccess = false) }

            try {
                val result = registerUseCase.execute(
                    state.username, state.email, state.password, state.fullName, state.gender
                )
                _uiState.update {
                    it.copy(isLoading = false, token = result.token, isLoginSuccess = true)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "Error desconocido", isLoginSuccess = false)
                }
            }
        }
    }

    fun consumeError() {
        _uiState.update { it.copy(error = null) }
    }
}