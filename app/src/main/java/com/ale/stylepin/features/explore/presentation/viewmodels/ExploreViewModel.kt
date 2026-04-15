package com.ale.stylepin.features.explore.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ale.stylepin.features.boards.domain.entities.Board
import com.ale.stylepin.features.boards.domain.usecases.GetAllBoardsUseCase
import com.ale.stylepin.features.explore.domain.entities.UserSearchResult
import com.ale.stylepin.features.explore.domain.usecases.SearchPinsUseCase
import com.ale.stylepin.features.explore.domain.usecases.SearchUsersUseCase
import com.ale.stylepin.features.pins.domain.entities.Pin
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Agrupa los tableros de un mismo usuario para mostrarse en fila horizontal.
 */
data class UserBoardsGroup(
    val userId: String,
    val username: String,
    val userFullName: String,
    val userAvatarUrl: String?,
    val boards: List<Board>
)

data class ExploreUiState(
    val isLoadingBoards: Boolean = true,
    val isSearching: Boolean = false,
    val searchQuery: String = "",
    val searchSelectedTab: Int = 0, // 0 = Pines, 1 = Usuarios
    // NUEVO: tableros agrupados por usuario
    val userBoardGroups: List<UserBoardsGroup> = emptyList(),
    val searchPinResults: List<Pin> = emptyList(),
    val searchUserResults: List<UserSearchResult> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val getAllBoardsUseCase: GetAllBoardsUseCase,
    private val searchUsersUseCase: SearchUsersUseCase,
    private val searchPinsUseCase: SearchPinsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadAllBoards()
    }

    /**
     * Carga TODOS los tableros públicos y los agrupa por usuario.
     */
    fun loadAllBoards() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingBoards = true, error = null) }
            try {
                val allBoards = getAllBoardsUseCase().getOrNull()
                    ?.filter { !it.isPrivate }
                    ?: emptyList()

                // Agrupar por userId
                val groups = allBoards
                    .groupBy { it.userId }
                    .map { (userId, boards) ->
                        val first = boards.first()
                        UserBoardsGroup(
                            userId = userId,
                            username = first.userUsername,
                            userFullName = first.userFullName ?: first.userUsername,
                            userAvatarUrl = first.userAvatarUrl,
                            boards = boards
                        )
                    }
                    // Ordenar: los que tienen más tableros primero
                    .sortedByDescending { it.boards.size }

                _uiState.update {
                    it.copy(isLoadingBoards = false, userBoardGroups = groups)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoadingBoards = false, error = "Error cargando tableros")
                }
            }
        }
    }

    fun setSearchTab(tabIndex: Int) {
        _uiState.update { it.copy(searchSelectedTab = tabIndex) }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchJob?.cancel()
        if (query.isBlank()) {
            _uiState.update {
                it.copy(searchPinResults = emptyList(), searchUserResults = emptyList(), isSearching = false)
            }
            return
        }
        searchJob = viewModelScope.launch {
            _uiState.update { it.copy(isSearching = true) }
            delay(500)
            val pinsDeferred = async { searchPinsUseCase.execute(query) }
            val usersDeferred = async { searchUsersUseCase.execute(query) }
            val pins = pinsDeferred.await().getOrNull() ?: emptyList()
            val users = usersDeferred.await().getOrNull() ?: emptyList()
            _uiState.update {
                it.copy(searchPinResults = pins, searchUserResults = users, isSearching = false)
            }
        }
    }
}