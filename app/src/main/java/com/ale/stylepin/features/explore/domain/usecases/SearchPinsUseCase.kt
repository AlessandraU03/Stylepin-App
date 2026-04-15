package com.ale.stylepin.features.explore.domain.usecases

import com.ale.stylepin.features.explore.domain.repository.ExploreRepository
import com.ale.stylepin.features.pins.domain.entities.Pin
import javax.inject.Inject

class SearchPinsUseCase @Inject constructor(
    private val repository: ExploreRepository
) {
    suspend fun execute(query: String): Result<List<Pin>> {
        return repository.searchPins(query)
    }
}