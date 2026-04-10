package com.ale.stylepin.features.boards.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.ale.stylepin.features.boards.domain.entities.BoardCollaborator
import com.ale.stylepin.features.boards.presentation.viewmodels.BoardFormEvent
import com.ale.stylepin.features.boards.presentation.viewmodels.BoardsViewModel
import com.ale.stylepin.features.pins.domain.entities.Pin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoardDetailScreen(
    boardId: String,
    viewModel: BoardsViewModel,
    onBack: () -> Unit,
    onPinClick: (String) -> Unit,
    onEditBoard: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }
    var showAddPinDialog by remember { mutableStateOf(false) }
    var showAddCollaboratorDialog by remember { mutableStateOf(false) }
    var selectedCollaboratorForEdit by remember { mutableStateOf<BoardCollaborator?>(null) }

    LaunchedEffect(boardId) { viewModel.loadBoardDetail(boardId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.boardDetail?.name ?: "Tablero") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Volver") } },
                actions = {
                    if (uiState.boardDetail?.isOwner == true) {
                        IconButton(onClick = { onEditBoard(boardId) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar Tablero")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            val board = uiState.boardDetail
            if (board != null) {
                if (selectedTab == 0 && (board.isOwner || board.isCollaborator)) {
                    FloatingActionButton(onClick = {
                        viewModel.loadUserPins()
                        showAddPinDialog = true
                    }) {
                        Icon(Icons.Default.Add, "Agregar Pin")
                    }
                } else if (selectedTab == 1 && board.isOwner) {
                    FloatingActionButton(onClick = {
                        showAddCollaboratorDialog = true
                    }) {
                        Icon(Icons.Default.PersonAdd, "Añadir Colaborador")
                    }
                }
            }
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
                                    LazyVerticalGrid(
                                        columns = GridCells.Fixed(2),
                                        contentPadding = PaddingValues(8.dp),
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        items(uiState.boardPins, key = { it.id }) { boardPin ->
                                            val pinDetail = uiState.pinsDetails[boardPin.pinId]
                                            BoardPinCard(
                                                pinDetail = pinDetail,
                                                notes = boardPin.notes,
                                                isOwner = board.isOwner,
                                                onPinClick = { onPinClick(boardPin.pinId) },
                                                onDelete = { viewModel.removePinFromBoard(boardId, boardPin.pinId) },
                                                onLikeClick = { /* Implementado vía PinsViewModel o BoardsViewModel si se requiere */ }
                                            )
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
                                            Card(
                                                modifier = Modifier.fillMaxWidth(),
                                                onClick = {
                                                    if (board.isOwner) {
                                                        selectedCollaboratorForEdit = collaborator
                                                    }
                                                }
                                            ) {
                                                Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                        AsyncImage(model = collaborator.userAvatarUrl, contentDescription = null, modifier = Modifier.size(40.dp).clip(CircleShape))
                                                        Column {
                                                            Text(collaborator.userFullName, fontWeight = FontWeight.Medium)
                                                            Text("@${collaborator.userUsername}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                                if (collaborator.canEdit) SuggestionChip(onClick = {}, label = { Text("Editar", style = MaterialTheme.typography.labelSmall) })
                                                                if (collaborator.canAddPins) SuggestionChip(onClick = {}, label = { Text("Añadir", style = MaterialTheme.typography.labelSmall) })
                                                                if (collaborator.canRemovePins) SuggestionChip(onClick = {}, label = { Text("Quitar", style = MaterialTheme.typography.labelSmall) })
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

    if (showAddPinDialog) {
        AddPinToBoardDialog(
            pins = uiState.userPins,
            isLoading = uiState.isLoadingUserPins,
            notes = uiState.addPinNotes,
            onNotesChange = { viewModel.onFormEvent(BoardFormEvent.AddPinNotesChanged(it)) },
            onPinSelected = { pinId ->
                viewModel.addPinToBoard(boardId, pinId) {
                    showAddPinDialog = false
                }
            },
            onDismiss = { showAddPinDialog = false }
        )
    }

    if (showAddCollaboratorDialog) {
        CollaboratorActionDialog(
            title = "Añadir Colaborador",
            confirmText = "Añadir",
            onDismiss = { showAddCollaboratorDialog = false },
            onConfirm = { userId, canEdit, canAddPins, canRemovePins ->
                viewModel.addCollaborator(boardId, userId, canEdit, canAddPins, canRemovePins) {
                    showAddCollaboratorDialog = false
                }
            },
            isEdit = false
        )
    }

    if (selectedCollaboratorForEdit != null) {
        CollaboratorActionDialog(
            title = "Permisos de @${selectedCollaboratorForEdit!!.userUsername}",
            confirmText = "Guardar",
            onDismiss = { selectedCollaboratorForEdit = null },
            onConfirm = { _, canEdit, canAddPins, canRemovePins ->
                viewModel.updateCollaboratorPermissions(boardId, selectedCollaboratorForEdit!!.userId, canEdit, canAddPins, canRemovePins) {
                    selectedCollaboratorForEdit = null
                }
            },
            isEdit = true,
            initialCanEdit = selectedCollaboratorForEdit!!.canEdit,
            initialCanAddPins = selectedCollaboratorForEdit!!.canAddPins,
            initialCanRemovePins = selectedCollaboratorForEdit!!.canRemovePins
        )
    }
}

@Composable
fun BoardPinCard(
    pinDetail: Pin?,
    notes: String?,
    isOwner: Boolean,
    onPinClick: () -> Unit,
    onDelete: () -> Unit,
    onLikeClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable { onPinClick() },
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(160.dp)) {
                if (pinDetail != null) {
                    AsyncImage(
                        model = pinDetail.imageUrl,
                        contentDescription = pinDetail.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    
                    // Botón de Like pequeño en la card del tablero
                    Icon(
                        imageVector = if (pinDetail.isLikedByMe) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = if (pinDetail.isLikedByMe) Color.Red else Color.White.copy(alpha = 0.8f),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(6.dp)
                            .size(18.dp)
                    )

                } else {
                    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                }
                
                if (isOwner) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            .size(32.dp)
                    ) {
                        Icon(Icons.Default.Delete, null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }
            
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = pinDetail?.title ?: "Cargando...",
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!notes.isNullOrBlank()) {
                        Text(
                            text = notes,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (pinDetail != null) {
                        Text(
                            text = "❤️ ${pinDetail.likesCount}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddPinToBoardDialog(
    pins: List<Pin>,
    isLoading: Boolean,
    notes: String,
    onNotesChange: (String) -> Unit,
    onPinSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.8f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Agregar Pin al Tablero", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = onNotesChange,
                    label = { Text("Notas (opcional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))
                Text("Selecciona uno de tus pins:", style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(8.dp))

                if (isLoading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (pins.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No tienes pins propios aún.")
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(4.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(pins, key = { it.id }) { pin ->
                            Card(
                                modifier = Modifier.padding(4.dp).clickable { onPinSelected(pin.id) },
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column {
                                    AsyncImage(
                                        model = pin.imageUrl,
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxWidth().height(100.dp),
                                        contentScale = ContentScale.Crop
                                    )
                                    Text(
                                        text = pin.title,
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(4.dp),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancelar") }
                }
            }
        }
    }
}

@Composable
fun CollaboratorActionDialog(
    title: String,
    confirmText: String,
    onDismiss: () -> Unit,
    onConfirm: (userId: String, canEdit: Boolean, canAddPins: Boolean, canRemovePins: Boolean) -> Unit,
    isEdit: Boolean,
    initialCanEdit: Boolean = false,
    initialCanAddPins: Boolean = true,
    initialCanRemovePins: Boolean = false
) {
    var userId by remember { mutableStateOf("") }
    var canEdit by remember { mutableStateOf(initialCanEdit) }
    var canAddPins by remember { mutableStateOf(initialCanAddPins) }
    var canRemovePins by remember { mutableStateOf(initialCanRemovePins) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (!isEdit) {
                    OutlinedTextField(
                        value = userId,
                        onValueChange = { userId = it },
                        label = { Text("ID del Usuario") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                Text("Permisos:", style = MaterialTheme.typography.labelLarge)
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = canEdit, onCheckedChange = { canEdit = it })
                    Text("Puede editar el tablero")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = canAddPins, onCheckedChange = { canAddPins = it })
                    Text("Puede añadir pins")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = canRemovePins, onCheckedChange = { canRemovePins = it })
                    Text("Puede quitar pins")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(userId, canEdit, canAddPins, canRemovePins) },
                enabled = isEdit || userId.isNotBlank()
            ) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
