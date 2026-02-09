package com.ale.stylepin.features.pins.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState // Importante importar esto
import androidx.compose.runtime.getValue        // Importante importar esto
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ale.stylepin.features.pins.presentation.viewmodels.AddPinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPinScreen(viewModel: AddPinViewModel, onBack: () -> Unit) {

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Nuevo Pin") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {

            // 2. TÍTULO
            OutlinedTextField(
                value = uiState.title, // Leemos del State
                onValueChange = { viewModel.onTitleChange(it) }, // Usamos la función del VM
                label = { Text("Título del Outfit") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            // 3. IMAGEN
            OutlinedTextField(
                value = uiState.imageUrl, // Leemos del State
                onValueChange = { viewModel.onImageUrlChange(it) }, // Usamos la función del VM
                label = { Text("URL de la Imagen") },
                modifier = Modifier.fillMaxWidth()
            )

            // 4. BOTÓN
            Button(
                onClick = { viewModel.savePin { onBack() } },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                // Usamos isLoading (que viene de PinsUiState) en lugar de isSaving
                enabled = !uiState.isLoading && uiState.title.isNotBlank()
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Text("Publicar Pin")
                }
            }

            // Opcional: Mostrar error si existe
            if (uiState.error != null) {
                Text(
                    text = uiState.error ?: "",
                    color = Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}