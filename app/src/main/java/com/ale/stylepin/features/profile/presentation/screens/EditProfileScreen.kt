package com.ale.stylepin.features.profile.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ale.stylepin.features.profile.presentation.viewmodels.EditProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    viewModel: EditProfileViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Perfil", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Outlined.Close, "Cancelar") }
                },
                actions = {
                    TextButton(onClick = { viewModel.saveProfile() }, enabled = !uiState.isLoading) {
                        Text("Guardar", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Campo de correo (Solo lectura)
                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = { },
                    label = { Text("Correo electrónico") },
                    enabled = false, // No se puede editar aquí
                    modifier = Modifier.fillMaxWidth(),
                    shape = CircleShape,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledBorderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = uiState.fullName,
                    onValueChange = { viewModel.onFullNameChange(it) },
                    label = { Text("Nombre completo") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = CircleShape,
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = uiState.bio,
                    onValueChange = { viewModel.onBioChange(it) },
                    label = { Text("Biografía") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    shape = MaterialTheme.shapes.large,
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text("Género", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))

                // Selector de Género
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    val genders = listOf("female" to "Mujer", "male" to "Hombre", "other" to "Otro")
                    genders.forEach { (value, label) ->
                        FilterChip(
                            selected = uiState.gender == value,
                            onClick = { viewModel.onGenderChange(value) },
                            label = { Text(label, modifier = Modifier.padding(horizontal = 8.dp)) },
                            shape = MaterialTheme.shapes.large,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }

                uiState.error?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }
    }
}