package com.ale.stylepin.features.profile.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ale.stylepin.features.community.domain.usecases.ToggleFollowUseCase
import com.ale.stylepin.features.profile.domain.entities.ProfileBoard
import com.ale.stylepin.features.profile.domain.entities.ProfilePin
import com.ale.stylepin.features.profile.domain.entities.ProfileSavedPin
import com.ale.stylepin.features.profile.domain.entities.PublicProfile
import com.ale.stylepin.features.profile.domain.usecases.GetProfileContentUseCase
import com.ale.stylepin.features.profile.domain.usecases.GetUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PublicProfileUiState(
    val isLoading: Boolean = true,
    val profile: PublicProfile? = null,
    val error: String? = null,
    val pins: List<ProfilePin> = emptyList(),
    val boards: List<ProfileBoard> = emptyList(),
    val savedPins: List<ProfileSavedPin> = emptyList(),
    val isLoadingContent: Boolean = false
)

@HiltViewModel
class PublicProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getProfileContentUseCase: GetProfileContentUseCase,
    private val toggleFollowUseCase: ToggleFollowUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(PublicProfileUiState())
    val uiState = _uiState.asStateFlow()

    fun loadProfile(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val profileResult = getUserProfileUseCase.execute(userId)
                if (profileResult.isSuccess) {
                    val profile = profileResult.getOrNull()
                    _uiState.update { it.copy(isLoading = false, profile = profile) }
                    if (profile != null) loadUserContent(profile.id)
                }
            } catch (e: Exception) { }
        }
    }

    private fun loadUserContent(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingContent = true) }
            val pinsTask = async { getProfileContentUseCase.getPins(userId) }
            val boardsTask = async { getProfileContentUseCase.getBoards(userId) }
            val savedTask = async { getProfileContentUseCase.getSavedPins(userId) }
            _uiState.update {
                it.copy(
                    isLoadingContent = false,
                    pins = pinsTask.await().getOrNull() ?: emptyList(),
                    boards = boardsTask.await().getOrNull() ?: emptyList(),
                    savedPins = savedTask.await().getOrNull() ?: emptyList()
                )
            }
        }
    }

    fun toggleFollow() {
        val currentProfile = _uiState.value.profile ?: return
        viewModelScope.launch {
            _uiState.update { state -> state.copy(profile = currentProfile.copy(isFollowing = !currentProfile.isFollowing, followersCount = currentProfile.followersCount + if (currentProfile.isFollowing) -1 else 1)) }
            toggleFollowUseCase.execute(currentProfile.id, currentProfile.isFollowing)
        }
    }
}