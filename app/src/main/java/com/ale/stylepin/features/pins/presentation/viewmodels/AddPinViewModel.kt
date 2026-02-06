package com.ale.stylepin.features.pins.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ale.stylepin.features.pins.domain.usecases.AddPinsUseCase
import kotlinx.coroutines.launch

class AddPinViewModel(private val repository: AddPinsUseCase) : ViewModel() {
    var title by mutableStateOf("")
    var imageUrl by mutableStateOf("")
    var selectedCategory by mutableStateOf("outfit_completo")
    var selectedSeason by mutableStateOf("todo_el_ano")
    var isSaving by mutableStateOf(false)

    fun savePin(onSuccess: () -> Unit) {
        viewModelScope.launch {
            isSaving = true
            val success = repository.addPin(title, imageUrl, selectedCategory, selectedSeason)
            isSaving = false
            if (success) onSuccess()
        }
    }
}