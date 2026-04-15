package com.ale.stylepin.features.boards.domain.usecases

import com.ale.stylepin.features.boards.domain.entities.BoardPin
import com.ale.stylepin.features.boards.domain.repository.BoardsRepository
import javax.inject.Inject

class GetBoardPinsUseCase @Inject constructor(
    private val repository: BoardsRepository
) {
    suspend operator fun invoke(boardId: String): Result<List<BoardPin>> = runCatching {
        repository.getBoardPins(boardId)
    }
}