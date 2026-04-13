package com.ale.stylepin.features.pins.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ale.stylepin.core.network.StylePinWebSocketManager
import com.ale.stylepin.features.boards.domain.usecases.AddPinToBoardUseCase
import com.ale.stylepin.features.boards.domain.usecases.CreateBoardUseCase
import com.ale.stylepin.features.boards.domain.usecases.GetUserBoardsUseCase
import com.ale.stylepin.features.community.domain.usecases.CheckFollowStatusUseCase
import com.ale.stylepin.features.community.domain.usecases.ToggleFollowUseCase
import com.ale.stylepin.features.likes.domain.usecases.ToggleLikeUseCase
import com.ale.stylepin.features.pins.domain.entities.Pin
import com.ale.stylepin.features.pins.domain.repository.PinsRepository
import com.ale.stylepin.features.pins.domain.usecases.*
import com.ale.stylepin.features.pins.presentation.screens.PinsUiState
import com.ale.stylepin.features.profile.domain.usecases.GetMyProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// Memoria global estática para forzar el estado en la sesión actual sin importar lo que diga el servidor
private val localLikedPins = mutableSetOf<String>()
private val localSavedPins = mutableSetOf<String>()

@HiltViewModel
class PinsViewModel @Inject constructor(
    private val getPinsUseCase: GetPinsUseCase,
    private val getPinByIdUseCase: GetPinByIdUseCase,
    private val addPinUseCase: AddPinsUseCase,
    private val updatePinUseCase: UpdatePinsUseCase,
    private val deletePinUseCase: DeletePinsUseCase,
    private val getMyProfileUseCase: GetMyProfileUseCase,
    private val toggleLikeUseCase: ToggleLikeUseCase,
    private val checkFollowStatusUseCase: CheckFollowStatusUseCase,
    private val toggleFollowUseCase: ToggleFollowUseCase,
    private val getUserBoardsUseCase: GetUserBoardsUseCase,
    private val addPinToBoardUseCase: AddPinToBoardUseCase,
    private val createBoardUseCase: CreateBoardUseCase,
    private val pinsRepository: PinsRepository,
    val webSocketManager: StylePinWebSocketManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(PinsUiState())
    val uiState: StateFlow<PinsUiState> = _uiState.asStateFlow()

    init {
        webSocketManager.connect()
        viewModelScope.launch { loadCurrentUser() }
        getPinsUseCase.executeFlow()
            .onEach { pins -> _uiState.update { it.copy(pins = pins, filteredPins = pins, isLoading = false) } }
            .launchIn(viewModelScope)
        refreshPins()
    }

    private suspend fun loadCurrentUser() {
        try {
            val profile = getMyProfileUseCase.execute()
            _uiState.update { it.copy(currentUserId = profile.id) }
        } catch (e: Exception) { }
    }

    fun refreshPins() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = it.pins.isEmpty()) }
            getPinsUseCase.refresh()
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun loadPins() = refreshPins()

    fun loadPinById(pinId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingDetail = true, error = null) }

            getPinByIdUseCase(pinId).fold(
                onSuccess = { apiPin ->
                    // 🔴 LÓGICA INFALIBLE: Si lo guardamos o likeamos en esta sesión, forzamos que se vea así
                    val forceLiked = localLikedPins.contains(apiPin.id)
                    val forceSaved = localSavedPins.contains(apiPin.id)

                    val finalPin = apiPin.copy(
                        isLikedByMe = forceLiked || apiPin.isLikedByMe,
                        isSavedByMe = forceSaved || apiPin.isSavedByMe,
                        likesCount = if (forceLiked && !apiPin.isLikedByMe) apiPin.likesCount + 1 else apiPin.likesCount,
                        savesCount = if (forceSaved && !apiPin.isSavedByMe) apiPin.savesCount + 1 else apiPin.savesCount
                    )

                    _uiState.update { it.copy(isLoadingDetail = false, pinDetail = finalPin) }
                    populateFormFromPin(finalPin)
                    pinsRepository.savePinLocal(finalPin)

                    loadComments(pinId)
                    if (finalPin.userId != _uiState.value.currentUserId) {
                        checkFollowStatusUseCase.execute(finalPin.userId).onSuccess { isFollowing ->
                            _uiState.update { it.copy(authorIsFollowed = isFollowing) }
                        }
                    }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoadingDetail = false, error = e.message) }
                }
            )
        }
    }

    private fun populateFormFromPin(pin: Pin) {
        _uiState.update {
            it.copy(
                title = pin.title, description = pin.description ?: "", imageUrl = pin.imageUrl,
                selectedCategory = pin.category, selectedSeason = pin.season, isPrivate = pin.isPrivate,
                styles = pin.styles, occasions = pin.occasions, brands = pin.brands,
                priceRange = pin.priceRange, whereToBuy = pin.whereToBuy ?: "",
                purchaseLink = pin.purchaseLink ?: "", colors = pin.colors, tags = pin.tags
            )
        }
    }

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
                is PinFormEvent.StylesChanged -> state.copy(styles = event.value)
                is PinFormEvent.OccasionsChanged -> state.copy(occasions = event.value)
                is PinFormEvent.BrandsChanged -> state.copy(brands = event.value)
                is PinFormEvent.ColorsChanged -> state.copy(colors = event.value)
                is PinFormEvent.TagsChanged -> state.copy(tags = event.value)
            }
        }
    }

    fun savePin(pinId: String? = null, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val s = _uiState.value
            val result = if (pinId == null) {
                addPinUseCase(
                    s.title, s.imageUrl, s.selectedCategory, s.selectedSeason, s.description, s.isPrivate,
                    s.styles, s.occasions, s.brands, s.priceRange, s.whereToBuy, s.purchaseLink, s.colors, s.tags
                )
            } else {
                updatePinUseCase(
                    pinId, s.title, s.imageUrl, s.selectedCategory, s.selectedSeason, s.description, s.isPrivate,
                    s.styles, s.occasions, s.brands, s.priceRange, s.whereToBuy, s.purchaseLink, s.colors, s.tags
                )
            }
            result.onSuccess {
                refreshPins()
                onSuccess()
            }.onFailure { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
        }
    }

    fun deletePin(id: String) {
        viewModelScope.launch { deletePinUseCase(id).onSuccess { refreshPins() } }
    }

    // --- SOCIAL: LIKES Y FOLLOWS ---
    fun toggleLike(pinId: String) {
        viewModelScope.launch {
            val currentPin = _uiState.value.pinDetail ?: return@launch
            val newLikeState = !currentPin.isLikedByMe

            if (newLikeState) localLikedPins.add(pinId) else localLikedPins.remove(pinId)

            val updatedPin = currentPin.copy(
                isLikedByMe = newLikeState,
                likesCount = if (newLikeState) currentPin.likesCount + 1 else currentPin.likesCount - 1
            )

            _uiState.update { it.copy(pinDetail = updatedPin) }
            pinsRepository.savePinLocal(updatedPin)

            toggleLikeUseCase(pinId).onFailure {
                if (!newLikeState) localLikedPins.add(pinId) else localLikedPins.remove(pinId)
                _uiState.update { state -> state.copy(pinDetail = currentPin) }
            }
        }
    }

    fun toggleFollowAuthor() {
        viewModelScope.launch {
            val pin = _uiState.value.pinDetail ?: return@launch
            val currentlyFollowing = _uiState.value.authorIsFollowed

            _uiState.update { it.copy(authorIsFollowed = !currentlyFollowing) }
            toggleFollowUseCase.execute(pin.userId, currentlyFollowing).onFailure {
                _uiState.update { it.copy(authorIsFollowed = currentlyFollowing) }
            }
        }
    }

    private fun loadComments(pinId: String) {
        viewModelScope.launch {
            try {
                val comments = pinsRepository.getPinComments(pinId)
                _uiState.update { it.copy(comments = comments) }
            } catch (e: Exception) { }
        }
    }

    fun onCommentInputChanged(text: String) {
        _uiState.update { it.copy(commentInput = text) }
    }

    fun postComment() {
        val text = _uiState.value.commentInput
        val pinId = _uiState.value.pinDetail?.id ?: return
        if (text.isBlank()) return

        viewModelScope.launch {
            try {
                val newComment = pinsRepository.addComment(pinId, text)
                val currentPin = _uiState.value.pinDetail!!
                val updatedPin = currentPin.copy(commentsCount = currentPin.commentsCount + 1)

                _uiState.update {
                    it.copy(comments = it.comments + newComment, commentInput = "", pinDetail = updatedPin)
                }
                pinsRepository.savePinLocal(updatedPin)
            } catch (e: Exception) { }
        }
    }

    // --- GUARDAR EN TABLERO ---
    fun showSaveDialog() {
        _uiState.update { it.copy(isSaveSheetVisible = true) }
        viewModelScope.launch {
            val userId = _uiState.value.currentUserId ?: return@launch
            getUserBoardsUseCase(userId).onSuccess { boards ->
                _uiState.update { it.copy(myBoards = boards) }
            }
        }
    }

    fun hideSaveDialog() {
        _uiState.update { it.copy(isSaveSheetVisible = false) }
    }

    fun savePinToBoard(boardId: String) {
        val currentPin = _uiState.value.pinDetail ?: return
        viewModelScope.launch {
            addPinToBoardUseCase(boardId, currentPin.id).onSuccess {
                hideSaveDialog()
                localSavedPins.add(currentPin.id) // GUARDAMOS EN MEMORIA CACHÉ
                val updatedPin = currentPin.copy(isSavedByMe = true, savesCount = currentPin.savesCount + 1)

                _uiState.update { it.copy(pinDetail = updatedPin) }
                pinsRepository.savePinLocal(updatedPin)
            }
        }
    }

    fun createBoardAndSavePin(boardName: String) {
        val currentPin = _uiState.value.pinDetail ?: return
        if (boardName.isBlank()) return
        viewModelScope.launch {
            createBoardUseCase(name = boardName).onSuccess { newBoard ->
                addPinToBoardUseCase(newBoard.id, currentPin.id).onSuccess {
                    hideSaveDialog()
                    localSavedPins.add(currentPin.id) // GUARDAMOS EN MEMORIA CACHÉ
                    val updatedPin = currentPin.copy(isSavedByMe = true, savesCount = currentPin.savesCount + 1)

                    _uiState.update { it.copy(pinDetail = updatedPin) }
                    pinsRepository.savePinLocal(updatedPin)
                }
            }
        }
    }
}