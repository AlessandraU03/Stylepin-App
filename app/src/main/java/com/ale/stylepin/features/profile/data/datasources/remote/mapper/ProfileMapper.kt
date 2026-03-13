package com.ale.stylepin.features.profile.data.datasources.remote.mapper

import com.ale.stylepin.features.profile.data.datasources.remote.model.UserMeDto
import com.ale.stylepin.features.profile.data.datasources.remote.model.UserStatsDto
import com.ale.stylepin.features.profile.domain.entities.Profile

fun mapToDomain(user: UserMeDto, stats: UserStatsDto): Profile {
    return Profile(
        id = user.id,
        username = user.username,
        email = user.email,
        fullName = user.fullName,
        bio = user.bio ?: "",
        avatarUrl = user.avatarUrl ?: "",
        gender = user.gender ?: "other", // Valor por defecto seguro
        followersCount = stats.totalFollowers,
        followingCount = stats.totalFollowing,
        pinsCount = stats.totalPins
    )
}