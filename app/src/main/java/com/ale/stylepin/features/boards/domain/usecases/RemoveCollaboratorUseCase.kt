package com.ale.stylepin.features.boards.domain.usecases

import com.ale.stylepin.features.boards.domain.repository.BoardsRepository
import javax.inject.Inject

class RemoveCollaboratorUseCase @Inject constructor(
    private val repository: BoardsRepository
) {
    suspend operator fun invoke(
        boardId: String,
        collaboratorUserId: String
    ): Result<Boolean> = runCatching {
        repository.removeCollaborator(boardId, collaboratorUserId)
    }
}