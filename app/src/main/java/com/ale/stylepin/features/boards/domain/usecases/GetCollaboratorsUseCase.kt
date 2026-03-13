package com.ale.stylepin.features.boards.domain.usecases

import com.ale.stylepin.features.boards.domain.entities.BoardCollaborator
import com.ale.stylepin.features.boards.domain.repository.BoardsRepository
import javax.inject.Inject

class GetCollaboratorsUseCase @Inject constructor(
    private val repository: BoardsRepository
) {
    suspend operator fun invoke(boardId: String): Result<List<BoardCollaborator>> = runCatching {
        repository.getCollaborators(boardId)
    }
}