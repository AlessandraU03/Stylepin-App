package com.ale.stylepin.features.community.data.datasources.remote.mapper

import com.ale.stylepin.features.community.data.datasources.remote.model.FollowerProfileDto
import com.ale.stylepin.features.community.data.datasources.remote.model.FollowingProfileDto
import com.ale.stylepin.features.community.domain.entities.CommunityUser

fun FollowerProfileDto.toDomain(): CommunityUser {
    val validName = this.fullName.takeIf { !it.isNullOrBlank() } ?: this.username
    return CommunityUser(
        id = this.userId,
        username = this.username,
        fullName = validName,
        avatarUrl = this.avatarUrl ?: "",
        isVerified = this.isVerified,
        isFollowing = this.isFollowingBack
    )
}

fun FollowingProfileDto.toDomain(): CommunityUser {
    val validName = this.fullName.takeIf { !it.isNullOrBlank() } ?: this.username
    return CommunityUser(
        id = this.userId,
        username = this.username,
        fullName = validName,
        avatarUrl = this.avatarUrl ?: "",
        isVerified = this.isVerified,
        isFollowing = this.isFollowedByMe
    )
}