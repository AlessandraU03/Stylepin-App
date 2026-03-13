package com.ale.stylepin.features.boards.data.datasources.remote.model

import kotlinx.serialization.Serializable

// ── Responses ─────────────────────────────────────────────

@Serializable
data class BoardDto(
    val id: String,
    val user_id: String,
    val user_username: String,
    val user_full_name: String? = null,
    val user_avatar_url: String? = null,
    val name: String,
    val description: String? = null,
    val cover_image_url: String? = null,
    val is_private: Boolean = false,
    val is_collaborative: Boolean = false,
    val pins_count: Int = 0,
    val created_at: String,
    val updated_at: String? = null,
    val is_owner: Boolean = false,
    val is_collaborator: Boolean = false
)

@Serializable
data class BoardListResponse(
    val boards: List<BoardDto>,
    val total: Int,
    val limit: Int,
    val offset: Int,
    val has_more: Boolean
)

@Serializable
data class BoardPinResponse(
    val id: String,
    val board_id: String,
    val pin_id: String,
    val user_id: String,
    val notes: String? = null,
    val created_at: String
)

@Serializable
data class BoardPinListResponse(
    val pins: List<BoardPinResponse>,
    val total: Int,
    val limit: Int,
    val offset: Int,
    val has_more: Boolean
)

@Serializable
data class BoardCollaboratorResponse(
    val id: String,
    val board_id: String,
    val user_id: String,
    val user_username: String,
    val user_full_name: String,
    val user_avatar_url: String? = null,
    val can_edit: Boolean,
    val can_add_pins: Boolean,
    val can_remove_pins: Boolean,
    val created_at: String
)

@Serializable
data class CollaboratorListResponse(
    val collaborators: List<BoardCollaboratorResponse>
)

// ── Requests ──────────────────────────────────────────────

@Serializable
data class CreateBoardRequest(
    val name: String,
    val description: String? = null,
    val is_private: Boolean = false,
    val is_collaborative: Boolean = false
)

@Serializable
data class UpdateBoardRequest(
    val name: String,
    val description: String? = null,
    val is_private: Boolean = false,
    val is_collaborative: Boolean = false,
    val cover_image_url: String? = null
)

@Serializable
data class AddPinToBoardRequest(
    val pin_id: String,
    val notes: String? = null
)

@Serializable
data class AddCollaboratorRequest(
    val user_id: String,
    val can_edit: Boolean = false,
    val can_add_pins: Boolean = true,
    val can_remove_pins: Boolean = false
)

@Serializable
data class UpdateCollaboratorRequest(
    val can_edit: Boolean = false,
    val can_add_pins: Boolean = true,
    val can_remove_pins: Boolean = false
)
