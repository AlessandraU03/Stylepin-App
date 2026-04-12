package com.ale.stylepin.features.community.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ale.stylepin.features.community.domain.entities.Notification
import com.ale.stylepin.features.community.presentation.viewmodels.AlertsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(viewModel: AlertsViewModel) {
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notificaciones", fontWeight = FontWeight.Bold) },
                actions = {
                    TextButton(onClick = { viewModel.loadServerNotifications() }) {
                        Text("Actualizar")
                    }
                }
            )
        }
    ) { padding ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }

            notifications.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Notifications,
                            null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.LightGray
                        )
                        Spacer(Modifier.height(16.dp))
                        Text("No tienes notificaciones todavía", color = Color.Gray)
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(notifications) { notif ->
                        NotificationCard(notif)
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationCard(notif: Notification) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Círculo con emoji según el tipo
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(notifBackgroundColor(notif.type)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = notifEmoji(notif.type),
                fontSize = 20.sp
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            // Título según tipo
            Text(
                text = notifTitle(notif.type),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(2.dp))
            // Mensaje completo del servidor
            Text(
                text = notif.message ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            // Si hay actor username lo mostramos también
            if (!notif.actorUsername.isNullOrBlank()) {
                Text(
                    text = "@${notif.actorUsername}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
            // Fecha
            if (!notif.createdAt.isNullOrBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = notif.createdAt.take(10),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun notifBackgroundColor(type: String): Color {
    return when (type) {
        "like" -> MaterialTheme.colorScheme.errorContainer
        "follow" -> MaterialTheme.colorScheme.primaryContainer
        "comment" -> MaterialTheme.colorScheme.secondaryContainer
        "board_collaboration" -> MaterialTheme.colorScheme.tertiaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
}

private fun notifEmoji(type: String): String = when (type) {
    "like" -> "❤️"
    "follow" -> "👤"
    "comment" -> "💬"
    "board_collaboration" -> "📌"
    else -> "🔔"
}

private fun notifTitle(type: String): String = when (type) {
    "like" -> "Nuevo like"
    "follow" -> "Nuevo seguidor"
    "comment" -> "Nuevo comentario"
    "board_collaboration" -> "Colaboración en tablero"
    else -> "Notificación"
}