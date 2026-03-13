package com.ale.stylepin.features.boards.data.datasources.remote.mapper

import com.ale.stylepin.features.boards.data.datasources.remote.model.AddCollaboratorRequest
import com.ale.stylepin.features.boards.data.datasources.remote.model.AddPinToBoardRequest
import com.ale.stylepin.features.boards.data.datasources.remote.model.BoardCollaboratorResponse
import com.ale.stylepin.features.boards.data.datasources.remote.model.BoardDto
import com.ale.stylepin.features.boards.data.datasources.remote.model.BoardPinResponse
import com.ale.stylepin.features.boards.data.datasources.remote.model.CreateBoardRequest
import com.ale.stylepin.features.boards.data.datasources.remote.model.UpdateBoardRequest
import com.ale.stylepin.features.boards.domain.entities.Board
import com.ale.stylepin.features.boards.domain.entities.BoardCollaborator
import com.ale.stylepin.features.boards.domain.entities.BoardPin

// ── API Response → Domain ─────────────────────────────────────

fun BoardDto.toDomain(): Board = Board(
    id = id,
    userId = user_id,
    userUsername = user_username,
    userFullName = user_full_name,
    userAvatarUrl = user_avatar_url,
    name = name,
    description = description,
    coverImageUrl = cover_image_url,
    isPrivate = is_private,
    isCollaborative = is_collaborative,
    pinsCount = pins_count,
    createdAt = created_at,
    updatedAt = updated_at,
    isOwner = is_owner,
    isCollaborator = is_collaborator
)

fun BoardPinResponse.toDomain(): BoardPin = BoardPin(
    id = id,
    boardId = board_id,
    pinId = pin_id,
    userId = user_id,
    notes = notes,
    createdAt = created_at
)

fun BoardCollaboratorResponse.toDomain(): BoardCollaborator = BoardCollaborator(
    id = id,
    boardId = board_id,
    userId = user_id,
    userUsername = user_username,
    userFullName = user_full_name,
    userAvatarUrl = user_avatar_url,
    canEdit = can_edit,
    canAddPins = can_add_pins,
    canRemovePins = can_remove_pins,
    createdAt = created_at
)

// ── Domain params → Request models ───────────────────────────

fun toCreateBoardRequest(
    name: String,
    description: String?,
    isPrivate: Boolean,
    isCollaborative: Boolean
): CreateBoardRequest = CreateBoardRequest(
    name = name,
    description = description,
    is_private = isPrivate,
    is_collaborative = isCollaborative
)

fun toUpdateBoardRequest(
    name: String,
    description: String?,
    isPrivate: Boolean,
    isCollaborative: Boolean,
    coverImageUrl: String?
): UpdateBoardRequest = UpdateBoardRequest(
    name = name,
    description = description,
    is_private = isPrivate,
    is_collaborative = isCollaborative,
    cover_image_url = coverImageUrl
)

fun toAddPinRequest(pinId: String, notes: String?): AddPinToBoardRequest =
    AddPinToBoardRequest(pin_id = pinId, notes = notes)

fun toAddCollaboratorRequest(
    userId: String,
    canEdit: Boolean,
    canAddPins: Boolean,
    canRemovePins: Boolean
): AddCollaboratorRequest = AddCollaboratorRequest(
    user_id = userId,
    can_edit = canEdit,
    can_add_pins = canAddPins,
    can_remove_pins = canRemovePins
)