package com.ale.stylepin.features.boards.domain.usecases

import com.ale.stylepin.features.boards.domain.entities.Board
import com.ale.stylepin.features.boards.domain.repository.BoardsRepository
import javax.inject.Inject

class GetUserBoardsUseCase @Inject constructor(
    private val repository: BoardsRepository
) {
    suspend operator fun invoke(userId: String): Result<List<Board>> = runCatching {
        repository.getUserBoards(userId)
    }
}