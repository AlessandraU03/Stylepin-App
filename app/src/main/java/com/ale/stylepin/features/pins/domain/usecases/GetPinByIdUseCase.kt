package com.ale.stylepin.features.pins.domain.usecases

import com.ale.stylepin.features.pins.domain.entities.Pin
import com.ale.stylepin.features.pins.domain.repository.PinsRepository
import javax.inject.Inject

class GetPinByIdUseCase @Inject constructor(
    private val repository: PinsRepository
) {
    suspend operator fun invoke(pinId: String): Result<Pin> {
        return try {
            Result.success(repository.getPinById(pinId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}