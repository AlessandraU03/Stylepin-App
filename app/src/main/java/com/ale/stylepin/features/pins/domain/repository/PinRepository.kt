package com.ale.stylepin.features.pins.domain.repository

import com.ale.stylepin.features.pins.domain.entities.Pin

interface PinsRepository {
    suspend fun getPins(): List<Pin>
    suspend fun addPin(title: String, imageUrl: String, category: String, season: String): Boolean
    suspend fun deletePin(pinId: String): Boolean // Nueva función para eliminar
}