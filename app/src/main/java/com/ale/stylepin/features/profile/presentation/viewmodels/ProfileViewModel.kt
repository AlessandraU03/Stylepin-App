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
    val isLoading: Boolean = true,          // Loading del perfil completo (primera carga)
    val isBoardsLoading: Boolean = false,   // Loading solo de tableros/guardados (al volver)
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
    private val removePinFromBoardUseCase: RemovePinFromBoardUseCase,
    private val uploadAvatarUseCase: UploadAvatarUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    private val _pinBoardMap = mutableMapOf<String, MutableList<String>>()

    init {
        refresh()

        getPinsUseCase.executeFlow().onEach { allPins ->
            val profileId = _uiState.value.profile?.id
            if (profileId != null) {
                _uiState.update { it.copy(userPins = allPins.filter { p -> p.userId == profileId }) }
            }
        }.launchIn(viewModelScope)
    }

    /** Carga completa: perfil + tableros + guardados. Solo para la primera vez o "Reintentar". */
    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val profile = getMyProfileUseCase.execute()
                _uiState.update { it.copy(profile = profile) }

                val boardsJob = async { getUserBoardsUseCase(profile.id).getOrNull() ?: emptyList() }
                val pinsJob   = async { getPinsUseCase().getOrNull() ?: emptyList() }

                val boards  = boardsJob.await()
                val allPins = pinsJob.await()

                _uiState.update {
                    it.copy(
                        userBoards = boards,
                        userPins   = allPins.filter { p -> p.userId == profile.id }
                    )
                }

                loadSavedPins(boards)
                _uiState.update { it.copy(isLoading = false) }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    /**
     * Recarga SOLO tableros y guardados sin tocar el perfil ni el isLoading principal.
     * Se llama desde ProfileScreen en ON_RESUME (al volver de CreateBoard, EditBoard, etc.)
     * Así el header del perfil no parpadea y la experiencia es fluida.
     */
    fun refreshBoards() {
        val profileId = _uiState.value.profile?.id ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isBoardsLoading = true) }
            try {
                val boards = getUserBoardsUseCase(profileId).getOrNull() ?: emptyList()
                _uiState.update { it.copy(userBoards = boards) }
                loadSavedPins(boards)
            } catch (e: Exception) {
                // Error silencioso — no borramos los datos anteriores
            } finally {
                _uiState.update { it.copy(isBoardsLoading = false) }
            }
        }
    }

    /** Carga los pins guardados a partir de la lista de tableros. */
    private suspend fun loadSavedPins(boards: List<Board>) {
        if (boards.isEmpty()) {
            _uiState.update { it.copy(savedPins = emptyList()) }
            return
        }
        _pinBoardMap.clear()
        val boardPinsJobs = boards.map { board ->
            viewModelScope.async {
                val bps = getBoardPinsUseCase(board.id).getOrNull() ?: emptyList()
                bps.forEach { bp ->
                    _pinBoardMap.getOrPut(bp.pinId) { mutableListOf() }.add(board.id)
                }
                bps
            }
        }
        val uniquePinIds = boardPinsJobs.awaitAll().flatten().map { it.pinId }.distinct()
        val pinDetailJobs = uniquePinIds.map { pinId ->
            viewModelScope.async { getPinByIdUseCase(pinId).getOrNull() }
        }
        _uiState.update { it.copy(savedPins = pinDetailJobs.awaitAll().filterNotNull()) }
    }

    /** Quita un pin de todos los tableros donde está guardado. */
    fun removeSavedPin(pinId: String) {
        viewModelScope.launch {
            val boardIds = _pinBoardMap[pinId] ?: return@launch
            boardIds.forEach { boardId -> removePinFromBoardUseCase(boardId, pinId) }
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