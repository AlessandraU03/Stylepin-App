package com.ale.stylepin.features.auth.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ale.stylepin.features.auth.domain.usecases.RegisterUseCase
import com.ale.stylepin.features.auth.presentation.screens.AuthUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(private val registerUseCase: RegisterUseCase) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    fun register(username: String, email: String, pass: String, fullName: String, gender: String) {
        Log.d(
            "RegisterViewModel",
            "register() called with: username=$username, email=$email, fullName=$fullName, gender=$gender"
        )

        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, error = null, isLoginSuccess = false)
            }
            Log.d("RegisterViewModel", "Estado cambiado a loading")

            try {
                val result = registerUseCase.execute(username, email, pass, fullName, gender)
                Log.d("RegisterViewModel", "Registro exitoso: token=${result.token}")

                // Éxito: Actualizamos con el token y marcamos éxito
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        token = result.token,
                        isLoginSuccess = true
                    )
                }
            } catch (e: Exception) {
                Log.e("RegisterViewModel", "Error en registro", e)

                // Error: Capturamos la excepción y actualizamos el estado
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Error en el registro",
                        isLoginSuccess = false
                    )
                }
            }
        }
    }

   fun consumeError() {
        _uiState.update { it.copy(error = null) }
    }
}