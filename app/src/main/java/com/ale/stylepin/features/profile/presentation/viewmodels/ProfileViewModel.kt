package com.ale.stylepin.features.profile.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ale.stylepin.features.profile.domain.entities.Profile
import com.ale.stylepin.features.profile.domain.entities.ProfileBoard
import com.ale.stylepin.features.profile.domain.entities.ProfilePin
import com.ale.stylepin.features.profile.domain.entities.ProfileSavedPin
import com.ale.stylepin.features.profile.domain.usecases.GetMyProfileUseCase
import com.ale.stylepin.features.profile.domain.usecases.GetProfileContentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val isLoading: Boolean = true,
    val profile: Profile? = null,
    val error: String? = null,

    // Contenido de las Tabs
    val pins: List<ProfilePin> = emptyList(),
    val boards: List<ProfileBoard> = emptyList(),
    val savedPins: List<ProfileSavedPin> = emptyList(),
    val isLoadingContent: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getMyProfileUseCase: GetMyProfileUseCase,
    private val getProfileContentUseCase: GetProfileContentUseCase // Inyectamos el nuevo UseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadProfileAndContent()
    }

    private fun loadProfileAndContent() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val profile = getMyProfileUseCase.execute()
                _uiState.update { it.copy(isLoading = false, profile = profile) }

                // Una vez que tenemos el perfil, cargamos sus fotos
                loadUserContent(profile.id)
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun loadUserContent(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingContent = true) }

            // Ejecutamos las 3 llamadas a la red en paralelo para que cargue súper rápido
            val pinsTask = async { getProfileContentUseCase.getPins(userId) }
            val boardsTask = async { getProfileContentUseCase.getBoards(userId) }
            val savedTask = async { getProfileContentUseCase.getSavedPins(userId) }

            val pinsResult = pinsTask.await()
            val boardsResult = boardsTask.await()
            val savedResult = savedTask.await()

            _uiState.update {
                it.copy(
                    isLoadingContent = false,
                    pins = pinsResult.getOrNull() ?: emptyList(),
                    boards = boardsResult.getOrNull() ?: emptyList(),
                    savedPins = savedResult.getOrNull() ?: emptyList()
                )
            }
        }
    }
}