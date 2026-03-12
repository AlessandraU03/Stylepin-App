package com.ale.stylepin.features.pins.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ale.stylepin.features.pins.domain.entities.Pin
import com.ale.stylepin.features.pins.domain.entities.PinCreate
import com.ale.stylepin.features.pins.domain.entities.PinUpdate
import com.ale.stylepin.features.pins.domain.usecases.AddPinsUseCase
import com.ale.stylepin.features.pins.domain.usecases.DeletePinsUseCase
import com.ale.stylepin.features.pins.domain.usecases.GetPinByIdUseCase
import com.ale.stylepin.features.pins.domain.usecases.GetPinsUseCase
import com.ale.stylepin.features.pins.domain.usecases.UpdatePinsUseCase
import com.ale.stylepin.features.pins.presentation.screens.PinsUiState
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
    private val addPinsUseCase: AddPinsUseCase,
    private val updatePinsUseCase: UpdatePinsUseCase,
    private val deletePinsUseCase: DeletePinsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PinsUiState())
    val uiState: StateFlow<PinsUiState> = _uiState.asStateFlow()

    init {
        loadPins()
    }

    // ─────────────────────────────────────────────────────────
    // Lista
    // ─────────────────────────────────────────────────────────

    fun loadPins() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            getPinsUseCase().fold(
                onSuccess = { pins ->
                    _uiState.update { it.copy(isLoading = false, pins = pins, filteredPins = pins) }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
            )
        }
    }

    // ─────────────────────────────────────────────────────────
    // Detalle — carga el pin completo por ID desde la API y
    // rellena automáticamente el formulario de edición.
    // ─────────────────────────────────────────────────────────

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

    // Rellena el formulario con todos los campos del pin,
    // incluyendo descripción, estilos, colores, etc.
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

    // ─────────────────────────────────────────────────────────
    // Guardado (crear o actualizar)
    // ─────────────────────────────────────────────────────────

    fun savePin(pinId: String? = null, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val s = _uiState.value
            _uiState.update { it.copy(isLoading = true, error = null) }

            if (pinId == null) {
                val pinCreate = PinCreate(
                    title = s.title,
                    imageUrl = s.imageUrl,
                    category = s.selectedCategory,
                    season = s.selectedSeason,
                    description = s.description.takeIf { it.isNotBlank() },
                    isPrivate = s.isPrivate,
                    styles = s.styles,
                    occasions = s.occasions,
                    brands = s.brands,
                    priceRange = s.priceRange,
                    whereToBuy = s.whereToBuy.takeIf { it.isNotBlank() },
                    purchaseLink = s.purchaseLink.takeIf { it.isNotBlank() },
                    colors = s.colors,
                    tags = s.tags
                )
                addPinsUseCase(pinCreate).fold(
                    onSuccess = { success ->
                        if (success) resetAndReload(onSuccess)
                        else _uiState.update { it.copy(isLoading = false, error = "Error al crear el pin") }
                    },
                    onFailure = { e ->
                        _uiState.update { it.copy(isLoading = false, error = e.message) }
                    }
                )
            } else {
                val pinUpdate = PinUpdate(
                    pinId = pinId,
                    title = s.title,
                    imageUrl = s.imageUrl.takeIf { it.isNotBlank() },
                    category = s.selectedCategory,
                    season = s.selectedSeason,
                    description = s.description.takeIf { it.isNotBlank() },
                    isPrivate = s.isPrivate
                )
                updatePinsUseCase(pinUpdate).fold(
                    onSuccess = { success ->
                        if (success) resetAndReload(onSuccess)
                        else _uiState.update { it.copy(isLoading = false, error = "Error al actualizar el pin") }
                    },
                    onFailure = { e ->
                        _uiState.update { it.copy(isLoading = false, error = e.message) }
                    }
                )
            }
        }
    }

    // ─────────────────────────────────────────────────────────
    // Eliminación
    // ─────────────────────────────────────────────────────────

    fun deletePin(id: String) {
        viewModelScope.launch {
            deletePinsUseCase(id).fold(
                onSuccess = { success ->
                    if (success) loadPins()
                    else _uiState.update { it.copy(error = "No se pudo eliminar el pin") }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(error = e.message) }
                }
            )
        }
    }

    // ─────────────────────────────────────────────────────────
    // Eventos del formulario
    // ─────────────────────────────────────────────────────────

    fun onFormEvent(event: PinFormEvent) {
        _uiState.update { state ->
            when (event) {
                is PinFormEvent.TitleChanged        -> state.copy(title = event.value)
                is PinFormEvent.DescriptionChanged  -> state.copy(description = event.value)
                is PinFormEvent.ImageUrlChanged     -> state.copy(imageUrl = event.value)
                is PinFormEvent.CategoryChanged     -> state.copy(selectedCategory = event.value)
                is PinFormEvent.SeasonChanged       -> state.copy(selectedSeason = event.value)
                is PinFormEvent.PriceRangeChanged   -> state.copy(priceRange = event.value)
                is PinFormEvent.WhereToBuyChanged   -> state.copy(whereToBuy = event.value)
                is PinFormEvent.PurchaseLinkChanged -> state.copy(purchaseLink = event.value)
                is PinFormEvent.IsPrivateChanged    -> state.copy(isPrivate = event.value)
                is PinFormEvent.StylesChanged       -> state.copy(styles = event.value)
                is PinFormEvent.OccasionsChanged    -> state.copy(occasions = event.value)
                is PinFormEvent.BrandsChanged       -> state.copy(brands = event.value)
                is PinFormEvent.ColorsChanged       -> state.copy(colors = event.value)
                is PinFormEvent.TagsChanged         -> state.copy(tags = event.value)
            }
        }
    }

    // ─────────────────────────────────────────────────────────
    // Utilidades
    // ─────────────────────────────────────────────────────────

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun resetAndReload(onSuccess: () -> Unit) {
        _uiState.value = PinsUiState()
        onSuccess()
        loadPins()
    }
}