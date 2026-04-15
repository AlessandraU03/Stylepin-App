package com.ale.stylepin.features.pins.domain.usecases

import com.ale.stylepin.features.pins.domain.repository.PinsRepository
import javax.inject.Inject

class DeletePinsUseCase @Inject constructor(
    private val repository: PinsRepository
) {
    suspend operator fun invoke(pinId: String): Result<Boolean> = runCatching {
        repository.deletePin(pinId)
    }
}