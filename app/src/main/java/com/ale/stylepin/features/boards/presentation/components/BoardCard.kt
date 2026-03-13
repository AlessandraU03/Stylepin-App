package com.ale.stylepin.features.boards.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ale.stylepin.features.boards.domain.entities.Board

@Composable
fun BoardCard(
    board: Board,
    onBoardClick: (String) -> Unit,
    onEditClick: (Board) -> Unit,
    onDeleteClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable { onBoardClick(board.id) },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(140.dp)) {
                if (!board.coverImageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = board.coverImageUrl,
                        contentDescription = board.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = board.name.take(1).uppercase(),
                            style = MaterialTheme.typography.displaySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (board.isOwner) {
                    Row(
                        modifier = Modifier.align(Alignment.TopEnd).padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(
                            onClick = { onEditClick(board) },
                            modifier = Modifier.size(30.dp).background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                        IconButton(
                            onClick = { onDeleteClick(board.id) },
                            modifier = Modifier.size(30.dp).background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
            Column(modifier = Modifier.padding(8.dp)) {
                Text(board.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, maxLines = 1)
                Spacer(Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("${board.pinsCount} pins", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        if (board.isPrivate) Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (board.isCollaborative) Icon(Icons.Default.People, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}