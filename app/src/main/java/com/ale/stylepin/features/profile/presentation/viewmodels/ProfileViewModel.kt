package com.ale.stylepin.features.profile.presentation.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ale.stylepin.features.boards.domain.entities.Board
import com.ale.stylepin.features.boards.domain.usecases.GetBoardPinsUseCase
import com.ale.stylepin.features.boards.domain.usecases.GetUserBoardsUseCase
import com.ale.stylepin.features.boards.domain.usecases.RemovePinFromBoardUseCase
import com.ale.stylepin.features.pins.domain.entities.Pin
import com.ale.stylepin.features.pins.domain.usecases.GetPinByIdUseCase
import com.ale.stylepin.features.pins.domain.usecases.GetPinsUseCase
import com.ale.stylepin.features.profile.domain.entities.Profile
import com.ale.stylepin.features.profile.domain.usecases.GetMyProfileUseCase
import com.ale.stylepin.features.profile.domain.usecases.UpdateProfileUseCase
import com.ale.stylepin.features.profile.domain.usecases.UploadAvatarUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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
    private val getBoardPinsUseCase: GetBoardPinsUseCase,
    private val getPinByIdUseCase: GetPinByIdUseCase,
    private val removePinFromBoardUseCase: RemovePinFromBoardUseCase, // Para quitar guardados
    private val uploadAvatarUseCase: UploadAvatarUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    // Mapa interno: pinId → lista de boardIds donde está guardado (para poder eliminarlo de todos)
    private val _pinBoardMap = mutableMapOf<String, MutableList<String>>()

    init {
        getPinsUseCase.executeFlow().onEach { allPins ->
            val profileId = _uiState.value.profile?.id
            if (profileId != null) {
                val created = allPins.filter { it.userId == profileId }
                _uiState.update { it.copy(userPins = created) }
            }
        }.launchIn(viewModelScope)
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(error = null) }
            try {
                val profile = getMyProfileUseCase.execute()
                _uiState.update { it.copy(profile = profile) }

                // 1. Tableros del usuario (propios + colaborados)
                val boards = getUserBoardsUseCase(profile.id).getOrNull() ?: emptyList()
                _uiState.update { it.copy(userBoards = boards) }

                // 2. GUARDADOS: recorrer TODOS los tableros y juntar todos los pins únicos
                _pinBoardMap.clear()
                val boardPinsDeferred = boards.map { board ->
                    async {
                        val bps = getBoardPinsUseCase(board.id).getOrNull() ?: emptyList()
                        bps.forEach { bp ->
                            _pinBoardMap.getOrPut(bp.pinId) { mutableListOf() }.add(board.id)
                        }
                        bps
                    }
                }
                val uniquePinIds = boardPinsDeferred.awaitAll()
                    .flatten()
                    .map { it.pinId }
                    .distinct()

                val pinsDeferred = uniquePinIds.map { pinId ->
                    async { getPinByIdUseCase(pinId).getOrNull() }
                }
                val savedPinsList = pinsDeferred.awaitAll().filterNotNull()

                // 3. Pines creados por el usuario
                val currentPins = getPinsUseCase().getOrNull() ?: emptyList()
                val created = currentPins.filter { it.userId == profile.id }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        userPins = created,
                        savedPins = savedPinsList
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    /**
     * Quita un pin de TODOS los tableros donde está guardado.
     * Se llama desde el tab Guardados cuando el usuario presiona el botón eliminar.
     */
    fun removeSavedPin(pinId: String) {
        viewModelScope.launch {
            val boardIds = _pinBoardMap[pinId] ?: return@launch
            boardIds.forEach { boardId ->
                removePinFromBoardUseCase(boardId, pinId)
            }
            // Actualizar UI optimistamente
            _uiState.update { state ->
                state.copy(savedPins = state.savedPins.filter { it.id != pinId })
            }
            _pinBoardMap.remove(pinId)
        }
    }

    fun updateAvatar(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUploadingAvatar = true, error = null) }
            uploadAvatarUseCase.execute(uri.toString()).onSuccess { newUrl ->
                val profile = _uiState.value.profile
                if (profile != null) {
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