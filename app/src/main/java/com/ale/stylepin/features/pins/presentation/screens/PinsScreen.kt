package com.ale.stylepin.features.pins.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ale.stylepin.features.pins.domain.entities.Pin
import com.ale.stylepin.features.pins.presentation.components.PinCard
import com.ale.stylepin.features.pins.presentation.viewmodels.PinsViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun PinsScreen(
    viewModel: PinsViewModel,
    onNavigateToAddPin: () -> Unit,
    onNavigateToEditPin: (Pin) -> Unit
) {
    // Cambiamos 'by' por '=' y usamos '.value' para evitar avisos del linter
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val pinIdToDeleteState = remember { mutableStateOf<String?>(null) }
    val currentPinIdToDelete = pinIdToDeleteState.value

    // Diálogo de eliminación
    if (currentPinIdToDelete != null) {
        AlertDialog(
            onDismissRequest = { pinIdToDeleteState.value = null },
            title = { Text("¿Eliminar Pin?") },
            text = { Text("Esta acción eliminará el outfit permanentemente de tu cuenta.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deletePin(currentPinIdToDelete)
                        pinIdToDeleteState.value = null
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { pinIdToDeleteState.value = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = uiState.isLoading,
        onRefresh = { viewModel.fetchPins() }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("StylePin Seasons") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddPin,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar Pin",
                    tint = Color.White
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .pullRefresh(pullRefreshState)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(uiState.filteredPins, key = { it.id }) { pin ->
                    PinCard(
                        pin = pin,
                        onDeleteClick = { id ->
                            pinIdToDeleteState.value = id
                        },
                        onEditClick = { onNavigateToEditPin(pin) }
                    )
                }
            }

            if (uiState.isLoading && uiState.pins.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            PullRefreshIndicator(
                refreshing = uiState.isLoading,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                contentColor = MaterialTheme.colorScheme.primary
            )
        }
    }
}