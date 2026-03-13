package com.ale.stylepin.features.boards.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ale.stylepin.features.boards.domain.entities.Board
import com.ale.stylepin.features.boards.domain.usecases.*
import com.ale.stylepin.features.boards.presentation.screens.BoardsUiState
import com.ale.stylepin.features.pins.domain.usecases.GetPinByIdUseCase
import com.ale.stylepin.features.pins.domain.usecases.GetPinsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BoardsViewModel @Inject constructor(
    private val getAllBoardsUseCase: GetAllBoardsUseCase,
    private val getUserBoardsUseCase: GetUserBoardsUseCase,
    private val getBoardByIdUseCase: GetBoardByIdUseCase,
    private val createBoardUseCase: CreateBoardUseCase,
    private val updateBoardUseCase: UpdateBoardUseCase,
    private val deleteBoardUseCase: DeleteBoardUseCase,
    private val getBoardPinsUseCase: GetBoardPinsUseCase,
    private val addPinToBoardUseCase: AddPinToBoardUseCase,
    private val removePinFromBoardUseCase: RemovePinFromBoardUseCase,
    private val getCollaboratorsUseCase: GetCollaboratorsUseCase,
    private val addCollaboratorUseCase: AddCollaboratorUseCase,
    private val removeCollaboratorUseCase: RemoveCollaboratorUseCase,
    private val updateCollaboratorPermissionsUseCase: UpdateCollaboratorPermissionsUseCase,
    private val getPinsUseCase: GetPinsUseCase,
    private val getPinByIdUseCase: GetPinByIdUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BoardsUiState())
    val uiState: StateFlow<BoardsUiState> = _uiState.asStateFlow()

    // ── Lista ─────────────────────────────────────────────────

    fun loadAllBoards(userId: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            getAllBoardsUseCase(userId).fold(
                onSuccess = { boards -> _uiState.update { it.copy(isLoading = false, boards = boards) } },
                onFailure = { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
            )
        }
    }

    fun loadUserBoards(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            getUserBoardsUseCase(userId).fold(
                onSuccess = { boards -> _uiState.update { it.copy(isLoading = false, boards = boards) } },
                onFailure = { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
            )
        }
    }

    // ── Detalle ───────────────────────────────────────────────

    fun loadBoardDetail(boardId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingDetail = true, error = null) }
            getBoardByIdUseCase(boardId).fold(
                onSuccess = { board ->
                    _uiState.update { it.copy(isLoadingDetail = false, boardDetail = board) }
                    loadBoardPins(boardId)
                    loadCollaborators(boardId)
                },
                onFailure = { e -> _uiState.update { it.copy(isLoadingDetail = false, error = e.message) } }
            )
        }
    }

    private fun loadBoardPins(boardId: String) {
        viewModelScope.launch {
            getBoardPinsUseCase(boardId).fold(
                onSuccess = { pins ->
                    _uiState.update { it.copy(boardPins = pins) }
                    pins.forEach { boardPin ->
                        loadPinDetail(boardPin.pinId)
                    }
                },
                onFailure = { e -> _uiState.update { it.copy(error = e.message) } }
            )
        }
    }

    private fun loadPinDetail(pinId: String) {
        if (_uiState.value.pinsDetails.containsKey(pinId)) return
        viewModelScope.launch {
            getPinByIdUseCase(pinId).fold(
                onSuccess = { pin ->
                    _uiState.update { state ->
                        val updatedDetails = state.pinsDetails.toMutableMap()
                        updatedDetails[pinId] = pin
                        state.copy(pinsDetails = updatedDetails)
                    }
                },
                onFailure = { }
            )
        }
    }

    private fun loadCollaborators(boardId: String) {
        viewModelScope.launch {
            getCollaboratorsUseCase(boardId).fold(
                onSuccess = { colabs -> _uiState.update { it.copy(collaborators = colabs) } },
                onFailure = { e -> _uiState.update { it.copy(error = e.message) } }
            )
        }
    }

    // ── CRUD Tablero ──────────────────────────────────────────

    fun createBoard(userId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val s = _uiState.value
            _uiState.update { it.copy(isLoading = true, error = null) }
            createBoardUseCase(
                name = s.name,
                description = s.description.takeIf { it.isNotBlank() },
                isPrivate = s.isPrivate,
                isCollaborative = s.isCollaborative
            ).fold(
                onSuccess = { resetForm(); loadAllBoards(); onSuccess() },
                onFailure = { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
            )
        }
    }

    fun updateBoard(boardId: String, userId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val s = _uiState.value
            _uiState.update { it.copy(isLoading = true, error = null) }
            updateBoardUseCase(
                boardId = boardId,
                name = s.name,
                description = s.description.takeIf { it.isNotBlank() },
                isPrivate = s.isPrivate,
                isCollaborative = s.isCollaborative
            ).fold(
                onSuccess = { resetForm(); loadAllBoards(); onSuccess() },
                onFailure = { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
            )
        }
    }

    fun deleteBoard(boardId: String, userId: String) {
        viewModelScope.launch {
            deleteBoardUseCase(boardId).fold(
                onSuccess = { loadAllBoards() },
                onFailure = { e -> _uiState.update { it.copy(error = e.message) } }
            )
        }
    }

    // ── Pins del tablero ──────────────────────────────────────

    fun loadUserPins() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingUserPins = true, error = null) }
            getPinsUseCase().fold(
                onSuccess = { pins -> _uiState.update { it.copy(isLoadingUserPins = false, userPins = pins) } },
                onFailure = { e -> _uiState.update { it.copy(isLoadingUserPins = false, error = e.message) } }
            )
        }
    }

    fun addPinToBoard(boardId: String, pinId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val notes = _uiState.value.addPinNotes.takeIf { it.isNotBlank() }
            addPinToBoardUseCase(boardId, pinId, notes).fold(
                onSuccess = {
                    _uiState.update { it.copy(addPinNotes = "") }
                    loadBoardPins(boardId)
                    onSuccess()
                },
                onFailure = { e -> _uiState.update { it.copy(error = e.message) } }
            )
        }
    }

    fun removePinFromBoard(boardId: String, pinId: String) {
        viewModelScope.launch {
            removePinFromBoardUseCase(boardId, pinId).fold(
                onSuccess = { loadBoardPins(boardId) },
                onFailure = { e -> _uiState.update { it.copy(error = e.message) } }
            )
        }
    }

    // ── Colaboradores ─────────────────────────────────────────

    fun addCollaborator(boardId: String, userId: String, canEdit: Boolean, canAddPins: Boolean, canRemovePins: Boolean, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            addCollaboratorUseCase(boardId, userId, canEdit, canAddPins, canRemovePins).fold(
                onSuccess = { 
                    loadCollaborators(boardId)
                    onSuccess()
                },
                onFailure = { e -> _uiState.update { it.copy(error = e.message) } }
            )
        }
    }

    fun removeCollaborator(boardId: String, collaboratorUserId: String) {
        viewModelScope.launch {
            removeCollaboratorUseCase(boardId, collaboratorUserId).fold(
                onSuccess = { loadCollaborators(boardId) },
                onFailure = { e -> _uiState.update { it.copy(error = e.message) } }
            )
        }
    }

    fun updateCollaboratorPermissions(
        boardId: String,
        collaboratorUserId: String,
        canEdit: Boolean,
        canAddPins: Boolean,
        canRemovePins: Boolean,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            updateCollaboratorPermissionsUseCase(boardId, collaboratorUserId, canEdit, canAddPins, canRemovePins).fold(
                onSuccess = {
                    loadCollaborators(boardId)
                    onSuccess()
                },
                onFailure = { e -> _uiState.update { it.copy(error = e.message) } }
            )
        }
    }

    // ── Formulario ────────────────────────────────────────────

    fun populateFormFromBoard(board: Board) {
        _uiState.update {
            it.copy(
                name = board.name,
                description = board.description ?: "",
                isPrivate = board.isPrivate,
                isCollaborative = board.isCollaborative
            )
        }
    }

    fun onFormEvent(event: BoardFormEvent) {
        _uiState.update { state ->
            when (event) {
                is BoardFormEvent.NameChanged            -> state.copy(name = event.value)
                is BoardFormEvent.DescriptionChanged     -> state.copy(description = event.value)
                is BoardFormEvent.IsPrivateChanged       -> state.copy(isPrivate = event.value)
                is BoardFormEvent.IsCollaborativeChanged -> state.copy(isCollaborative = event.value)
                is BoardFormEvent.AddPinNotesChanged     -> state.copy(addPinNotes = event.value)
            }
        }
    }

    fun clearError() { _uiState.update { it.copy(error = null) } }

    private fun resetForm() {
        _uiState.update { it.copy(isLoading = false, name = "", description = "", isPrivate = false, isCollaborative = false) }
    }
}
