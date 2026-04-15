package com.ale.stylepin.features.boards.domain.usecases

import com.ale.stylepin.features.boards.domain.entities.Board
import com.ale.stylepin.features.boards.domain.repository.BoardsRepository
import javax.inject.Inject

class CreateBoardUseCase @Inject constructor(
    private val repository: BoardsRepository
) {
    suspend operator fun invoke(
        name: String,
        description: String? = null,
        isPrivate: Boolean = false,
        isCollaborative: Boolean = true
    ): Result<Board> = runCatching {
        repository.createBoard(name, description, isPrivate, isCollaborative)
    }
}