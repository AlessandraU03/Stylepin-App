package com.ale.stylepin.features.pins.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ale.stylepin.features.pins.domain.usecases.DeletePinsUseCase
import com.ale.stylepin.features.pins.domain.usecases.GetPinsUseCase
import com.ale.stylepin.features.pins.presentation.screens.PinsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PinsViewModel(
    private val getPinsUseCase: GetPinsUseCase,
    private val deletePinUseCase: DeletePinsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PinsUiState())
    val uiState = _uiState.asStateFlow()

    init { fetchPins() }

    fun fetchPins() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val pins = getPinsUseCase.execute()
                _uiState.update {
                    it.copy(isLoading = false, pins = pins, filteredPins = pins)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message)
                }
            }
        }
    }

    fun filterBySeason(season: String) {
        _uiState.update { currentState ->
            val filtered = if (season == "todo_el_ano") {
                currentState.pins
            } else {
                currentState.pins.filter { it.season.equals(season, ignoreCase = true) }
            }
            currentState.copy(filteredPins = filtered, selectedSeason = season)
        }
    }

    fun deletePin(pinId: String) {
        viewModelScope.launch {
            val success = deletePinUseCase.execute(pinId)
            if (success) {
                fetchPins()
            } else {
                _uiState.update {
                    it.copy(error = "No se pudo eliminar el pin.")
                }
            }
        }
    }
}