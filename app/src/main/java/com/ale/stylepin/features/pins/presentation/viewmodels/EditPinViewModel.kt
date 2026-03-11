package com.ale.stylepin.features.pins.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ale.stylepin.features.pins.domain.usecases.UpdatePinsUseCase
import com.ale.stylepin.features.pins.presentation.screens.PinsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditPinViewModel @Inject constructor(
    private val updatePinsUseCase: UpdatePinsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(PinsUiState())
    val uiState = _uiState.asStateFlow()

    fun initData(title: String, imageUrl: String, category: String, season: String) {
        if (_uiState.value.title.isBlank()) {
            _uiState.update {
                it.copy(title = title, imageUrl = imageUrl, selectedCategory = category, selectedSeason = season)
            }
        }
    }

    fun onTitleChange(newTitle: String) = _uiState.update { it.copy(title = newTitle) }
    fun onImageUrlChange(newUrl: String) = _uiState.update { it.copy(imageUrl = newUrl) }

    fun updatePin(pinId: String, onSuccess: () -> Unit) {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val state = _uiState.value
                val success = updatePinsUseCase.execute(
                    pinId = pinId,
                    title = state.title,
                    imageUrl = state.imageUrl,
                    category = state.selectedCategory,
                    season = state.selectedSeason
                )
                _uiState.update { it.copy(isLoading = false) }
                if (success) onSuccess() else _uiState.update { it.copy(error = "No se pudo actualizar.") }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}