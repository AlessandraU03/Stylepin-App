package com.ale.stylepin.features.pins.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ale.stylepin.core.network.StylePinWebSocketManager
import com.ale.stylepin.features.likes.domain.usecases.ToggleLikeUseCase
import com.ale.stylepin.features.pins.domain.entities.Pin
import com.ale.stylepin.features.pins.domain.usecases.AddPinsUseCase
import com.ale.stylepin.features.pins.domain.usecases.DeletePinsUseCase
import com.ale.stylepin.features.pins.domain.usecases.GetPinByIdUseCase
import com.ale.stylepin.features.pins.domain.usecases.GetPinsUseCase
import com.ale.stylepin.features.pins.domain.usecases.UpdatePinsUseCase
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
    val webSocketManager: StylePinWebSocketManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(PinsUiState())
    val uiState: StateFlow<PinsUiState> = _uiState.asStateFlow()

    init {
        // 1. Conectar WebSocket
        webSocketManager.connect()

        // 2. Cargar perfil de usuario
        viewModelScope.launch {
            loadCurrentUser()
        }

        // 3. Suscribirse al Flow de Room (Single Source of Truth)
        // Solo refrescamos desde la red si Room está TOTALMENTE vacío
        getPinsUseCase.executeFlow()
            .onEach { pins ->
                _uiState.update { it.copy(pins = pins, filteredPins = pins, isLoading = false) }
                
                // Si la base de datos está vacía, forzamos la primera carga
                if (pins.isEmpty()) {
                    refreshPins()
                }
            }
            .launchIn(viewModelScope)
    }

    private suspend fun loadCurrentUser() {
        try {
            val profile = getMyProfileUseCase.execute()
            _uiState.update { it.copy(currentUserId = profile.id) }
        } catch (e: Exception) {
            // Error silencioso
        }
    }

    /**
     * Sincronización manual o inicial. 
     * Ya no se llama automáticamente cada vez que se abre la app si ya hay datos.
     */
    fun refreshPins() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getPinsUseCase.refresh()
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    // Mantenemos loadPins para el SwipeRefresh (acción manual del usuario)
    fun loadPins() {
        refreshPins()
    }

    fun loadPinById(pinId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingDetail = true, error = null) }
            getPinByIdUseCase(pinId).fold(
                onSuccess = { pin ->
                    _uiState.update { it.copy(isLoadingDetail = false, pinDetail = pin) }
                    populateFormFromPin(pin)
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
                title = pin.title,
                description = pin.description ?: "",
                imageUrl = pin.imageUrl,
                selectedCategory = pin.category,
                selectedSeason = pin.season,
                isPrivate = pin.isPrivate,
                styles = pin.styles,
                occasions = pin.occasions,
                brands = pin.brands,
                priceRange = pin.priceRange,
                whereToBuy = pin.whereToBuy ?: "",
                purchaseLink = pin.purchaseLink ?: "",
                colors = pin.colors,
                tags = pin.tags
            )
        }
    }

    fun toggleLike(pinId: String) {
        viewModelScope.launch {
            toggleLikeUseCase(pinId).onSuccess { status ->
                _uiState.update { state ->
                    val updatePin = { p: Pin ->
                        if (p.id == pinId) p.copy(isLikedByMe = status.isLiked, likesCount = status.likesCount)
                        else p
                    }
                    state.copy(
                        pinDetail = if (state.pinDetail?.id == pinId) updatePin(state.pinDetail) else state.pinDetail
                    )
                }
            }
        }
    }
    
    fun deletePin(id: String) {
        viewModelScope.launch {
            deletePinUseCase(id).fold(
                onSuccess = { success -> if (success) refreshPins() },
                onFailure = { }
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
}
