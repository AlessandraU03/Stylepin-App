package com.ale.stylepin.features.boards.domain.usecases

import com.ale.stylepin.features.boards.domain.entities.Board
import com.ale.stylepin.features.boards.domain.repository.BoardsRepository
import javax.inject.Inject

class UpdateBoardUseCase @Inject constructor(
    private val repository: BoardsRepository
) {
    suspend operator fun invoke(
        boardId: String,
        name: String,
        description: String? = null,
        isPrivate: Boolean = false,
        isCollaborative: Boolean = false,
        coverImageUrl: String? = null
    ): Result<Board> = runCatching {
        repository.updateBoard(boardId, name, description, isPrivate, isCollaborative, coverImageUrl)
    }
}