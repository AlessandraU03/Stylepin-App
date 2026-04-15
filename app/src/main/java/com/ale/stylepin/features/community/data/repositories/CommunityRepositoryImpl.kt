// com/ale/stylepin/features/community/data/repositories/CommunityRepositoryImpl.kt
package com.ale.stylepin.features.community.data.repositories

import com.ale.stylepin.features.community.data.datasources.remote.api.CommunityApi
import com.ale.stylepin.features.community.data.datasources.remote.mapper.toDomain
import com.ale.stylepin.features.community.data.datasources.remote.model.FollowUserRequest
import com.ale.stylepin.features.community.domain.entities.CommunityUser
import com.ale.stylepin.features.community.domain.repositories.CommunityRepository
import javax.inject.Inject

class CommunityRepositoryImpl @Inject constructor(
    private val api: CommunityApi
) : CommunityRepository {

    override suspend fun getFollowers(userId: String): Result<List<CommunityUser>> {
        return try {
            val response = api.getFollowers(userId)
            Result.success(response.followers.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getFollowing(userId: String): Result<List<CommunityUser>> {
        return try {
            val response = api.getFollowing(userId)
            Result.success(response.following.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun toggleFollow(targetUserId: String, isCurrentlyFollowing: Boolean): Result<Unit> {
        return try {
            if (isCurrentlyFollowing) {
                api.unfollowUser(targetUserId)
            } else {
                api.followUser(FollowUserRequest(userId = targetUserId))
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun checkFollowStatus(targetUserId: String): Result<Boolean> {
        return try {
            val response = api.getFollowStatus(targetUserId)
            if (response.isSuccessful) {
                Result.success(response.body()?.is_following == true)
            } else {
                Result.failure(Exception("Error al checar status"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}