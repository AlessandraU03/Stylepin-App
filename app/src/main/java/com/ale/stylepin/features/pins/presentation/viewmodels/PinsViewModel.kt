package com.ale.stylepin.features.pins.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ale.stylepin.features.pins.domain.usecases.GetPinsUseCase
import com.ale.stylepin.features.pins.domain.usecases.DeletePinsUseCase
import com.ale.stylepin.features.pins.domain.entities.Pin
import kotlinx.coroutines.launch

// Estado de la UI
data class PinsUiState(
    val pins: List<Pin> = emptyList(),          // Todos los pines de EC2
    val filteredPins: List<Pin> = emptyList(),  // Los que realmente se ven
    val selectedSeason: String = "All",         // Temporada activa
    val isLoading: Boolean = false,
    val error: String? = null
)

class PinsViewModel(
    private val getPinsUseCase: GetPinsUseCase,
    private val deletePinUseCase: DeletePinsUseCase
) : ViewModel() {

    var uiState by mutableStateOf(PinsUiState())
        private set

    init {
        fetchPins()
    }

    fun fetchPins() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            try {
                val result = getPinsUseCase.execute()
                uiState = uiState.copy(
                    pins = result,
                    // Al cargar, aplicamos el filtro actual (por si estaba en 'Winter')
                    filteredPins = applyFilter(result, uiState.selectedSeason),
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, error = e.message)
            }
        }
    }

    // --- ESTA ES LA FUNCIÓN QUE LLAMAN LOS BOTONES ---
    fun filterBySeason(season: String) {
        uiState = uiState.copy(
            selectedSeason = season,
            filteredPins = applyFilter(uiState.pins, season)
        )
    }

    // Lógica interna de filtrado
    private fun applyFilter(list: List<Pin>, season: String): List<Pin> {
        return if (season == "All") {
            list
        } else {
            // Comparamos el campo 'season' del Pin con el botón presionado
            list.filter { it.season.equals(season, ignoreCase = true) }
        }
    }

    fun deletePin(pinId: String) {
        viewModelScope.launch {
            val success = deletePinUseCase.execute(pinId)
            if (success) {
                fetchPins() // Recargamos de EC2 tras borrar
            }
        }
    }
}