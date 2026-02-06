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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ale.stylepin.features.pins.presentation.viewmodels.AddPinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPinScreen(viewModel: AddPinViewModel, onBack: () -> Unit) {

    Scaffold(
        topBar = { TopAppBar(title = { Text("Nuevo Pin") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = viewModel.title,
                onValueChange = { viewModel.title = it },
                label = { Text("Título del Outfit") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = viewModel.imageUrl,
                onValueChange = { viewModel.imageUrl = it },
                label = { Text("URL de la Imagen") },
                modifier = Modifier.fillMaxWidth()
            )


            Button(
                onClick = { viewModel.savePin { onBack() } },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                enabled = !viewModel.isSaving && viewModel.title.isNotBlank()
            ) {
                if (viewModel.isSaving) CircularProgressIndicator(color = Color.White)
                else Text("Publicar Pin")
            }
        }
    }
}