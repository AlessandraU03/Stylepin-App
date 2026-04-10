package com.ale.stylepin.features.pins.domain.usecases

import com.ale.stylepin.features.pins.domain.entities.Pin
import com.ale.stylepin.features.pins.domain.repository.PinsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPinsUseCase @Inject constructor(
    private val repository: PinsRepository
) {
    /**
     * Devuelve el Flow de Room (SSOT)
     */
    fun executeFlow(): Flow<List<Pin>> = repository.getPinsFlow()

    /**
     * Ordena la actualización desde la red
     */
    suspend fun refresh() = repository.refreshPins()

    suspend operator fun invoke(): Result<List<Pin>> = runCatching {
        repository.getPins()
    }
}
