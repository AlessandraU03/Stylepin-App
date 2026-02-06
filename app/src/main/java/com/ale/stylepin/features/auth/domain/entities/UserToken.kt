package com.ale.stylepin.features.auth.domain.entities

data class UserToken(
    val token: String,
    val username: String
)