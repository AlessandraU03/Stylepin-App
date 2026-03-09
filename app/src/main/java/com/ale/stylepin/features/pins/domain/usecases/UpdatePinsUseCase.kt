package com.ale.stylepin.features.pins.domain.usecases

import com.ale.stylepin.features.pins.domain.repository.PinsRepository

class UpdatePinsUseCase(private val repository: PinsRepository) {
    suspend fun execute(
        pinId: String,
        title: String,
        imageUrl: String,
        category: String,
        season: String
    ): Boolean {
        return repository.updatePin(pinId, title, imageUrl, category, season)
    }
}