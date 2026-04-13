package com.ale.stylepin.features.boards.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ale.stylepin.features.boards.presentation.viewmodels.BoardFormEvent
import com.ale.stylepin.features.boards.presentation.viewmodels.BoardsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBoardScreen(
    userId: String,
    viewModel: BoardsViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Limpiar el estado del formulario al entrar a la pantalla
    LaunchedEffect(Unit) {
        viewModel.clearError()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Tablero", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            // ── Nombre ───────────────────────────────────────────────────────
            OutlinedTextField(
                value = uiState.name,
                onValueChange = {
                    viewModel.onFormEvent(BoardFormEvent.NameChanged(it))
                    viewModel.clearError()
                },
                label = { Text("Nombre del tablero *") },
                placeholder = { Text("Ej: Outfits de verano") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = uiState.error != null && uiState.name.isBlank()
            )

            Spacer(Modifier.height(12.dp))

            // ── Descripción ──────────────────────────────────────────────────
            OutlinedTextField(
                value = uiState.description,
                onValueChange = { viewModel.onFormEvent(BoardFormEvent.DescriptionChanged(it)) },
                label = { Text("Descripción (opcional)") },
                placeholder = { Text("¿De qué trata este tablero?") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            Spacer(Modifier.height(20.dp))

            // ── Privado ──────────────────────────────────────────────────────
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Tablero privado", fontWeight = FontWeight.Medium)
                        Text(
                            "Solo tú podrás verlo",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = uiState.isPrivate,
                        onCheckedChange = { viewModel.onFormEvent(BoardFormEvent.IsPrivateChanged(it)) }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // ── Colaborativo ─────────────────────────────────────────────────
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Permitir colaboradores", fontWeight = FontWeight.Medium)
                        Text(
                            "Otros pueden agregar pins",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = uiState.isCollaborative,
                        onCheckedChange = { viewModel.onFormEvent(BoardFormEvent.IsCollaborativeChanged(it)) }
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

            // ── Botón crear ──────────────────────────────────────────────────
            Button(
                onClick = { viewModel.createBoard(userId) { onBack() } },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = MaterialTheme.shapes.extraLarge,
                enabled = !uiState.isLoading && uiState.name.isNotBlank()
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Creando...")
                } else {
                    Text("Crear Tablero", fontWeight = FontWeight.Bold)
                }
            }

            // ── Error ────────────────────────────────────────────────────────
            uiState.error?.let { errorMsg ->
                Spacer(Modifier.height(12.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMsg,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}