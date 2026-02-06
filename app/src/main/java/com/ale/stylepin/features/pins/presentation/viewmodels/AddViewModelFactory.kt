package com.ale.stylepin.features.pins.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ale.stylepin.features.pins.domain.usecases.AddPinsUseCase

class AddPinViewModelFactory(
    private val addPinsUseCase: AddPinsUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Verificamos que la clase que se intenta crear sea AddPinViewModel
        if (modelClass.isAssignableFrom(AddPinViewModel::class.java)) {
            return AddPinViewModel(addPinsUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}