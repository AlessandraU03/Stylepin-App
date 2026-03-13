package com.ale.stylepin.features.pins.domain.repository

import com.ale.stylepin.features.pins.domain.entities.Comment
import com.ale.stylepin.features.pins.domain.entities.Pin
import kotlinx.coroutines.flow.Flow

interface PinsRepository {
    fun getPinsFlow(): Flow<List<Pin>>
    suspend fun refreshPins(): Result<Unit>
    suspend fun getPins(): List<Pin>
    suspend fun getPinById(pinId: String): Pin
    suspend fun addPin(
        title: String, imageUrl: String, category: String, season: String,
        description: String?, isPrivate: Boolean, styles: List<String>, occasions: List<String>,
        brands: List<String>, priceRange: String, whereToBuy: String?, purchaseLink: String?,
        colors: List<String>, tags: List<String>
    ): Boolean
    suspend fun updatePin(pinId: String, title: String, imageUrl: String?, category: String, season: String, description: String?, isPrivate: Boolean): Boolean
    suspend fun deletePin(pinId: String): Boolean

    // --- COMENTARIOS ---
    suspend fun getComments(pinId: String): List<Comment>
    suspend fun addComment(pinId: String, text: String): Boolean
}