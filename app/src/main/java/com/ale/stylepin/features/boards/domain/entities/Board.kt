package com.ale.stylepin.features.boards.domain.entities

data class Board(
    val id: String,
    val userId: String,
    val userUsername: String,
    val userFullName: String,
    val userAvatarUrl: String?,
    val name: String,
    val description: String?,
    val coverImageUrl: String?,
    val isPrivate: Boolean,
    val isCollaborative: Boolean,
    val pinsCount: Int,
    val createdAt: String,
    val updatedAt: String,
    val isOwner: Boolean,
    val isCollaborator: Boolean
)