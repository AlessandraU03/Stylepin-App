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
import com.ale.stylepin.features.pins.presentation.components.SeasonFilterRow
import com.ale.stylepin.features.pins.presentation.components.PinCard
import com.ale.stylepin.features.pins.presentation.viewmodels.PinsViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun PinsScreen(
    viewModel: PinsViewModel,
    onNavigateToAddPin: () -> Unit
) {
    val uiState = viewModel.uiState

    // Estado local para controlar qué Pin se quiere borrar y mostrar el diálogo
    var pinIdToDelete by remember { mutableStateOf<String?>(null) }

    // --- DIÁLOGO DE CONFIRMACIÓN ---
    if (pinIdToDelete != null) {
        AlertDialog(
            onDismissRequest = { pinIdToDelete = null },
            title = { Text("¿Eliminar Pin?") },
            text = { Text("Esta acción eliminará el outfit permanentemente de tu cuenta.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        pinIdToDelete?.let { id ->
                            viewModel.deletePin(id)
                        }
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

    // Configuración del Pull to Refresh (conecta con fetchPins de EC2)
    val pullRefreshState = rememberPullRefreshState(
        refreshing = uiState.isLoading,
        onRefresh = { viewModel.fetchPins() }
    )

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("StylePin Seasons") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
                SeasonFilterRow(
                    selectedSeason = uiState.selectedSeason,
                    onSeasonSelected = { season ->
                        viewModel.filterBySeason(season)
                    }
                )
            }
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
            // Mostrar error si la API de Python devuelve un fallo (ej: 401 o 403)
            uiState.error?.let {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Grid de Pines
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(uiState.filteredPins, key = { it.id }) { pin ->
                    PinCard(
                        pin = pin,
                        onDeleteClick = { id ->
                            // Al hacer clic, guardamos el ID para mostrar el diálogo
                            pinIdToDelete = id
                        }
                    )
                }
            }

            // Carga central inicial
            if (uiState.isLoading && uiState.pins.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            // Indicador visual de actualización (PullRefresh)
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