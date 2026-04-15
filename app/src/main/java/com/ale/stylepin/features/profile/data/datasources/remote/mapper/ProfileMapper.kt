package com.ale.stylepin.features.profile.data.datasources.remote.mapper

import com.ale.stylepin.features.profile.data.datasources.remote.model.UserMeDto
import com.ale.stylepin.features.profile.data.datasources.remote.model.UserStatsDto
import com.ale.stylepin.features.profile.domain.entities.Profile

fun mapToDomain(user: UserMeDto, stats: UserStatsDto): Profile {
    return Profile(
        id = user.id ?: "",
        username = user.username ?: "usuario",
        email = user.email ?: "",
        fullName = user.fullName ?: user.username ?: "Usuario",
        bio = user.bio ?: "",
        avatarUrl = user.avatarUrl ?: "",
        gender = user.gender ?: "other",
        followersCount = stats.totalFollowers ?: 0,
        followingCount = stats.totalFollowing ?: 0,
        pinsCount = stats.totalPins ?: 0
    )
}