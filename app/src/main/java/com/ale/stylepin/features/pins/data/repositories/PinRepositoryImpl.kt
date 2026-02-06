package com.ale.stylepin.features.pins.data.repositories

import android.util.Log
import com.ale.stylepin.core.network.StylePinApi
import com.ale.stylepin.features.pins.data.datasources.remote.mapper.toDomain
import com.ale.stylepin.features.pins.data.datasources.remote.model.AddPinRequest
import com.ale.stylepin.features.pins.domain.entities.Pin
import com.ale.stylepin.features.pins.domain.repository.PinsRepository

class PinRepositoryImpl(private val api: StylePinApi) : PinsRepository {
    override suspend fun getPins(): List<Pin> {
        // Al corregir la interfaz StylePinApi (añadiendo getPins), el error de 'it' desaparece
        return api.getPins().map { it.toDomain() }
    }

    override suspend fun addPin(title: String, imageUrl: String, category: String, season: String): Boolean {
        return try {
            val request = AddPinRequest(
                title = title,
                image_url = imageUrl,
                description = "",
                category = category,
                season = season
            )
            api.addPin(request)
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun deletePin(pinId: String): Boolean {
        return try {
            val response = api.deletePin(pinId)
            // El código 204 No Content es el estándar para eliminaciones exitosas
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("DELETE_ERROR", "Error al eliminar pin: ${e.message}")
            false
        }
    }

}

