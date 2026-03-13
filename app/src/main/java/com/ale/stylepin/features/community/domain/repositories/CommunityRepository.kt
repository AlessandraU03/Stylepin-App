package com.ale.stylepin.features.community.domain.repositories

import com.ale.stylepin.features.community.domain.entities.CommunityUser

interface CommunityRepository {
    suspend fun getFollowers(userId: String): Result<List<CommunityUser>>
    suspend fun getFollowing(userId: String): Result<List<CommunityUser>>
    suspend fun toggleFollow(targetUserId: String, isCurrentlyFollowing: Boolean): Result<Unit>
}