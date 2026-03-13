package com.ale.stylepin.features.pins.domain.usecases

import com.ale.stylepin.features.pins.domain.entities.Pin
import com.ale.stylepin.features.pins.domain.repository.PinsRepository
import javax.inject.Inject

class GetPinsUseCase @Inject constructor(
    private val repository: PinsRepository
) {
    suspend operator fun invoke(): Result<List<Pin>> = runCatching {
        repository.getPins()
    }
}