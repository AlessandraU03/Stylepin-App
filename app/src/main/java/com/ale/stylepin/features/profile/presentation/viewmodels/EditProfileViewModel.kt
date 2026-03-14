package com.ale.stylepin.features.profile.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ale.stylepin.features.profile.domain.usecases.GetMyProfileUseCase
import com.ale.stylepin.features.profile.domain.usecases.UpdateProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditProfileUiState(
    val email: String = "", // Solo lectura
    val fullName: String = "",
    val bio: String = "",
    val gender: String = "other",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val getMyProfileUseCase: GetMyProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadCurrentData()
    }

    private fun loadCurrentData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val profile = getMyProfileUseCase.execute()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        email = profile.email,
                        fullName = profile.fullName,
                        bio = profile.bio,
                        gender = profile.gender
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "No se pudieron cargar tus datos.") }
            }
        }
    }

    fun onFullNameChange(value: String) = _uiState.update { it.copy(fullName = value, error = null) }
    fun onBioChange(value: String) = _uiState.update { it.copy(bio = value, error = null) }
    fun onGenderChange(value: String) = _uiState.update { it.copy(gender = value, error = null) }

    fun saveProfile() {
        val currentState = _uiState.value
        if (currentState.fullName.isBlank()) {
            _uiState.update { it.copy(error = "El nombre no puede estar vacío.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = updateProfileUseCase.execute(currentState.fullName, currentState.bio, currentState.gender)

            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Error al guardar el perfil.") }
            }
        }
    }
}