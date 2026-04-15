package com.ale.stylepin.features.profile.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ale.stylepin.features.auth.presentation.components.StylePinPasswordField
import com.ale.stylepin.features.profile.presentation.viewmodels.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToSync: () -> Unit   // ← NUEVO parámetro
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(uiState.accountDeleted) {
        if (uiState.accountDeleted) onLogout()
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { if (!uiState.isLoading) showDeleteDialog = false },
            title = { Text("Eliminar Cuenta Definitivamente", color = MaterialTheme.colorScheme.error) },
            text = {
                Column {
                    Text("Esta acción es irreversible. Se perderán todos tus tableros y pins. Por seguridad, ingresa tu contraseña:")
                    Spacer(modifier = Modifier.height(16.dp))
                    StylePinPasswordField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Tu contraseña"
                    )
                    uiState.error?.let {
                        Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.deleteAccount(password) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    enabled = !uiState.isLoading && password.isNotBlank()
                ) {
                    if (uiState.isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                    else Text("Eliminar Cuenta")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    viewModel.clearError()
                    password = ""
                }, enabled = !uiState.isLoading) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuración", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Cuenta",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            HorizontalDivider()

            ListItem(
                headlineContent = { Text("Cerrar sesión") },
                leadingContent = { Icon(Icons.Default.Logout, contentDescription = null) },
                modifier = Modifier.fillMaxWidth().clickable { onLogout() }
            )

            ListItem(
                headlineContent = { Text("Eliminar cuenta", color = MaterialTheme.colorScheme.error) },
                leadingContent = {
                    Icon(
                        Icons.Default.DeleteForever,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                },
                modifier = Modifier.fillMaxWidth().clickable { showDeleteDialog = true }
            )

            Spacer(Modifier.height(8.dp))
            Text(
                "Datos",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            HorizontalDivider()

            // ← NUEVO: opción de sincronización
            ListItem(
                headlineContent = { Text("Sincronización de pines") },
                supportingContent = { Text("Sincronizar pines sin conexión") },
                leadingContent = {
                    Icon(Icons.Default.Sync, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth().clickable { onNavigateToSync() }
            )
        }
    }
}