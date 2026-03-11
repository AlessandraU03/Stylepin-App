package com.ale.stylepin.features.pins.domain.usecases

import com.ale.stylepin.features.pins.domain.repository.PinsRepository
import javax.inject.Inject

class AddPinsUseCase @Inject constructor(private val repository: PinsRepository) {
    suspend fun addPin(title: String, imageUrl: String, category: String, season: String): Boolean {
        return repository.addPin(title, imageUrl, category, season)
    }
}