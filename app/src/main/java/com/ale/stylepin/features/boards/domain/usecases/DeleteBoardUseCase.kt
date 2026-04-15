package com.ale.stylepin.features.boards.domain.usecases

import com.ale.stylepin.features.boards.domain.repository.BoardsRepository
import javax.inject.Inject

class DeleteBoardUseCase @Inject constructor(
    private val repository: BoardsRepository
) {
    suspend operator fun invoke(boardId: String): Result<Boolean> = runCatching {
        repository.deleteBoard(boardId)
    }
}