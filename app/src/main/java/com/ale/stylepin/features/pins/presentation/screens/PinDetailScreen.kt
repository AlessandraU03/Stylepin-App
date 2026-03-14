package com.ale.stylepin.features.pins.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.ale.stylepin.features.pins.presentation.viewmodels.PinsViewModel

// Componente para los iconos de estadísticas
@Composable
fun PinStatBadge(icon: ImageVector, value: String, label: String, tint: Color = Color.Gray) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = icon, contentDescription = label, tint = tint, modifier = Modifier.size(24.dp))
        Spacer(Modifier.height(4.dp))
        Text(text = value, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PinDetailScreen(
    pinId: String,
    viewModel: PinsViewModel,
    onBack: () -> Unit,
    onNavigateToEditPin: (com.ale.stylepin.features.pins.domain.entities.Pin) -> Unit,
    onNavigateToUserProfile: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var newBoardName by remember { mutableStateOf("") }

    LaunchedEffect(pinId) {
        viewModel.loadPinById(pinId)
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("¿Eliminar Pin?") },
            text = { Text("Esta acción eliminará el outfit permanentemente.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deletePin(pinId)
                    showDeleteDialog = false
                    onBack()
                }) { Text("Eliminar", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") } }
        )
    }

    // --- BOTTOM SHEET PARA GUARDAR EN TABLERO ---
    if (uiState.isSaveSheetVisible) {
        ModalBottomSheet(onDismissRequest = { viewModel.hideSaveDialog() }) {
            Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                Text("Guardar en tablero", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))

                // Crear nuevo tablero
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = newBoardName,
                        onValueChange = { newBoardName = it },
                        placeholder = { Text("Crear tablero nuevo...") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = CircleShape
                    )
                    Spacer(Modifier.width(8.dp))
                    IconButton(onClick = {
                        viewModel.createBoardAndSavePin(newBoardName)
                        newBoardName = ""
                    }, modifier = Modifier.background(MaterialTheme.colorScheme.primary, CircleShape)) {
                        Icon(Icons.Default.Add, contentDescription = "Crear", tint = Color.White)
                    }
                }

                Spacer(Modifier.height(16.dp))
                HorizontalDivider()

                // Lista de tableros
                LazyColumn(modifier = Modifier.weight(1f, fill = false)) {
                    items(uiState.myBoards) { board ->
                        ListItem(
                            headlineContent = { Text(board.name, fontWeight = FontWeight.Medium) },
                            trailingContent = {
                                Button(
                                    onClick = { viewModel.savePinToBoard(board.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                                ) { Text("Guardar") }
                            },
                            modifier = Modifier.clickable { viewModel.savePinToBoard(board.id) }
                        )
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.background(Color.White.copy(alpha = 0.5f), CircleShape)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    val isOwner = uiState.currentUserId != null && uiState.pinDetail?.userId == uiState.currentUserId
                    if (isOwner) {
                        IconButton(onClick = { uiState.pinDetail?.let(onNavigateToEditPin) }, modifier = Modifier.background(Color.White.copy(alpha = 0.5f), CircleShape)) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar")
                        }
                        Spacer(Modifier.width(8.dp))
                        IconButton(onClick = { showDeleteDialog = true }, modifier = Modifier.background(Color.White.copy(alpha = 0.5f), CircleShape)) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                        }
                    }
                }
            )
        },
        bottomBar = {
            // Input de Comentarios fijo abajo
            Surface(shadowElevation = 8.dp, color = MaterialTheme.colorScheme.surface) {
                Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = uiState.commentInput,
                        onValueChange = { viewModel.onCommentInputChanged(it) },
                        placeholder = { Text("Añade un comentario...") },
                        modifier = Modifier.weight(1f),
                        shape = CircleShape
                    )
                    Spacer(Modifier.width(8.dp))
                    IconButton(onClick = { viewModel.postComment() }, enabled = uiState.commentInput.isNotBlank()) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (uiState.isLoadingDetail) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.pinDetail != null) {
                val pin = uiState.pinDetail!!

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    // 1. IMAGEN
                    item {
                        AsyncImage(
                            model = pin.imageUrl,
                            contentDescription = pin.title,
                            modifier = Modifier.fillMaxWidth().heightIn(min = 300.dp, max = 500.dp).clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    // 2. BOTONES PRINCIPALES (Like, Share, Save)
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                IconButton(onClick = { viewModel.toggleLike(pinId) }) {
                                    Icon(
                                        imageVector = if (pin.isLikedByMe) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = "Like",
                                        tint = if (pin.isLikedByMe) Color.Red else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                                IconButton(onClick = { /* Share */ }) {
                                    Icon(Icons.Default.Share, contentDescription = "Compartir", modifier = Modifier.size(28.dp))
                                }
                            }

                            Button(
                                onClick = { viewModel.showSaveDialog() },
                                colors = ButtonDefaults.buttonColors(containerColor = if (pin.isSavedByMe) Color.Gray else Color.Red),
                                shape = CircleShape
                            ) {
                                Text(if (pin.isSavedByMe) "Guardado" else "Guardar", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // 3. TÍTULO Y DESCRIPCIÓN
                    item {
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            Text(pin.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                            if (!pin.description.isNullOrBlank()) {
                                Spacer(Modifier.height(8.dp))
                                Text(pin.description, style = MaterialTheme.typography.bodyLarge)
                            }
                            Spacer(Modifier.height(16.dp))
                        }
                    }

                    // 4. ESTADÍSTICAS DEL PIN (Vistas, Likes, Comentarios, Guardados)
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            PinStatBadge(icon = Icons.Outlined.Visibility, value = "${pin.viewsCount}", label = "Vistas")
                            PinStatBadge(icon = if (pin.isLikedByMe) Icons.Default.Favorite else Icons.Default.FavoriteBorder, value = "${pin.likesCount}", label = "Likes", tint = if (pin.isLikedByMe) Color.Red else Color.Gray)
                            PinStatBadge(icon = Icons.Outlined.ChatBubbleOutline, value = "${pin.commentsCount}", label = "Comentarios")
                            PinStatBadge(icon = if (pin.isSavedByMe) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder, value = "${pin.savesCount}", label = "Guardados", tint = if (pin.isSavedByMe) MaterialTheme.colorScheme.primary else Color.Gray)
                        }
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    }

                    // 5. INFORMACIÓN DEL AUTOR (Sección de Seguir)
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f).clickable { onNavigateToUserProfile(pin.userId) }) {
                                val fallbackUrl = "https://ui-avatars.com/api/?name=${pin.userFullName.replace(" ", "+")}&background=random"
                                AsyncImage(
                                    model = pin.userAvatarUrl?.takeIf { it.isNotBlank() } ?: fallbackUrl,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp).clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(pin.userFullName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                                    Text("@${pin.username}", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                                }
                            }

                            // Botón seguir (Solo si no es mi propio pin)
                            if (pin.userId != uiState.currentUserId) {
                                Button(
                                    onClick = { viewModel.toggleFollowAuthor() },
                                    colors = ButtonDefaults.buttonColors(containerColor = if (uiState.authorIsFollowed) Color.Black else MaterialTheme.colorScheme.surfaceVariant, contentColor = if (uiState.authorIsFollowed) Color.White else MaterialTheme.colorScheme.onSurface)
                                ) {
                                    Text(if (uiState.authorIsFollowed) "Siguiendo" else "Seguir", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // 6. COMENTARIOS
                    item {
                        Text("Comentarios (${uiState.comments.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                    }

                    if (uiState.comments.isEmpty()) {
                        item {
                            Text("Aún no hay comentarios. ¡Sé el primero!", color = Color.Gray, modifier = Modifier.padding(16.dp))
                        }
                    } else {
                        items(uiState.comments) { comment ->
                            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp).fillMaxWidth()) {
                                val fallbackUrl = "https://ui-avatars.com/api/?name=${comment.userFullName.replace(" ", "+")}"
                                AsyncImage(
                                    model = comment.userAvatarUrl.takeIf { it.isNotBlank() } ?: fallbackUrl,
                                    contentDescription = null,
                                    modifier = Modifier.size(36.dp).clip(CircleShape)
                                )
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(comment.userFullName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                        Spacer(Modifier.width(8.dp))
                                        Text(comment.createdAt.take(10), color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                                    }
                                    Spacer(Modifier.height(4.dp))
                                    Text(comment.text, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }

                    // Espacio final para que el input no tape el último comentario
                    item { Spacer(Modifier.height(100.dp)) }
                }
            }
        }
    }
}