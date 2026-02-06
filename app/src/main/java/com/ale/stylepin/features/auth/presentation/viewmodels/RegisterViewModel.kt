package com.ale.stylepin.features.auth.presentation.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ale.stylepin.features.auth.domain.usecases.RegisterUseCase
import com.ale.stylepin.features.auth.presentation.screens.AuthUiState
import kotlinx.coroutines.launch

class RegisterViewModel(private val registerUseCase: RegisterUseCase) : ViewModel() {

    var uiState by mutableStateOf(AuthUiState())
        private set

    fun register(username: String, email: String, pass: String, fullName: String, gender: String) {
        Log.d(
            "RegisterViewModel",
            "register() called with: username=$username, email=$email, fullName=$fullName, gender=$gender"
        )

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null, isLoginSuccess = false)
            Log.d("RegisterViewModel", "Estado cambiado a loading")

            try {
                val result = registerUseCase.execute(username, email, pass, fullName, gender)
                Log.d("RegisterViewModel", "Registro exitoso: token=${result.token}")

                uiState = uiState.copy(
                    isLoading = false,
                    token = result.token,
                    isLoginSuccess = true
                )
            } catch (e: Exception) {
                Log.e("RegisterViewModel", "Error en registro", e)

                uiState = uiState.copy(
                    isLoading = false,
                    error = e.message ?: "Error en el registro",
                    isLoginSuccess = false
                )
            }
        }
    }
}