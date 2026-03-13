package com.ale.stylepin.features.boards.domain.usecases

import com.ale.stylepin.features.boards.domain.entities.BoardCollaborator
import com.ale.stylepin.features.boards.domain.repository.BoardsRepository
import javax.inject.Inject

class UpdateCollaboratorPermissionsUseCase @Inject constructor(
    private val repository: BoardsRepository
) {
    suspend operator fun invoke(
        boardId: String,
        collaboratorUserId: String,
        canEdit: Boolean,
        canAddPins: Boolean,
        canRemovePins: Boolean
    ): Result<BoardCollaborator> = runCatching {
        repository.updateCollaboratorPermissions(boardId, collaboratorUserId, canEdit, canAddPins, canRemovePins)
    }
}
