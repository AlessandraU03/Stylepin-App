package com.ale.stylepin.features.community.domain.usecases

import com.ale.stylepin.features.community.domain.repositories.CommunityRepository
import javax.inject.Inject

class ToggleFollowUseCase @Inject constructor(
    private val repository: CommunityRepository
) {
    suspend fun execute(targetUserId: String, isCurrentlyFollowing: Boolean): Result<Unit> {
        return repository.toggleFollow(targetUserId, isCurrentlyFollowing)
    }
}