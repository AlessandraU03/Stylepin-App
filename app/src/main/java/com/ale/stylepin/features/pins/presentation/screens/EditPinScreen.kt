package com.ale.stylepin.features.pins.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ale.stylepin.features.pins.presentation.viewmodels.EditPinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPinScreen(
    pinId: String,
    viewModel: EditPinViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text("Editar Pin") }) }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = uiState.title,
                onValueChange = { viewModel.onTitleChange(it) },
                label = { Text("Título del Outfit") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = uiState.imageUrl,
                onValueChange = { viewModel.onImageUrlChange(it) },
                label = { Text("URL de la Imagen") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { viewModel.updatePin(pinId) { onBack() } },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                enabled = !uiState.isLoading && uiState.title.isNotBlank()
            ) {
                if (uiState.isLoading) CircularProgressIndicator(color = Color.White)
                else Text("Actualizar Pin")
            }

            uiState.error?.let { Text(text = it, color = Color.Red, modifier = Modifier.padding(top = 8.dp)) }
        }
    }
}