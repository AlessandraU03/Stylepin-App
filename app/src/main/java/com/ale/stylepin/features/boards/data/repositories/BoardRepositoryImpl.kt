package com.ale.stylepin.features.boards.data.repositories

import android.util.Log
import com.ale.stylepin.features.boards.data.datasources.remote.api.BoardApi
import com.ale.stylepin.features.boards.data.datasources.remote.mapper.toAddCollaboratorRequest
import com.ale.stylepin.features.boards.data.datasources.remote.mapper.toAddPinRequest
import com.ale.stylepin.features.boards.data.datasources.remote.mapper.toCreateBoardRequest
import com.ale.stylepin.features.boards.data.datasources.remote.mapper.toDomain
import com.ale.stylepin.features.boards.data.datasources.remote.mapper.toUpdateBoardRequest
import com.ale.stylepin.features.boards.domain.entities.Board
import com.ale.stylepin.features.boards.domain.entities.BoardCollaborator
import com.ale.stylepin.features.boards.domain.entities.BoardPin
import com.ale.stylepin.features.boards.domain.repository.BoardsRepository
import javax.inject.Inject

class BoardRepositoryImpl @Inject constructor(
    private val api: BoardApi
) : BoardsRepository {

    override suspend fun getUserBoards(userId: String): List<Board> {
        return api.getUserBoards(userId).body()?.boards?.map { it.toDomain() } ?: emptyList()
    }

    override suspend fun getBoardById(boardId: String): Board {
        val response = api.getBoardById(boardId)
        return response.body()?.toDomain()
            ?: throw Exception("Error ${response.code()} al obtener el tablero")
    }

    override suspend fun createBoard(
        name: String,
        description: String?,
        isPrivate: Boolean,
        isCollaborative: Boolean
    ): Board {
        val response = api.createBoard(toCreateBoardRequest(name, description, isPrivate, isCollaborative))
        if (!response.isSuccessful) Log.e(TAG, "createBoard error ${response.code()}: ${response.errorBody()?.string()}")
        return response.body()?.toDomain()
            ?: throw Exception("Error ${response.code()} al crear el tablero")
    }

    override suspend fun updateBoard(
        boardId: String,
        name: String,
        description: String?,
        isPrivate: Boolean,
        isCollaborative: Boolean,
        coverImageUrl: String?
    ): Board {
        val response = api.updateBoard(boardId, toUpdateBoardRequest(name, description, isPrivate, isCollaborative, coverImageUrl))
        if (!response.isSuccessful) Log.e(TAG, "updateBoard error ${response.code()}: ${response.errorBody()?.string()}")
        return response.body()?.toDomain()
            ?: throw Exception("Error ${response.code()} al actualizar el tablero")
    }

    override suspend fun deleteBoard(boardId: String): Boolean =
        api.deleteBoard(boardId).isSuccessful

    override suspend fun getBoardPins(boardId: String): List<BoardPin> {
        return api.getBoardPins(boardId).body()?.pins?.map { it.toDomain() } ?: emptyList()
    }

    override suspend fun addPinToBoard(boardId: String, pinId: String, notes: String?): BoardPin {
        val response = api.addPinToBoard(boardId, toAddPinRequest(pinId, notes))
        return response.body()?.toDomain()
            ?: throw Exception("Error ${response.code()} al agregar pin al tablero")
    }

    override suspend fun removePinFromBoard(boardId: String, pinId: String): Boolean =
        api.removePinFromBoard(boardId, pinId).isSuccessful

    override suspend fun getCollaborators(boardId: String): List<BoardCollaborator> {
        return api.getCollaborators(boardId).body()?.collaborators?.map { it.toDomain() } ?: emptyList()
    }

    override suspend fun addCollaborator(
        boardId: String,
        userId: String,
        canEdit: Boolean,
        canAddPins: Boolean,
        canRemovePins: Boolean
    ): BoardCollaborator {
        val response = api.addCollaborator(boardId, toAddCollaboratorRequest(userId, canEdit, canAddPins, canRemovePins))
        return response.body()?.toDomain()
            ?: throw Exception("Error ${response.code()} al agregar colaborador")
    }

    override suspend fun removeCollaborator(boardId: String, collaboratorUserId: String): Boolean =
        api.removeCollaborator(boardId, collaboratorUserId).isSuccessful

    companion object {
        private const val TAG = "BoardRepositoryImpl"
    }
}