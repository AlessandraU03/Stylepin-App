package com.ale.stylepin.features.pins.domain.usecases

import com.ale.stylepin.features.pins.domain.repository.PinsRepository
import javax.inject.Inject

class AddPinsUseCase @Inject constructor(
    private val repository: PinsRepository
) {
    suspend operator fun invoke(
        title: String,
        imageUrl: String,
        category: String,
        season: String,
        description: String? = null,
        isPrivate: Boolean = false,
        styles: List<String> = emptyList(),
        occasions: List<String> = emptyList(),
        brands: List<String> = emptyList(),
        priceRange: String = "bajo_500",
        whereToBuy: String? = null,
        purchaseLink: String? = null,
        colors: List<String> = emptyList(),
        tags: List<String> = emptyList()
    ): Result<Boolean> = runCatching {
        repository.addPin(
            title, imageUrl, category, season, description, isPrivate,
            styles, occasions, brands, priceRange, whereToBuy, purchaseLink, colors, tags
        )
    }
}