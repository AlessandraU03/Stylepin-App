package com.ale.stylepin.features.pins.presentation.screens

import android.widget.Toast
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
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.ale.stylepin.features.pins.presentation.viewmodels.PinsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PinDetailScreen(
    pinId: String,
    viewModel: PinsViewModel,
    onBack: () -> Unit,
    onEditClick: (String) -> Unit,
    onUserClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showBoardsDialog by remember { mutableStateOf(false) }

    LaunchedEffect(pinId) { viewModel.loadPinById(pinId) }

    val pin = uiState.pinDetail
    val isOwner = pin?.userId == uiState.currentUserId

    if (showDeleteDialog && pin != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("¿Eliminar Pin?") }, text = { Text("No se puede deshacer.") },
            confirmButton = { TextButton(onClick = { viewModel.deletePin(pin.id); showDeleteDialog = false; onBack() }) { Text("Eliminar", color = MaterialTheme.colorScheme.error) } },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") } }
        )
    }

    if (showBoardsDialog && pin != null) {
        AlertDialog(
            onDismissRequest = { showBoardsDialog = false }, title = { Text("Guardar en tablero") },
            text = {
                if (uiState.userBoards.isEmpty()) { Text("No tienes tableros. Crea uno en Explorar.") }
                else {
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(uiState.userBoards) { board ->
                            ListItem(
                                headlineContent = { Text(board.name, fontWeight = FontWeight.Bold) },
                                modifier = Modifier.clickable {
                                    viewModel.savePinToBoard(board.id, pin.id)
                                    showBoardsDialog = false
                                    Toast.makeText(context, "Guardado", Toast.LENGTH_SHORT).show()
                                }
                            )
                            HorizontalDivider()
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showBoardsDialog = false }) { Text("Cerrar") } }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") } },
                actions = {
                    if (isOwner && pin != null) {
                        IconButton(onClick = { onEditClick(pin.id) }) { Icon(Icons.Default.Edit, "Editar") }
                        IconButton(onClick = { showDeleteDialog = true }) { Icon(Icons.Default.Delete, "Eliminar", tint = MaterialTheme.colorScheme.error) }
                    }
                }
            )
        },
        bottomBar = {
            if (pin != null) {
                Surface(color = MaterialTheme.colorScheme.surface, tonalElevation = 8.dp) {
                    Row(modifier = Modifier.fillMaxWidth().padding(8.dp).navigationBarsPadding(), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = uiState.newCommentText, onValueChange = { viewModel.onCommentTextChanged(it) }, placeholder = { Text("Añadir un comentario...") },
                            modifier = Modifier.weight(1f), shape = CircleShape, colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color.Transparent, focusedBorderColor = Color.Transparent, unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant, focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant)
                        )
                        IconButton(onClick = { viewModel.addComment(pin.id) }, enabled = uiState.newCommentText.isNotBlank()) { Icon(Icons.AutoMirrored.Filled.Send, "Enviar", tint = MaterialTheme.colorScheme.primary) }
                    }
                }
            }
        }
    ) { padding ->
        if (uiState.isLoadingDetail) { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else if (pin != null) {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp), contentPadding = PaddingValues(bottom = 80.dp)) {
                item {
                    Box {
                        AsyncImage(model = pin.imageUrl, contentDescription = pin.title, modifier = Modifier.fillMaxWidth().height(450.dp).clip(RoundedCornerShape(16.dp)), contentScale = ContentScale.Crop)
                        Button(onClick = { showBoardsDialog = true }, modifier = Modifier.align(Alignment.TopEnd).padding(16.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) { Text("Guardar", color = Color.White, fontWeight = FontWeight.Bold) }
                        FloatingActionButton(onClick = { viewModel.toggleLike(pin.id) }, modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp), shape = CircleShape, containerColor = MaterialTheme.colorScheme.surface) {
                            Icon(imageVector = if (pin.isLikedByMe) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder, contentDescription = "Like", tint = if (pin.isLikedByMe) Color.Red else MaterialTheme.colorScheme.onSurface)
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable { onUserClick(pin.userId) }.padding(vertical = 8.dp)) {
                        val displayName = pin.userFullName.ifBlank { pin.username }
                        AsyncImage(model = pin.userAvatarUrl ?: "https://ui-avatars.com/api/?name=${displayName.replace(" ", "+")}", contentDescription = "Avatar", modifier = Modifier.size(48.dp).clip(CircleShape).background(Color.LightGray), contentScale = ContentScale.Crop)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(displayName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text("@${pin.username}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(pin.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    if (!pin.description.isNullOrBlank()) { Spacer(Modifier.height(8.dp)); Text(pin.description, style = MaterialTheme.typography.bodyLarge) }
                    Spacer(Modifier.height(24.dp))
                    Row(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp)).padding(16.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                        StatItem("Likes", pin.likesCount); StatItem("Comentarios", uiState.comments.size); StatItem("Guardados", pin.savesCount)
                    }
                    Spacer(Modifier.height(24.dp))
                    Text("Comentarios", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(16.dp))
                }
                if (uiState.comments.isEmpty()) {
                    item { Text("Sé el primero en comentar.", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(vertical = 16.dp)) }
                } else {
                    items(uiState.comments) { comment ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                            val commentName = comment.userFullName?.ifBlank { comment.userUsername } ?: comment.userUsername
                            AsyncImage(model = comment.userAvatarUrl ?: "https://ui-avatars.com/api/?name=${commentName.replace(" ", "+")}", contentDescription = null, modifier = Modifier.size(36.dp).clip(CircleShape).background(Color.LightGray), contentScale = ContentScale.Crop)
                            Spacer(Modifier.width(12.dp))
                            Column { Text(commentName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium); Text(comment.text, style = MaterialTheme.typography.bodyMedium) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItem(label: String, count: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) { Text(count.toString(), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge); Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
}