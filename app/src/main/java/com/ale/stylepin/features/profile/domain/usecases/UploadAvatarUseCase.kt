package com.ale.stylepin.features.profile.domain.usecases
import com.ale.stylepin.features.profile.domain.repositories.ProfileRepository
import javax.inject.Inject
class UploadAvatarUseCase @Inject constructor(private val repository: ProfileRepository) {
    suspend fun execute(uriString: String): Result<String> = repository.uploadAvatar(uriString)
}