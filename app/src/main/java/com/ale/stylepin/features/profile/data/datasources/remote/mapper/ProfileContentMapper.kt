package com.ale.stylepin.features.profile.data.datasources.remote.mapper

import com.ale.stylepin.features.profile.data.datasources.remote.model.ProfileBoardDto
import com.ale.stylepin.features.profile.data.datasources.remote.model.ProfileLikeDto
import com.ale.stylepin.features.profile.data.datasources.remote.model.ProfilePinDto
import com.ale.stylepin.features.profile.domain.entities.ProfileBoard
import com.ale.stylepin.features.profile.domain.entities.ProfilePin
import com.ale.stylepin.features.profile.domain.entities.ProfileSavedPin

fun ProfilePinDto.toDomain() = ProfilePin(
    id = id,
    imageUrl = imageUrl
)

fun ProfileBoardDto.toDomain() = ProfileBoard(
    id = id,
    name = name,
    coverImageUrl = coverImageUrl ?: "",
    pinsCount = pinsCount
)

fun ProfileLikeDto.toDomain() = ProfileSavedPin(
    pinId = pinId,
    imageUrl = imageUrl ?: ""
)