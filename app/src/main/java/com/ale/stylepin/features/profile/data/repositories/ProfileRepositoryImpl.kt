package com.ale.stylepin.features.profile.data.repositories

import android.util.Log
import com.ale.stylepin.features.profile.data.datasources.remote.api.ProfileApi
import com.ale.stylepin.features.profile.data.datasources.remote.mapper.mapToDomain
import com.ale.stylepin.features.profile.data.datasources.remote.model.UserStatsDto
import com.ale.stylepin.features.profile.domain.entities.Profile
import com.ale.stylepin.features.profile.domain.repositories.ProfileRepository
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val api: ProfileApi
) : ProfileRepository {

    override suspend fun getMyProfile(): Profile {
        // 1. Obtenemos el perfil principal (Este rara vez falla)
        val user = api.getMyProfile()

        // 2. Obtenemos las estadísticas de forma SEGURA.
        // Si el backend lanza el Error 500 aquí, lo atrapamos y no rompemos la app.
        val stats = try {
            api.getMyStats()
        } catch (e: Exception) {
            Log.e("ProfileRepository", "El backend falló al traer las stats: ${e.message}")
            // Si el servidor falla, le decimos a la app que asuma que todo está en CERO.
            UserStatsDto(totalPins = 0, totalFollowers = 0, totalFollowing = 0)
        }

        return mapToDomain(user, stats)
    }
}