package com.ale.stylepin.features.explore.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ale.stylepin.features.boards.domain.usecases.GetAllBoardsUseCase
import com.ale.stylepin.features.boards.domain.usecases.GetBoardPinsUseCase
import com.ale.stylepin.features.explore.domain.entities.TrendingBoard
import com.ale.stylepin.features.explore.domain.entities.UserSearchResult
import com.ale.stylepin.features.explore.domain.usecases.SearchPinsUseCase
// ESTA ES LA LÍNEA QUE FALTABA Y CAUSABA EL ERROR DE KSP:
import com.ale.stylepin.features.explore.domain.usecases.SearchUsersUseCase
import com.ale.stylepin.features.pins.domain.entities.Pin
import com.ale.stylepin.features.pins.domain.usecases.GetPinByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExploreUiState(
    val isLoadingTrending: Boolean = true,
    val isSearching: Boolean = false,
    val searchQuery: String = "",
    val searchSelectedTab: Int = 0, // 0 = Pines, 1 = Usuarios
    val trendingBoards: List<TrendingBoard> = emptyList(),
    val searchPinResults: List<Pin> = emptyList(),
    val searchUserResults: List<UserSearchResult> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val getAllBoardsUseCase: GetAllBoardsUseCase,
    private val getBoardPinsUseCase: GetBoardPinsUseCase,
    private val getPinByIdUseCase: GetPinByIdUseCase,
    private val searchUsersUseCase: SearchUsersUseCase, // <-- KSP SE QUEJABA DE ESTE
    private val searchPinsUseCase: SearchPinsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadTrendingBoards()
    }

    fun loadTrendingBoards() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingTrending = true, error = null) }
            try {
                val allBoards = getAllBoardsUseCase().getOrNull()?.filter { !it.isPrivate } ?: emptyList()
                val sortedBoards = allBoards.sortedByDescending { it.pinsCount }

                val boardsWithPreviews = sortedBoards.map { board ->
                    async {
                        val boardPins = getBoardPinsUseCase(board.id).getOrNull() ?: emptyList()
                        val urls = boardPins.take(3).map { bp ->
                            async { getPinByIdUseCase(bp.pinId).getOrNull()?.imageUrl }
                        }.awaitAll().filterNotNull()
                        TrendingBoard(board, urls)
                    }
                }.awaitAll()

                _uiState.update { it.copy(isLoadingTrending = false, trendingBoards = boardsWithPreviews) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoadingTrending = false, error = "Error cargando tendencias") }
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
            _uiState.update { it.copy(searchPinResults = emptyList(), searchUserResults = emptyList(), isSearching = false) }
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
                it.copy(
                    searchPinResults = pins,
                    searchUserResults = users,
                    isSearching = false
                )
            }
        }
    }
}