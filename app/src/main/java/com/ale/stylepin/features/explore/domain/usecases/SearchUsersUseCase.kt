package com.ale.stylepin.features.explore.domain.usecases

import com.ale.stylepin.features.explore.domain.entities.UserSearchResult
import com.ale.stylepin.features.explore.domain.repository.ExploreRepository
import javax.inject.Inject

class SearchUsersUseCase @Inject constructor(
    private val repository: ExploreRepository
) {
    suspend fun execute(query: String): Result<List<UserSearchResult>> {
        return repository.searchUsers(query)
    }
}