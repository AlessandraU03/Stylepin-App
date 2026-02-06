package com.ale.stylepin.features.pins.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ale.stylepin.features.pins.domain.usecases.GetPinsUseCase
import com.ale.stylepin.features.pins.domain.usecases.DeletePinsUseCase // Importa el nuevo Use Case

class PinsViewModelFactory(
    private val getPinsUseCase: GetPinsUseCase,
    private val deletePinUseCase: DeletePinsUseCase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PinsViewModel::class.java)) {
            return PinsViewModel(getPinsUseCase, deletePinUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}