package com.ale.stylepin.features.pins.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ale.stylepin.features.pins.domain.usecases.DeletePinsUseCase
import com.ale.stylepin.features.pins.domain.usecases.GetPinsUseCase
import com.ale.stylepin.features.pins.presentation.screens.PinsUiState
import kotlinx.coroutines.launch

class PinsViewModel(private val getPinsUseCase: GetPinsUseCase,private val deletePinUseCase: DeletePinsUseCase) : ViewModel() {
    var uiState by mutableStateOf(PinsUiState())
        private set

    init { fetchPins() }

    fun fetchPins() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            try {
                val pins = getPinsUseCase.execute()
                uiState = uiState.copy(isLoading = false, pins = pins, filteredPins = pins)
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun filterBySeason(season: String) {
        val filtered = if (season == "todo_el_ano") {
            uiState.pins
        } else {
            uiState.pins.filter { pin ->
                // Comparación directa de Strings (case-insensitive por seguridad)
                pin.season.equals(season, ignoreCase = true)
            }
        }
        uiState = uiState.copy(filteredPins = filtered, selectedSeason = season)
    }

    fun deletePin(pinId: String) {
        viewModelScope.launch {
            // Opcional: Podrías añadir un estado 'isDeleting' si quieres bloquear la UI
            val success = deletePinUseCase.execute(pinId)

            if (success) {
                // Si EC2 confirma el borrado (204), refrescamos la lista localmente
                fetchPins()
            } else {
                // Manejo de error (ej: el usuario no tiene permisos/403)
                uiState = uiState.copy(error = "No se pudo eliminar el pin. Verifica tus permisos.")
            }
        }
    }
}