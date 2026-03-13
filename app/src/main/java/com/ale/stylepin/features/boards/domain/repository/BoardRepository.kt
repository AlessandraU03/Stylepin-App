package com.ale.stylepin.features.boards.domain.repository

import com.ale.stylepin.features.boards.domain.entities.Board
import com.ale.stylepin.features.boards.domain.entities.BoardCollaborator
import com.ale.stylepin.features.boards.domain.entities.BoardPin

interface BoardsRepository {

    suspend fun getUserBoards(userId: String): List<Board>

    suspend fun getBoardById(boardId: String): Board

    suspend fun createBoard(
        name: String,
        description: String?,
        isPrivate: Boolean,
        isCollaborative: Boolean
    ): Board

    suspend fun updateBoard(
        boardId: String,
        name: String,
        description: String?,
        isPrivate: Boolean,
        isCollaborative: Boolean,
        coverImageUrl: String?
    ): Board

    suspend fun deleteBoard(boardId: String): Boolean

    suspend fun getBoardPins(boardId: String): List<BoardPin>

    suspend fun addPinToBoard(boardId: String, pinId: String, notes: String?): BoardPin

    suspend fun removePinFromBoard(boardId: String, pinId: String): Boolean

    suspend fun getCollaborators(boardId: String): List<BoardCollaborator>

    suspend fun addCollaborator(
        boardId: String,
        userId: String,
        canEdit: Boolean,
        canAddPins: Boolean,
        canRemovePins: Boolean
    ): BoardCollaborator

    suspend fun removeCollaborator(boardId: String, collaboratorUserId: String): Boolean
}