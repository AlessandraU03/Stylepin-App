package com.ale.stylepin.features.community.domain.usecases

import com.ale.stylepin.features.community.domain.entities.CommunityUser
import com.ale.stylepin.features.community.domain.repositories.CommunityRepository
import javax.inject.Inject

class GetFollowingUseCase @Inject constructor(
    private val repository: CommunityRepository
) {
    suspend fun execute(userId: String): Result<List<CommunityUser>> {
        return repository.getFollowing(userId)
    }
}