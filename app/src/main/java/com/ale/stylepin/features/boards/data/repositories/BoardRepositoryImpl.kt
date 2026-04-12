package com.ale.stylepin.features.boards.data.repositories

import android.util.Log
import com.ale.stylepin.features.boards.data.datasources.remote.api.BoardApi
import com.ale.stylepin.features.boards.data.datasources.remote.mapper.*
import com.ale.stylepin.features.boards.domain.entities.Board
import com.ale.stylepin.features.boards.domain.entities.BoardCollaborator
import com.ale.stylepin.features.boards.domain.entities.BoardPin
import com.ale.stylepin.features.boards.domain.repository.BoardsRepository
import javax.inject.Inject

class BoardRepositoryImpl @Inject constructor(
    private val api: BoardApi
) : BoardsRepository {

    override suspend fun getAllBoards(userId: String?): List<Board> {
        return try {
            val response = api.getAllBoards(userId)
            Log.d(TAG, "getAllBoards → code=${response.code()}, body=${response.body()}")
            val boards = response.body()?.boards?.map { it.toDomain() } ?: emptyList()
            Log.d(TAG, "getAllBoards → ${boards.size} tableros")
            boards
        } catch (e: Exception) {
            Log.e(TAG, "getAllBoards EXCEPTION: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun getUserBoards(userId: String): List<Board> {
        return try {
            val response = api.getUserBoards(userId)
            Log.d(TAG, "getUserBoards(userId=$userId) → code=${response.code()}, isSuccessful=${response.isSuccessful}")

            if (!response.isSuccessful) {
                Log.e(TAG, "getUserBoards ERROR: ${response.errorBody()?.string()}")
                return emptyList()
            }

            val body = response.body()
            Log.d(TAG, "getUserBoards → body=$body")

            val boards = body?.boards?.map { it.toDomain() } ?: run {
                Log.w(TAG, "getUserBoards → body o boards es null → emptyList")
                emptyList()
            }
            Log.d(TAG, "getUserBoards → ${boards.size} tableros encontrados")
            boards
        } catch (e: Exception) {
            Log.e(TAG, "getUserBoards EXCEPTION: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun getBoardById(boardId: String): Board {
        val response = api.getBoardById(boardId)
        return response.body()?.toDomain()
            ?: throw Exception("Error ${response.code()} al obtener el tablero")
    }

    override suspend fun createBoard(
        name: String, description: String?,
        isPrivate: Boolean, isCollaborative: Boolean
    ): Board {
        val response = api.createBoard(
            toCreateBoardRequest(name, description, isPrivate, isCollaborative)
        )
        Log.d(TAG, "createBoard → code=${response.code()}")
        if (!response.isSuccessful) {
            Log.e(TAG, "createBoard ERROR: ${response.errorBody()?.string()}")
        }
        return response.body()?.toDomain()
            ?: throw Exception("Error ${response.code()} al crear el tablero")
    }

    override suspend fun updateBoard(
        boardId: String, name: String, description: String?,
        isPrivate: Boolean, isCollaborative: Boolean, coverImageUrl: String?
    ): Board {
        val response = api.updateBoard(
            boardId,
            toUpdateBoardRequest(name, description, isPrivate, isCollaborative, coverImageUrl)
        )
        return response.body()?.toDomain()
            ?: throw Exception("Error ${response.code()} al actualizar el tablero")
    }

    override suspend fun deleteBoard(boardId: String): Boolean =
        api.deleteBoard(boardId).isSuccessful

    override suspend fun getBoardPins(boardId: String): List<BoardPin> {
        return try {
            api.getBoardPins(boardId).body()?.pins?.map { it.toDomain() } ?: emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "getBoardPins EXCEPTION: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun addPinToBoard(boardId: String, pinId: String, notes: String?): BoardPin {
        val response = api.addPinToBoard(boardId, toAddPinRequest(pinId, notes))
        return response.body()?.toDomain()
            ?: throw Exception("Error al agregar pin al tablero")
    }

    override suspend fun removePinFromBoard(boardId: String, pinId: String): Boolean =
        api.removePinFromBoard(boardId, pinId).isSuccessful

    override suspend fun getCollaborators(boardId: String): List<BoardCollaborator> =
        api.getCollaborators(boardId).body()?.collaborators?.map { it.toDomain() } ?: emptyList()

    override suspend fun addCollaborator(
        boardId: String, userId: String,
        canEdit: Boolean, canAddPins: Boolean, canRemovePins: Boolean
    ): BoardCollaborator {
        val response = api.addCollaborator(
            boardId,
            toAddCollaboratorRequest(userId, canEdit, canAddPins, canRemovePins)
        )
        return response.body()?.toDomain() ?: throw Exception("Error al agregar colaborador")
    }

    override suspend fun removeCollaborator(boardId: String, collaboratorUserId: String): Boolean =
        api.removeCollaborator(boardId, collaboratorUserId).isSuccessful

    override suspend fun updateCollaboratorPermissions(
        boardId: String, collaboratorUserId: String,
        canEdit: Boolean, canAddPins: Boolean, canRemovePins: Boolean
    ): BoardCollaborator {
        val response = api.updateCollaboratorPermissions(
            boardId,
            collaboratorUserId,
            toUpdateCollaboratorRequest(canEdit, canAddPins, canRemovePins)
        )
        return response.body()?.toDomain() ?: throw Exception("Error al actualizar permisos")
    }

    companion object {
        private const val TAG = "BoardRepository"
    }
}