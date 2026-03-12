package com.ale.stylepin.features.pins.domain.usecases

import com.ale.stylepin.features.pins.domain.entities.PinUpdate
import com.ale.stylepin.features.pins.domain.repository.PinsRepository
import javax.inject.Inject

class UpdatePinsUseCase @Inject constructor(
    private val repository: PinsRepository
) {
    suspend operator fun invoke(pinUpdate: PinUpdate): Result<Boolean> {
        return try {
            Result.success(repository.updatePin(pinUpdate))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}