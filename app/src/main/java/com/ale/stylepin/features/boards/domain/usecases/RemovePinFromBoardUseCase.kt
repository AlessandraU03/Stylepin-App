package com.ale.stylepin.features.boards.domain.usecases

import com.ale.stylepin.features.boards.domain.repository.BoardsRepository
import javax.inject.Inject

class RemovePinFromBoardUseCase @Inject constructor(
    private val repository: BoardsRepository
) {
    suspend operator fun invoke(boardId: String, pinId: String): Result<Boolean> = runCatching {
        repository.removePinFromBoard(boardId, pinId)
    }
}