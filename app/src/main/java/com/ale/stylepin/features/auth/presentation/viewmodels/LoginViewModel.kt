package com.ale.stylepin.features.auth.presentation.viewmodels

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ale.stylepin.core.hardware.domain.BiometricManager
import com.ale.stylepin.core.hardware.domain.FlashManager
import com.ale.stylepin.features.auth.domain.usecases.LoginUseCase
import com.ale.stylepin.features.auth.presentation.screens.AuthUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val biometricManager: BiometricManager,
    private val flashManager: FlashManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    fun onIdentityChanged(value: String) = _uiState.update { it.copy(username = value, error = null) }
    fun onPasswordChanged(value: String) = _uiState.update { it.copy(password = value, error = null) }

    fun login() {
        val currentState = _uiState.value

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, isLoginSuccess = false) }

            try {
                val result = loginUseCase.execute(currentState.username, currentState.password)
                _uiState.update {
                    it.copy(isLoading = false, token = result.token, isLoginSuccess = true)
                }
                if (flashManager.hasFlash()) {
                    flashManager.blink()
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "Credenciales inválidas", isLoginSuccess = false)
                }
            }
        }
    }

    fun canUseBiometrics(): Boolean = biometricManager.canAuthenticate()

    fun loginWithBiometrics(activity: FragmentActivity) {
        // Validación: Solo permitir biometría si hay una sesión previa
        if (!loginUseCase.hasStoredSession()) {
            _uiState.update { 
                it.copy(error = "Debes iniciar sesión con contraseña al menos una vez para activar el acceso con huella.") 
            }
            return
        }

        biometricManager.authenticate(
            activity = activity,
            title = "Inicio de Sesión Biométrico",
            subtitle = "Usa tu huella o reconocimiento facial para entrar",
            onSuccess = {
                _uiState.update { it.copy(isLoginSuccess = true) }
                viewModelScope.launch {
                    if (flashManager.hasFlash()) flashManager.blink()
                }
            },
            onError = { code, message ->
                _uiState.update { it.copy(error = "Error biométrico: $message") }
            },
            onFailed = {
                _uiState.update { it.copy(error = "Autenticación fallida") }
            }
        )
    }
}
