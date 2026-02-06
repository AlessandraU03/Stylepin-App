package com.ale.stylepin.features.pins.domain.usecases

import com.ale.stylepin.features.pins.domain.repository.PinsRepository

class AddPinsUseCase(private val repository: PinsRepository) {
    // Cambiamos el nombre de la función para que el ViewModel la encuentre
    suspend fun addPin(
        title: String,
        imageUrl: String,
        category: String,
        season: String
    ): Boolean = repository.addPin(title, imageUrl, category, season)
}