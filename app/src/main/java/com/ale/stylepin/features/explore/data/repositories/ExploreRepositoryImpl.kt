package com.ale.stylepin.features.explore.data.repositories

import android.util.Log
import com.ale.stylepin.features.explore.data.datasources.remote.api.ExploreApi
import com.ale.stylepin.features.explore.data.datasources.remote.mapper.toDomain
import com.ale.stylepin.features.explore.data.datasources.remote.mapper.toDomainPin
import com.ale.stylepin.features.explore.domain.entities.UserSearchResult
import com.ale.stylepin.features.explore.domain.repository.ExploreRepository
import com.ale.stylepin.features.pins.domain.entities.Pin
import javax.inject.Inject

class ExploreRepositoryImpl @Inject constructor(
    private val api: ExploreApi
) : ExploreRepository {

    override suspend fun searchUsers(query: String): Result<List<UserSearchResult>> {
        return try {
            val response = api.searchUsers(query)
            if (response.isSuccessful) {
                // Sacamos los usuarios de la propiedad "users" del JSON
                val usersList = response.body()?.users ?: emptyList()
                val mappedUsers = usersList.map { it.toDomain() }
                Log.d("BUSQUEDA", "Usuarios encontrados: ${mappedUsers.size}")
                Result.success(mappedUsers)
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
                // Sacamos los pines de la propiedad "pins" del JSON
                val pinsList = response.body()?.pins ?: emptyList()
                val mappedPins = pinsList.map { it.toDomainPin() }
                Log.d("BUSQUEDA", "Pines encontrados: ${mappedPins.size}")
                Result.success(mappedPins)
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