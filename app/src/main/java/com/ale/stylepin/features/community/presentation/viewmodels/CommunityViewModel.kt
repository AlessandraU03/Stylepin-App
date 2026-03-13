package com.ale.stylepin.features.community.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ale.stylepin.features.community.domain.entities.CommunityUser
import com.ale.stylepin.features.community.domain.usecases.GetFollowersUseCase
import com.ale.stylepin.features.community.domain.usecases.GetFollowingUseCase
import com.ale.stylepin.features.community.domain.usecases.ToggleFollowUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CommunityUiState(
    val isLoading: Boolean = true,
    val followers: List<CommunityUser> = emptyList(),
    val following: List<CommunityUser> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val getFollowersUseCase: GetFollowersUseCase,
    private val getFollowingUseCase: GetFollowingUseCase,
    private val toggleFollowUseCase: ToggleFollowUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CommunityUiState())
    val uiState = _uiState.asStateFlow()

    @Suppress("unused")
    fun loadData(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val followersResult = getFollowersUseCase.execute(userId)
            val followingResult = getFollowingUseCase.execute(userId)

            if (followersResult.isSuccess && followingResult.isSuccess) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        followers = followersResult.getOrNull() ?: emptyList(),
                        following = followingResult.getOrNull() ?: emptyList()
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Error al cargar la comunidad.") }
            }
        }
    }

    fun toggleFollow(user: CommunityUser) {
        viewModelScope.launch {
            // Actualización UI Optimista
            updateUserFollowState(user.id, !user.isFollowing)

            val result = toggleFollowUseCase.execute(user.id, user.isFollowing)

            // Reversión en caso de error de red
            if (result.isFailure) {
                updateUserFollowState(user.id, user.isFollowing)
            }
        }
    }

    private fun updateUserFollowState(userId: String, isNowFollowing: Boolean) {
        _uiState.update { state ->
            val updatedFollowers = state.followers.map { if (it.id == userId) it.copy(isFollowing = isNowFollowing) else it }
            val updatedFollowing = state.following.map { if (it.id == userId) it.copy(isFollowing = isNowFollowing) else it }
            state.copy(followers = updatedFollowers, following = updatedFollowing)
        }
    }
}