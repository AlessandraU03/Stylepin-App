// com/ale/stylepin/features/community/data/datasources/remote/mapper/CommunityMapper.kt
package com.ale.stylepin.features.community.data.datasources.remote.mapper

import com.ale.stylepin.features.community.data.datasources.remote.model.FollowerProfileDto
import com.ale.stylepin.features.community.data.datasources.remote.model.FollowingProfileDto
import com.ale.stylepin.features.community.domain.entities.CommunityUser

fun FollowerProfileDto.toDomain(): CommunityUser {
    return CommunityUser(
        id = this.userId,
        username = this.username,
        fullName = this.fullName,
        avatarUrl = this.avatarUrl ?: "",
        isVerified = this.isVerified,
        isFollowing = this.isFollowingBack
    )
}

fun FollowingProfileDto.toDomain(): CommunityUser {
    return CommunityUser(
        id = this.userId,
        username = this.username,
        fullName = this.fullName,
        avatarUrl = this.avatarUrl ?: "",
        isVerified = this.isVerified,
        isFollowing = this.isFollowedByMe
    )
}