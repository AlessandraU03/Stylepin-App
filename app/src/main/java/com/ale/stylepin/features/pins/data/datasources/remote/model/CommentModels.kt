package com.ale.stylepin.features.pins.data.datasources.remote.model

data class CommentDto(
    val id: String,
    val pin_id: String,
    val user_id: String,
    val user_username: String,
    val user_full_name: String,
    val user_avatar_url: String?,
    val text: String,
    val created_at: String
)

data class CommentListResponse(val comments: List<CommentDto>)
data class CreateCommentRequest(val pin_id: String, val text: String)