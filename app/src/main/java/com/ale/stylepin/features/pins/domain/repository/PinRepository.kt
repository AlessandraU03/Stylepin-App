package com.ale.stylepin.features.pins.domain.repository

import com.ale.stylepin.features.pins.domain.entities.Pin
import kotlinx.coroutines.flow.Flow

interface PinsRepository {
    /**
     * Fuente de verdad única (SSOT). Emite la lista de pines guardada localmente
     * y se actualiza automáticamente cuando la base de datos cambia.
     */
    fun getPinsFlow(): Flow<List<Pin>>

    /**
     * Sincroniza los pines locales con el servidor.
     */
    suspend fun refreshPins(): Result<Unit>

    suspend fun getPins(): List<Pin>
    suspend fun getPinById(pinId: String): Pin
    suspend fun addPin(
        title: String,
        imageUrl: String,
        category: String,
        season: String,
        description: String?,
        isPrivate: Boolean,
        styles: List<String>,
        occasions: List<String>,
        brands: List<String>,
        priceRange: String,
        whereToBuy: String?,
        purchaseLink: String?,
        colors: List<String>,
        tags: List<String>
    ): Boolean
    suspend fun updatePin(
        pinId: String,
        title: String,
        imageUrl: String?,
        category: String,
        season: String,
        description: String?,
        isPrivate: Boolean
    ): Boolean
    suspend fun deletePin(pinId: String): Boolean
}
