package com.ale.stylepin.features.boards.domain.usecases

import com.ale.stylepin.features.boards.domain.entities.Board
import com.ale.stylepin.features.boards.domain.repository.BoardsRepository
import javax.inject.Inject

class GetBoardByIdUseCase @Inject constructor(
    private val repository: BoardsRepository
) {
    suspend operator fun invoke(boardId: String): Result<Board> = runCatching {
        repository.getBoardById(boardId)
    }
}