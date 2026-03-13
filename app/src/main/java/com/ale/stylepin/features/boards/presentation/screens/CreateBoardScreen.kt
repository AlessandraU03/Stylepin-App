package com.ale.stylepin.features.boards.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Tablero") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Volver") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = uiState.name,
                onValueChange = { viewModel.onFormEvent(BoardFormEvent.NameChanged(it)) },
                label = { Text("Nombre del tablero *") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = uiState.description,
                onValueChange = { viewModel.onFormEvent(BoardFormEvent.DescriptionChanged(it)) },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Tablero privado")
                Switch(checked = uiState.isPrivate, onCheckedChange = { viewModel.onFormEvent(BoardFormEvent.IsPrivateChanged(it)) })
            }
            Spacer(Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Permitir colaboradores")
                Switch(checked = uiState.isCollaborative, onCheckedChange = { viewModel.onFormEvent(BoardFormEvent.IsCollaborativeChanged(it)) })
            }
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = { viewModel.createBoard(userId) { onBack() } },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading && uiState.name.isNotBlank()
            ) {
                if (uiState.isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                else Text("Crear Tablero")
            }
            uiState.error?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp)) }
        }
    }
}