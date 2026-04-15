package com.ale.stylepin.features.community.domain.usecases

import com.ale.stylepin.features.community.domain.repositories.CommunityRepository
import javax.inject.Inject

class CheckFollowStatusUseCase @Inject constructor(
    private val repository: CommunityRepository
) {
    suspend fun execute(targetUserId: String): Result<Boolean> {
        return repository.checkFollowStatus(targetUserId)
    }
}