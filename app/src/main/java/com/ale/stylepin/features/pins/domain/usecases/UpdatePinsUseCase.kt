package com.ale.stylepin.features.pins.domain.usecases

import com.ale.stylepin.features.pins.domain.repository.PinsRepository
import javax.inject.Inject

class UpdatePinsUseCase @Inject constructor(
    private val repository: PinsRepository
) {
    suspend operator fun invoke(
        pinId: String,
        title: String,
        imageUrl: String? = null,
        category: String,
        season: String,
        description: String? = null,
        isPrivate: Boolean = false
    ): Result<Boolean> = runCatching {
        repository.updatePin(pinId, title, imageUrl, category, season, description, isPrivate)
    }
}