package com.ale.stylepin.features.pins.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ale.stylepin.features.pins.domain.usecases.UpdatePinsUseCase

class EditPinViewModelFactory(private val updatePinsUseCase: UpdatePinsUseCase) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EditPinViewModel(updatePinsUseCase) as T
    }
}