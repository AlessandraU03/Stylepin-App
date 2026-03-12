package com.ale.stylepin.features.pins.domain.usecases

import com.ale.stylepin.features.pins.domain.entities.PinCreate
import com.ale.stylepin.features.pins.domain.repository.PinsRepository
import javax.inject.Inject

class AddPinsUseCase @Inject constructor(
    private val repository: PinsRepository
) {
    suspend operator fun invoke(pinCreate: PinCreate): Result<Boolean> {
        return try {
            Result.success(repository.addPin(pinCreate))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}