package com.ale.stylepin.features.pins.domain.usecases

import com.ale.stylepin.features.pins.domain.repository.PinsRepository
import javax.inject.Inject

class DeletePinsUseCase @Inject constructor(private val repository: PinsRepository) {
    suspend fun execute(pinId: String): Boolean {
        return repository.deletePin(pinId)
    }
}