package com.ale.stylepin.features.explore.data.repositories

import android.util.Log // <-- Importante para ver los logs
import com.ale.stylepin.features.explore.data.datasources.remote.api.ExploreApi
import com.ale.stylepin.features.explore.data.datasources.remote.mapper.toDomain
import com.ale.stylepin.features.explore.domain.entities.UserSearchResult
import com.ale.stylepin.features.explore.domain.repository.ExploreRepository
import com.ale.stylepin.features.pins.data.datasources.remote.mapper.toDomain
import com.ale.stylepin.features.pins.domain.entities.Pin
import javax.inject.Inject

class ExploreRepositoryImpl @Inject constructor(
    private val api: ExploreApi
) : ExploreRepository {

    override suspend fun searchUsers(query: String): Result<List<UserSearchResult>> {
        return try {
            val response = api.searchUsers(query)
            if (response.isSuccessful) {
                val users = response.body()?.users?.map { it.toDomain() } ?: emptyList()
                Log.d("BUSQUEDA", "Usuarios encontrados: ${users.size}")
                Result.success(users)
            } else {
                Log.e("BUSQUEDA", "Error API Usuarios: ${response.code()} - ${response.errorBody()?.string()}")
                Result.failure(Exception("Error en la búsqueda de usuarios"))
            }
        } catch (e: Exception) {
            Log.e("BUSQUEDA", "Crash al buscar usuarios: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun searchPins(query: String): Result<List<Pin>> {
        return try {
            val response = api.searchPins(query)
            if (response.isSuccessful) {
                val pins = response.body()?.pins?.map { it.toDomain() } ?: emptyList()
                Log.d("BUSQUEDA", "Pines encontrados: ${pins.size}")
                Result.success(pins)
            } else {
                Log.e("BUSQUEDA", "Error API Pines: ${response.code()} - ${response.errorBody()?.string()}")
                Result.failure(Exception("Error en la búsqueda de pines"))
            }
        } catch (e: Exception) {
            Log.e("BUSQUEDA", "Crash al buscar pines: ${e.message}", e)
            Result.failure(e)
        }
    }
}