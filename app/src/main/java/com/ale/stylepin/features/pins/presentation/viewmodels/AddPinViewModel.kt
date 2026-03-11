package com.ale.stylepin.features.pins.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ale.stylepin.features.pins.domain.usecases.AddPinsUseCase
import com.ale.stylepin.features.pins.presentation.screens.PinsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddPinViewModel @Inject constructor(
    private val addPinsUseCase: AddPinsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PinsUiState())
    val uiState = _uiState.asStateFlow()

    fun onTitleChange(newTitle: String) = _uiState.update { it.copy(title = newTitle) }
    fun onImageUrlChange(newUrl: String) = _uiState.update { it.copy(imageUrl = newUrl) }
    fun onCategoryChange(newCategory: String) = _uiState.update { it.copy(selectedCategory = newCategory) }
    fun onSeasonChange(newSeason: String) = _uiState.update { it.copy(selectedSeason = newSeason) }

    fun savePin(onSuccess: () -> Unit) {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val currentState = _uiState.value
                val success = addPinsUseCase.addPin(
                    title = currentState.title,
                    imageUrl = currentState.imageUrl,
                    category = currentState.selectedCategory,
                    season = currentState.selectedSeason
                )

                _uiState.update { it.copy(isLoading = false) }

                if (success) {
                    onSuccess()
                    resetState()
                } else {
                    _uiState.update { it.copy(error = "No se pudo guardar el pin.") }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "Error desconocido")
                }
            }
        }
    }

    private fun resetState() {
        _uiState.update { PinsUiState() }
    }
}