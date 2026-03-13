package com.ale.stylepin.features.boards.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.ale.stylepin.features.boards.presentation.viewmodels.BoardsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoardDetailScreen(
    boardId: String,
    viewModel: BoardsViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(boardId) { viewModel.loadBoardDetail(boardId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.boardDetail?.name ?: "Tablero") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Volver") } }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when {
                uiState.isLoadingDetail -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                uiState.boardDetail != null -> {
                    val board = uiState.boardDetail!!
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Header
                        Column(modifier = Modifier.padding(16.dp)) {
                            if (!board.description.isNullOrBlank()) {
                                Text(board.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(Modifier.height(8.dp))
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("${board.pinsCount} pins", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                                if (board.isPrivate) Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Default.Lock, null, modifier = Modifier.size(14.dp))
                                    Text("Privado", style = MaterialTheme.typography.bodySmall)
                                }
                                if (board.isCollaborative) Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Default.People, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                                    Text("Colaborativo", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }

                        TabRow(selectedTabIndex = selectedTab) {
                            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Pins") })
                            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Colaboradores") })
                        }

                        when (selectedTab) {
                            0 -> {
                                if (uiState.boardPins.isEmpty()) {
                                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Sin pins aún") }
                                } else {
                                    LazyVerticalGrid(columns = GridCells.Fixed(2), contentPadding = PaddingValues(8.dp), modifier = Modifier.fillMaxSize()) {
                                        items(uiState.boardPins, key = { it.id }) { boardPin ->
                                            Card(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                                                Row(modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(boardPin.pinId, style = MaterialTheme.typography.bodySmall, maxLines = 1)
                                                        if (!boardPin.notes.isNullOrBlank()) Text(boardPin.notes, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                    }
                                                    if (board.isOwner) IconButton(onClick = { viewModel.removePinFromBoard(boardId, boardPin.pinId) }) {
                                                        Icon(Icons.Default.Delete, null, modifier = Modifier.size(16.dp))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            1 -> {
                                if (uiState.collaborators.isEmpty()) {
                                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Sin colaboradores") }
                                } else {
                                    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxSize()) {
                                        items(uiState.collaborators, key = { it.id }) { collaborator ->
                                            Card(modifier = Modifier.fillMaxWidth()) {
                                                Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                        AsyncImage(model = collaborator.userAvatarUrl, contentDescription = null, modifier = Modifier.size(40.dp).clip(CircleShape))
                                                        Column {
                                                            Text(collaborator.userFullName, fontWeight = FontWeight.Medium)
                                                            Text("@${collaborator.userUsername}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                                if (collaborator.canEdit) SuggestionChip(onClick = {}, label = { Text("Editar", style = MaterialTheme.typography.labelSmall) })
                                                                if (collaborator.canAddPins) SuggestionChip(onClick = {}, label = { Text("Agregar pins", style = MaterialTheme.typography.labelSmall) })
                                                            }
                                                        }
                                                    }
                                                    if (board.isOwner) IconButton(onClick = { viewModel.removeCollaborator(boardId, collaborator.userId) }) {
                                                        Icon(Icons.Default.Delete, null, modifier = Modifier.size(16.dp))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}