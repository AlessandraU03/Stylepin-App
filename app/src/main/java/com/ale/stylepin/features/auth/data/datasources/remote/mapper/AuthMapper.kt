package com.ale.stylepin.features.auth.data.datasources.remote.mapper

import com.ale.stylepin.features.auth.data.datasources.remote.model.AuthResponse
import com.ale.stylepin.features.auth.domain.entities.UserToken

fun AuthResponse.toDomain(): UserToken {
    return UserToken(
        token = this.token,
        username = this.user.username
    )
}