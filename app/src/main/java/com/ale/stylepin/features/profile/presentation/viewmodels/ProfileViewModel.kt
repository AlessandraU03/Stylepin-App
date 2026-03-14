package com.ale.stylepin.features.profile.presentation.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ale.stylepin.features.boards.domain.entities.Board
import com.ale.stylepin.features.boards.domain.usecases.GetUserBoardsUseCase
import com.ale.stylepin.features.pins.domain.entities.Pin
import com.ale.stylepin.features.pins.domain.usecases.GetPinsUseCase
import com.ale.stylepin.features.profile.domain.entities.Profile
import com.ale.stylepin.features.profile.domain.usecases.GetMyProfileUseCase
import com.ale.stylepin.features.profile.domain.usecases.UpdateProfileUseCase
import com.ale.stylepin.features.profile.domain.usecases.UploadAvatarUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val isLoading: Boolean = true,
    val isUploadingAvatar: Boolean = false,
    val profile: Profile? = null,
    val error: String? = null,
    val userPins: List<Pin> = emptyList(),
    val userBoards: List<Board> = emptyList(),
    val savedPins: List<Pin> = emptyList()
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getMyProfileUseCase: GetMyProfileUseCase,
    private val getPinsUseCase: GetPinsUseCase,
    private val getUserBoardsUseCase: GetUserBoardsUseCase,
    private val uploadAvatarUseCase: UploadAvatarUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // Escucha cambios en los pines de la base de datos local
        getPinsUseCase.executeFlow().onEach { allPins ->
            val profileId = _uiState.value.profile?.id
            if (profileId != null) {
                val created = allPins.filter { it.userId == profileId }
                val saved = allPins.filter { it.isSavedByMe || it.isLikedByMe }
                _uiState.update { it.copy(userPins = created, savedPins = saved) }
            }
        }.launchIn(viewModelScope)
    }

    // Refresca la información cada que la pantalla de perfil entra en foco
    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(error = null) } // No ponemos loading para que no parpadee si ya hay info
            try {
                val profile = getMyProfileUseCase.execute()
                _uiState.update { it.copy(profile = profile) }

                // Obtener Tableros (Esto arregla que no te salgan los nuevos)
                getUserBoardsUseCase(profile.id).onSuccess { boards ->
                    _uiState.update { it.copy(userBoards = boards) }
                }

                // Filtrar Pins iniciales
                val currentPins = getPinsUseCase().getOrNull() ?: emptyList()
                val created = currentPins.filter { it.userId == profile.id }
                val saved = currentPins.filter { it.isSavedByMe || it.isLikedByMe }
                _uiState.update { it.copy(isLoading = false, userPins = created, savedPins = saved) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    // Lógica para subir el avatar
    fun updateAvatar(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUploadingAvatar = true, error = null) }
            uploadAvatarUseCase.execute(uri.toString()).onSuccess { newUrl ->
                val profile = _uiState.value.profile
                if(profile != null) {
                    updateProfileUseCase.execute(profile.fullName, profile.bio, profile.gender, newUrl)
                    refresh()
                }
            }.onFailure { e ->
                _uiState.update { it.copy(error = "Error al subir foto: ${e.message}") }
            }
            _uiState.update { it.copy(isUploadingAvatar = false) }
        }
    }
}