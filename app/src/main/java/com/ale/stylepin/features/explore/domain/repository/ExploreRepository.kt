package com.ale.stylepin.features.explore.domain.repository

import com.ale.stylepin.features.explore.domain.entities.UserSearchResult
import com.ale.stylepin.features.pins.domain.entities.Pin

interface ExploreRepository {
    suspend fun searchUsers(query: String): Result<List<UserSearchResult>>
    suspend fun searchPins(query: String): Result<List<Pin>>
}