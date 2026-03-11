package com.ale.stylepin.features.pins.domain.usecases

import com.ale.stylepin.features.pins.domain.repository.PinsRepository
import com.ale.stylepin.features.pins.domain.entities.Pin
import javax.inject.Inject

class GetPinsUseCase @Inject constructor(private val repository: PinsRepository) {
    suspend fun execute(): List<Pin> = repository.getPins()
}