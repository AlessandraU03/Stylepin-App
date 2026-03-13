// com/ale/stylepin/features/pins/presentation/viewmodels/PinsViewModel.kt
package com.ale.stylepin.features.pins.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ale.stylepin.core.network.StylePinWebSocketManager
import com.ale.stylepin.features.boards.domain.repository.BoardsRepository
import com.ale.stylepin.features.community.domain.usecases.ToggleFollowUseCase
import com.ale.stylepin.features.pins.domain.entities.Pin
import com.ale.stylepin.features.pins.domain.repository.PinsRepository
import com.ale.stylepin.features.pins.domain.usecases.*
import com.ale.stylepin.features.pins.presentation.screens.PinsUiState
import com.ale.stylepin.features.profile.domain.usecases.GetMyProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PinsViewModel @Inject constructor(
    private val getPinsUseCase: GetPinsUseCase,
    private val getPinByIdUseCase: GetPinByIdUseCase,
    private val addPinUseCase: AddPinsUseCase,
    private val updatePinUseCase: UpdatePinsUseCase,
    private val deletePinUseCase: DeletePinsUseCase,
    private val getMyProfileUseCase: GetMyProfileUseCase,
    private val toggleFollowUseCase: ToggleFollowUseCase,
    private val repository: PinsRepository,
    private val boardsRepository: BoardsRepository, // <--- INYECTAMOS TABLEROS PARA PODER GUARDAR
    val webSocketManager: StylePinWebSocketManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(PinsUiState())
    val uiState: StateFlow<PinsUiState> = _uiState.asStateFlow()

    init {
        loadPins()
        loadCurrentUser()
        webSocketManager.connect()
    }

    override fun onCleared() {
        super.onCleared()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            try {
                val profile = getMyProfileUseCase.execute()
                _uiState.update { it.copy(currentUserId = profile.id) }
                // Ya que tenemos el ID, cargamos sus tableros para cuando quiera guardar
                loadUserBoards(profile.id)
            } catch (e: Exception) { }
        }
    }

    // --- NUEVO: GUARDAR PIN EN TABLERO ---
    private fun loadUserBoards(userId: String) {
        viewModelScope.launch {
            try {
                val boards = boardsRepository.getUserBoards(userId)
                _uiState.update { it.copy(userBoards = boards) }
            } catch (e: Exception) { }
        }
    }

    fun savePinToBoard(boardId: String, pinId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSavingToBoard = true) }
            try {
                boardsRepository.addPinToBoard(boardId, pinId, null)
                loadPinById(pinId) // Refrescamos para ver el número de "Guardados" subir
            } catch (e: Exception) {
                // Ignorar error de red
            } finally {
                _uiState.update { it.copy(isSavingToBoard = false) }
            }
        }
    }

    fun loadPins() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getPinsUseCase().fold(
                onSuccess = { pins -> _uiState.update { it.copy(isLoading = false, pins = pins, filteredPins = pins) } },
                onFailure = { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
            )
        }
    }

    fun loadPinById(pinId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingDetail = true, error = null) }
            getPinByIdUseCase(pinId).fold(
                onSuccess = { pin ->
                    _uiState.update { it.copy(isLoadingDetail = false, pinDetail = pin) }
                    populateFormFromPin(pin)
                    loadComments(pinId)
                },
                onFailure = { e -> _uiState.update { it.copy(isLoadingDetail = false, error = e.message) } }
            )
        }
    }

    // --- FUNCIONES SOCIALES ---

    fun toggleLike(pinId: String, currentLikeState: Boolean) {
        viewModelScope.launch {
            _uiState.update { state ->
                val updatedPin = state.pinDetail?.copy(
                    isLikedByMe = !currentLikeState,
                    likesCount = state.pinDetail.likesCount + if (currentLikeState) -1 else 1
                )
                state.copy(pinDetail = updatedPin)
            }
            val success = repository.toggleLike(pinId, currentLikeState)
            if (!success) { loadPinById(pinId) }
        }
    }

    fun toggleFollowAuthor(authorId: String) {
        viewModelScope.launch {
            toggleFollowUseCase.execute(authorId, false)
        }
    }

    private fun loadComments(pinId: String) {
        viewModelScope.launch {
            val comments = repository.getComments(pinId)
            _uiState.update { it.copy(comments = comments) }
        }
    }

    fun onCommentTextChanged(text: String) {
        _uiState.update { it.copy(newCommentText = text) }
    }

    fun addComment(pinId: String) {
        viewModelScope.launch {
            val text = _uiState.value.newCommentText
            if (text.isBlank()) return@launch

            _uiState.update { it.copy(newCommentText = "") }
            val success = repository.addComment(pinId, text)
            if (success) loadComments(pinId)
        }
    }

    private fun populateFormFromPin(pin: Pin) {
        _uiState.update {
            it.copy(
                title = pin.title,
                description = pin.description ?: "",
                imageUrl = pin.imageUrl,
                selectedCategory = pin.category.ifBlank { "outfit_completo" },
                selectedSeason = pin.season.ifBlank { "todo_el_ano" },
                isPrivate = pin.isPrivate,
                styles = pin.styles,
                occasions = pin.occasions,
                brands = pin.brands,
                priceRange = pin.priceRange.ifBlank { "bajo_500" },
                whereToBuy = pin.whereToBuy ?: "",
                purchaseLink = pin.purchaseLink ?: "",
                colors = pin.colors,
                tags = pin.tags
            )
        }
    }

    fun savePin(pinId: String? = null, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val s = _uiState.value
            _uiState.update { it.copy(isLoading = true, error = null) }

            val result = if (pinId == null) {
                addPinUseCase(
                    title = s.title, imageUrl = s.imageUrl, category = s.selectedCategory,
                    season = s.selectedSeason, description = s.description.takeIf { it.isNotBlank() },
                    isPrivate = s.isPrivate, styles = s.styles, occasions = s.occasions,
                    brands = s.brands, priceRange = s.priceRange,
                    whereToBuy = s.whereToBuy.takeIf { it.isNotBlank() },
                    purchaseLink = s.purchaseLink.takeIf { it.isNotBlank() },
                    colors = s.colors, tags = s.tags
                )
            } else {
                updatePinUseCase(
                    pinId = pinId, title = s.title, imageUrl = s.imageUrl.takeIf { it.isNotBlank() },
                    category = s.selectedCategory, season = s.selectedSeason,
                    description = s.description.takeIf { it.isNotBlank() }, isPrivate = s.isPrivate
                )
            }

            result.fold(
                onSuccess = { success ->
                    if (success) {
                        _uiState.value = PinsUiState()
                        loadPins()
                        onSuccess()
                    } else {
                        _uiState.update { it.copy(isLoading = false, error = "Error al guardar el pin") }
                    }
                },
                onFailure = { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
            )
        }
    }

    fun deletePin(id: String) {
        viewModelScope.launch {
            deletePinUseCase(id).fold(
                onSuccess = { success ->
                    if (success) loadPins() else _uiState.update { it.copy(error = "No se pudo eliminar el pin") }
                },
                onFailure = { e -> _uiState.update { it.copy(error = e.message) } }
            )
        }
    }

    fun onFormEvent(event: PinFormEvent) {
        _uiState.update { state ->
            when (event) {
                is PinFormEvent.TitleChanged         -> state.copy(title = event.value)
                is PinFormEvent.DescriptionChanged   -> state.copy(description = event.value)
                is PinFormEvent.ImageUrlChanged      -> state.copy(imageUrl = event.value)
                is PinFormEvent.CategoryChanged      -> state.copy(selectedCategory = event.value)
                is PinFormEvent.SeasonChanged        -> state.copy(selectedSeason = event.value)
                is PinFormEvent.PriceRangeChanged    -> state.copy(priceRange = event.value)
                is PinFormEvent.WhereToBuyChanged    -> state.copy(whereToBuy = event.value)
                is PinFormEvent.PurchaseLinkChanged  -> state.copy(purchaseLink = event.value)
                is PinFormEvent.IsPrivateChanged     -> state.copy(isPrivate = event.value)
                is PinFormEvent.StylesChanged        -> state.copy(styles = event.value)
                is PinFormEvent.OccasionsChanged     -> state.copy(occasions = event.value)
                is PinFormEvent.BrandsChanged        -> state.copy(brands = event.value)
                is PinFormEvent.ColorsChanged        -> state.copy(colors = event.value)
                is PinFormEvent.TagsChanged          -> state.copy(tags = event.value)
            }
        }
    }
}