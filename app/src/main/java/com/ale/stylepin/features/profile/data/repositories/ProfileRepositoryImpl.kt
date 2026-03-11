package com.ale.stylepin.features.profile.data.repositories

import com.ale.stylepin.features.profile.data.datasources.remote.api.ProfileApi
import com.ale.stylepin.features.profile.data.datasources.remote.mapper.mapToDomain
import com.ale.stylepin.features.profile.domain.entities.Profile
import com.ale.stylepin.features.profile.domain.repositories.ProfileRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val api: ProfileApi
) : ProfileRepository {

    override suspend fun getMyProfile(): Profile = coroutineScope {
        val userDeferred = async { api.getMyProfile() }
        val statsDeferred = async { api.getMyStats() }

        val user = userDeferred.await()
        val stats = statsDeferred.await()

        return@coroutineScope mapToDomain(user, stats)
    }
}