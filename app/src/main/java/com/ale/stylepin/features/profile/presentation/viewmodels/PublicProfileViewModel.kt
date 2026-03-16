package com.ale.stylepin.features.profile.presentation.viewmodels

import android.util.Log // <-- Necesario para la consola
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ale.stylepin.features.boards.domain.entities.Board
import com.ale.stylepin.features.boards.domain.usecases.GetUserBoardsUseCase
import com.ale.stylepin.features.community.domain.usecases.CheckFollowStatusUseCase
import com.ale.stylepin.features.community.domain.usecases.ToggleFollowUseCase
import com.ale.stylepin.features.pins.domain.entities.Pin
import com.ale.stylepin.features.pins.domain.usecases.GetPinsUseCase
import com.ale.stylepin.features.profile.domain.entities.Profile
import com.ale.stylepin.features.profile.domain.usecases.GetProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PublicProfileUiState(
    val isLoading: Boolean = true,
    val profile: Profile? = null,
    val isFollowing: Boolean = false,
    val publicPins: List<Pin> = emptyList(),
    val publicBoards: List<Board> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class PublicProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val getPinsUseCase: GetPinsUseCase,
    private val getUserBoardsUseCase: GetUserBoardsUseCase,
    private val checkFollowStatusUseCase: CheckFollowStatusUseCase,
    private val toggleFollowUseCase: ToggleFollowUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PublicProfileUiState())
    val uiState = _uiState.asStateFlow()

    fun loadProfile(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val profile = getProfileUseCase.execute(userId)
                _uiState.update { it.copy(profile = profile) }

                checkFollowStatusUseCase.execute(userId).onSuccess { following ->
                    _uiState.update { it.copy(isFollowing = following) }
                }

                val allPins = getPinsUseCase().getOrNull() ?: emptyList()
                val userPins = allPins.filter { it.userId == userId && !it.isPrivate }
                _uiState.update { it.copy(publicPins = userPins) }

                val userBoards = getUserBoardsUseCase(userId).getOrNull() ?: emptyList()
                val publicBoards = userBoards.filter { !it.isPrivate }
                _uiState.update { it.copy(publicBoards = publicBoards, isLoading = false) }

            } catch (e: Exception) {
                // 👇 LA TRAMPA ANTI-PANTALLA BLANCA
                val errorReal = e.message ?: e.localizedMessage ?: e.javaClass.simpleName
                Log.e("PERFIL_PUBLICO", "Crash al cargar: $errorReal", e)
                _uiState.update { it.copy(isLoading = false, error = errorReal) }
            }
        }
    }

    fun toggleFollow() {
        viewModelScope.launch {
            val userId = _uiState.value.profile?.id ?: return@launch
            val currentlyFollowing = _uiState.value.isFollowing

            _uiState.update { it.copy(isFollowing = !currentlyFollowing) }

            toggleFollowUseCase.execute(userId, currentlyFollowing).onFailure {
                _uiState.update { it.copy(isFollowing = currentlyFollowing) }
            }
        }
    }
}