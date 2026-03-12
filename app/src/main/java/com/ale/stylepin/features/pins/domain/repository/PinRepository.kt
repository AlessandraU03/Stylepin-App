package com.ale.stylepin.features.pins.domain.repository

import com.ale.stylepin.features.pins.domain.entities.Pin
import com.ale.stylepin.features.pins.domain.entities.PinCreate
import com.ale.stylepin.features.pins.domain.entities.PinUpdate

interface PinsRepository {
    suspend fun getPins(): List<Pin>
    suspend fun getPinById(pinId: String): Pin
    suspend fun addPin(pinCreate: PinCreate): Boolean
    suspend fun updatePin(pinUpdate: PinUpdate): Boolean
    suspend fun deletePin(pinId: String): Boolean
}