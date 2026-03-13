package com.ale.stylepin.features.boards.domain.usecases

import com.ale.stylepin.features.boards.domain.entities.BoardCollaborator
import com.ale.stylepin.features.boards.domain.repository.BoardsRepository
import javax.inject.Inject

class AddCollaboratorUseCase @Inject constructor(
    private val repository: BoardsRepository
) {
    suspend operator fun invoke(
        boardId: String,
        userId: String,
        canEdit: Boolean = false,
        canAddPins: Boolean = true,
        canRemovePins: Boolean = false
    ): Result<BoardCollaborator> = runCatching {
        repository.addCollaborator(boardId, userId, canEdit, canAddPins, canRemovePins)
    }
}