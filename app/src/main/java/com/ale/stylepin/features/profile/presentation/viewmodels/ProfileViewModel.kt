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
    private val removePinFromBoardUseCase: RemovePinFromBoardUseCase,
    private val uploadAvatarUseCase: UploadAvatarUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    // Mapa interno: pinId → boardIds donde está guardado (para poder quitarlo de todos)
    private val _pinBoardMap = mutableMapOf<String, MutableList<String>>()

    init {
        // ← CORRECCIÓN PRINCIPAL: cargar datos al crear el ViewModel,
        // no esperar a que ProfileScreen llame refresh() desde el DisposableEffect
        refresh()

        // Escuchar Room para actualizar pins creados en tiempo real
        getPinsUseCase.executeFlow().onEach { allPins ->
            val profileId = _uiState.value.profile?.id
            if (profileId != null) {
                _uiState.update { it.copy(userPins = allPins.filter { p -> p.userId == profileId }) }
            }
        }.launchIn(viewModelScope)
    }

    fun refresh() {
        viewModelScope.launch {
            // Mostrar loading pero conservar el perfil si ya estaba cargado
            // (evita pantalla completamente vacía al recargar)
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                // 1. Perfil del usuario
                val profile = getMyProfileUseCase.execute()
                _uiState.update { it.copy(profile = profile) }

                // 2. Tableros y pins creados en paralelo
                val boardsJob = async { getUserBoardsUseCase(profile.id).getOrNull() ?: emptyList() }
                val pinsJob   = async { getPinsUseCase().getOrNull() ?: emptyList() }

                val boards  = boardsJob.await()
                val allPins = pinsJob.await()
                val created = allPins.filter { it.userId == profile.id }

                _uiState.update { it.copy(userBoards = boards, userPins = created) }

                // 3. Pins guardados: recorrer todos los tableros en paralelo
                _pinBoardMap.clear()
                if (boards.isNotEmpty()) {
                    val boardPinsJobs = boards.map { board ->
                        async {
                            val bps = getBoardPinsUseCase(board.id).getOrNull() ?: emptyList()
                            // Registrar en el mapa para poder quitar después
                            bps.forEach { bp ->
                                _pinBoardMap.getOrPut(bp.pinId) { mutableListOf() }.add(board.id)
                            }
                            bps
                        }
                    }
                    val uniquePinIds = boardPinsJobs.awaitAll()
                        .flatten()
                        .map { it.pinId }
                        .distinct()

                    val pinDetailJobs = uniquePinIds.map { pinId ->
                        async { getPinByIdUseCase(pinId).getOrNull() }
                    }
                    _uiState.update { it.copy(savedPins = pinDetailJobs.awaitAll().filterNotNull()) }
                }

                _uiState.update { it.copy(isLoading = false) }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    /**
     * Quita un pin guardado de TODOS los tableros donde está.
     * Se llama desde el botón Delete del tab Guardados.
     */
    fun removeSavedPin(pinId: String) {
        viewModelScope.launch {
            val boardIds = _pinBoardMap[pinId] ?: return@launch
            boardIds.forEach { boardId ->
                removePinFromBoardUseCase(boardId, pinId)
            }
            // Actualización optimista: quitar de la lista sin esperar al servidor
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