package com.ale.stylepin.features.explore.data.datasources.remote.mapper

import com.ale.stylepin.features.explore.data.datasources.remote.model.UserSearchDto
import com.ale.stylepin.features.explore.domain.entities.UserSearchResult

fun UserSearchDto.toDomain(): UserSearchResult = UserSearchResult(
    id = id,
    username = username,
    fullName = fullName.takeIf { !it.isNullOrBlank() } ?: username,
    avatarUrl = avatarUrl ?: "",
    isVerified = isVerified
)