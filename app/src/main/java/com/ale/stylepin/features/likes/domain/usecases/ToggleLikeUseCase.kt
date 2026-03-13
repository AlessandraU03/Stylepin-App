package com.ale.stylepin.features.likes.domain.usecases

import com.ale.stylepin.features.likes.domain.entities.LikeStatus
import com.ale.stylepin.features.likes.domain.repository.LikeRepository
import javax.inject.Inject

class ToggleLikeUseCase @Inject constructor(
    private val repository: LikeRepository
) {
    suspend operator fun invoke(pinId: String): Result<LikeStatus> {
        return repository.toggleLike(pinId)
    }
}
