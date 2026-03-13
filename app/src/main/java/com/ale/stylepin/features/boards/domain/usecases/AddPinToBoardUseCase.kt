package com.ale.stylepin.features.boards.domain.usecases

import com.ale.stylepin.features.boards.domain.entities.BoardPin
import com.ale.stylepin.features.boards.domain.repository.BoardsRepository
import javax.inject.Inject

class AddPinToBoardUseCase @Inject constructor(
    private val repository: BoardsRepository
) {
    suspend operator fun invoke(
        boardId: String,
        pinId: String,
        notes: String? = null
    ): Result<BoardPin> = runCatching {
        repository.addPinToBoard(boardId, pinId, notes)
    }
}