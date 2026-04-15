package com.ale.stylepin.features.profile.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ale.stylepin.features.auth.domain.repositories.AuthRepository
import com.ale.stylepin.features.auth.domain.usecases.LoginUseCase
import com.ale.stylepin.features.profile.domain.usecases.DeleteAccountUseCase
import com.ale.stylepin.features.profile.domain.usecases.GetMyProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val currentUsername: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val accountDeleted: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val getMyProfileUseCase: GetMyProfileUseCase,
    private val loginUseCase: LoginUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val profile = getMyProfileUseCase.execute()
                _uiState.update { it.copy(currentUsername = profile.username) }
            } catch (e: Exception) { }
        }
    }

    fun logout() {
        authRepository.clearToken()
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun deleteAccount(password: String) {
        if (password.isBlank()) {
            _uiState.update { it.copy(error = "Ingresa tu contraseña por seguridad.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // Verificamos contraseña con login
                val identity = _uiState.value.currentUsername
                if (identity.isNotBlank()) loginUseCase.execute(identity, password)

                // Si es correcta, eliminamos
                val result = deleteAccountUseCase.execute()
                if (result.isSuccess) {
                    authRepository.clearToken()
                    _uiState.update { it.copy(isLoading = false, accountDeleted = true) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Error del servidor.") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Contraseña incorrecta.") }
            }
        }
    }
}