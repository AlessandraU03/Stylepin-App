package com.ale.stylepin.features.boards.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ale.stylepin.features.boards.domain.entities.Board
import com.ale.stylepin.features.boards.presentation.components.BoardCard
import com.ale.stylepin.features.boards.presentation.viewmodels.BoardsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoardsScreen(
    userId: String,
    viewModel: BoardsViewModel,
    onNavigateToBoardDetail: (String) -> Unit,
    onNavigateToCreateBoard: () -> Unit,
    onNavigateToEditBoard: (Board) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var boardIdToDelete by remember { mutableStateOf<String?>(null) }

    // Al entrar a la pantalla, cargamos todos los tableros públicos
    LaunchedEffect(Unit) { 
        viewModel.loadAllBoards() 
    }

    boardIdToDelete?.let { id ->
        AlertDialog(
            onDismissRequest = { boardIdToDelete = null },
            title = { Text("¿Eliminar tablero?") },
            text = { Text("Esta acción eliminará el tablero y todo su contenido permanentemente.") },
            confirmButton = {
                TextButton(onClick = { 
                    viewModel.deleteBoard(id, userId)
                    boardIdToDelete = null 
                }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = { boardIdToDelete = null }) { Text("Cancelar") } }
        )
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Explorar Tableros") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreateBoard) {
                Icon(Icons.Default.Add, contentDescription = "Crear tablero")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when {
                uiState.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                uiState.boards.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No se encontraron tableros públicos")
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = onNavigateToCreateBoard) { 
                            Text("Crear mi propio tablero") 
                        }
                    }
                }
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(uiState.boards, key = { it.id }) { board ->
                            BoardCard(
                                board = board,
                                onBoardClick = { onNavigateToBoardDetail(it) },
                                onEditClick = { onNavigateToEditBoard(it) },
                                onDeleteClick = { boardIdToDelete = it }
                            )
                        }
                    }
                }
            }
            uiState.error?.let { error ->
                Snackbar(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
                    action = { 
                        TextButton(onClick = { viewModel.clearError() }) { Text("OK") } 
                    }
                ) { Text(error) }
            }
        }
    }
}