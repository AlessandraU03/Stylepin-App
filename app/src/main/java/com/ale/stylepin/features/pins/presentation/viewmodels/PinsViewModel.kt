package com.ale.stylepin.features.pins.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ale.stylepin.core.network.StylePinWebSocketManager
import com.ale.stylepin.features.boards.domain.repository.BoardsRepository
import com.ale.stylepin.features.likes.domain.usecases.ToggleLikeUseCase
import com.ale.stylepin.features.pins.domain.entities.Pin
import com.ale.stylepin.features.pins.domain.repository.PinsRepository
import com.ale.stylepin.features.pins.domain.usecases.*
import com.ale.stylepin.features.pins.presentation.screens.PinsUiState
import com.ale.stylepin.features.profile.domain.usecases.GetMyProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
    private val toggleLikeUseCase: ToggleLikeUseCase,
    private val boardsRepository: BoardsRepository, // <--- INYECTADO PARA GUARDAR
    private val pinsRepository: PinsRepository,     // <--- INYECTADO PARA COMENTARIOS
    val webSocketManager: StylePinWebSocketManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(PinsUiState())
    val uiState: StateFlow<PinsUiState> = _uiState.asStateFlow()

    init {
        webSocketManager.connect()
        viewModelScope.launch { loadCurrentUser() }
        getPinsUseCase.executeFlow().onEach { pins ->
            _uiState.update { it.copy(pins = pins, filteredPins = pins, isLoading = false) }
        }.launchIn(viewModelScope)
        refreshPins()
    }

    private suspend fun loadCurrentUser() {
        try {
            val profile = getMyProfileUseCase.execute()
            _uiState.update { it.copy(currentUserId = profile.id) }
            val boards = boardsRepository.getUserBoards(profile.id)
            _uiState.update { it.copy(userBoards = boards) }
        } catch (e: Exception) { }
    }

    fun refreshPins() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = it.pins.isEmpty()) }
            getPinsUseCase.refresh()
        }
    }
    fun loadPins() { refreshPins() }

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

    fun toggleLike(pinId: String) {
        val pin = _uiState.value.pinDetail ?: return
        val currentLiked = pin.isLikedByMe

        // Optimistic update
        _uiState.update { state -> state.copy(pinDetail = pin.copy(isLikedByMe = !currentLiked, likesCount = pin.likesCount + if(currentLiked) -1 else 1)) }

        viewModelScope.launch {
            toggleLikeUseCase(pinId, currentLiked).onSuccess { status ->
                getPinsUseCase.refresh() // Actualiza en la base de datos
            }.onFailure {
                // Revertir si falla
                _uiState.update { state -> state.copy(pinDetail = pin) }
            }
        }
    }

    fun savePinToBoard(boardId: String, pinId: String) {
        viewModelScope.launch {
            try {
                boardsRepository.addPinToBoard(boardId, pinId, null)
                loadPinById(pinId)
            } catch (e: Exception) { }
        }
    }

    private fun loadComments(pinId: String) {
        viewModelScope.launch {
            val comments = pinsRepository.getComments(pinId)
            _uiState.update { it.copy(comments = comments) }
        }
    }
    fun onCommentTextChanged(text: String) { _uiState.update { it.copy(newCommentText = text) } }
    fun addComment(pinId: String) {
        viewModelScope.launch {
            val text = _uiState.value.newCommentText
            if (text.isBlank()) return@launch
            _uiState.update { it.copy(newCommentText = "") }
            val success = pinsRepository.addComment(pinId, text)
            if (success) loadComments(pinId)
        }
    }

    private fun populateFormFromPin(pin: Pin) {
        _uiState.update {
            it.copy(
                title = pin.title, description = pin.description ?: "", imageUrl = pin.imageUrl,
                selectedCategory = pin.category.ifBlank { "outfit_completo" },
                selectedSeason = pin.season.ifBlank { "todo_el_ano" },
                isPrivate = pin.isPrivate, priceRange = pin.priceRange.ifBlank { "bajo_500" },
                whereToBuy = pin.whereToBuy ?: "", purchaseLink = pin.purchaseLink ?: ""
            )
        }
    }

    fun savePin(pinId: String? = null, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val s = _uiState.value
            val result = if (pinId == null) {
                addPinUseCase(s.title, s.imageUrl, s.selectedCategory, s.selectedSeason, s.description, s.isPrivate, s.styles, s.occasions, s.brands, s.priceRange, s.whereToBuy, s.purchaseLink, s.colors, s.tags)
            } else {
                updatePinUseCase(pinId, s.title, s.imageUrl, s.selectedCategory, s.selectedSeason, s.description, s.isPrivate)
            }
            result.onSuccess { refreshPins(); onSuccess() }
        }
    }

    fun deletePin(id: String) { viewModelScope.launch { deletePinUseCase(id).onSuccess { refreshPins() } } }

    fun onFormEvent(event: PinFormEvent) {
        _uiState.update { state ->
            when (event) {
                is PinFormEvent.TitleChanged -> state.copy(title = event.value)
                is PinFormEvent.DescriptionChanged -> state.copy(description = event.value)
                is PinFormEvent.ImageUrlChanged -> state.copy(imageUrl = event.value)
                is PinFormEvent.CategoryChanged -> state.copy(selectedCategory = event.value)
                is PinFormEvent.SeasonChanged -> state.copy(selectedSeason = event.value)
                is PinFormEvent.PriceRangeChanged -> state.copy(priceRange = event.value)
                is PinFormEvent.WhereToBuyChanged -> state.copy(whereToBuy = event.value)
                is PinFormEvent.PurchaseLinkChanged -> state.copy(purchaseLink = event.value)
                is PinFormEvent.IsPrivateChanged -> state.copy(isPrivate = event.value)
                else -> state
            }
        }
    }
}