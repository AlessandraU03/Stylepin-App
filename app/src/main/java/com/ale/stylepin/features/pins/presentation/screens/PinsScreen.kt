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
import androidx.lifecycle.compose.collectAsStateWithLifecycle // Importación necesaria
import com.ale.stylepin.features.pins.presentation.components.SeasonFilterRow
import com.ale.stylepin.features.pins.presentation.components.PinCard
import com.ale.stylepin.features.pins.presentation.viewmodels.PinsViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun PinsScreen(
    viewModel: PinsViewModel,
    onNavigateToAddPin: () -> Unit
) {
    // 1. Recolección reactiva del estado del StateFlow
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Estado local para el diálogo de confirmación (sigue siendo de la UI)
    var pinIdToDelete by remember { mutableStateOf<String?>(null) }

    // Diálogo de eliminación
    if (pinIdToDelete != null) {
        AlertDialog(
            onDismissRequest = { pinIdToDelete = null },
            title = { Text("¿Eliminar Pin?") },
            text = { Text("Esta acción eliminará el outfit permanentemente de tu cuenta.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        pinIdToDelete?.let { id -> viewModel.deletePin(id) }
                        pinIdToDelete = null
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { pinIdToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Configuración del Pull to Refresh ligada al uiState.isLoading
    val pullRefreshState = rememberPullRefreshState(
        refreshing = uiState.isLoading,
        onRefresh = { viewModel.fetchPins() }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("StylePin Seasons") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
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
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .pullRefresh(pullRefreshState)
        ) {
            // Muestra de errores dinámicos
            uiState.error?.let { msg ->
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = msg,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Grilla de Pins
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Usamos uiState.filteredPins que viene procesado del ViewModel
                items(uiState.filteredPins, key = { it.id }) { pin ->
                    PinCard(
                        pin = pin,
                        onDeleteClick = { id -> pinIdToDelete = id }
                    )
                }
            }

            // Loader central solo si la lista está vacía
            if (uiState.isLoading && uiState.pins.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            // Indicador visual de PullRefresh
            PullRefreshIndicator(
                refreshing = uiState.isLoading,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                backgroundColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            )
        }
    }
}