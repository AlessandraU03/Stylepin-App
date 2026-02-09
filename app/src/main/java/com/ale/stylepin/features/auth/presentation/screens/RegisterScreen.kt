package com.ale.stylepin.features.auth.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ale.stylepin.features.auth.presentation.components.StylePinPasswordField
import com.ale.stylepin.features.auth.presentation.components.StylePinTextField
import com.ale.stylepin.features.auth.presentation.viewmodels.RegisterViewModel

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    // Observamos el estado del StateFlow (Igual que en Rick y Morty)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    // Validaciones visuales (Derivadas del estado)
    val passwordHasUppercase = uiState.password.any { it.isUpperCase() }
    val isFormValid = uiState.username.isNotBlank() &&
            uiState.email.contains("@") &&
            uiState.password.length >= 8 &&
            passwordHasUppercase &&
            uiState.fullName.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "StylePin", style = MaterialTheme.typography.displayMedium, color = MaterialTheme.colorScheme.primary)
        Text(text = "Crea tu cuenta", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(32.dp))

        // CAMPOS CONECTADOS AL VIEWMODEL
        StylePinTextField(
            value = uiState.fullName,
            onValueChange = { viewModel.onFullNameChanged(it) },
            label = "Nombre Completo"
        )
        Spacer(modifier = Modifier.height(16.dp))

        StylePinTextField(
            value = uiState.username,
            onValueChange = { viewModel.onUsernameChanged(it) },
            label = "Nombre de usuario"
        )
        Spacer(modifier = Modifier.height(16.dp))

        StylePinTextField(
            value = uiState.email,
            onValueChange = { viewModel.onEmailChanged(it) },
            label = "Correo electrónico"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // SELECTOR DE GÉNERO
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            listOf("male" to "Hombre", "female" to "Mujer", "other" to "Otro").forEach { (value, label) ->
                FilterChip(
                    selected = uiState.gender == value,
                    onClick = { viewModel.onGenderChanged(value) },
                    label = { Text(label) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        StylePinPasswordField(
            value = uiState.password,
            onValueChange = { viewModel.onPasswordChanged(it) },
            label = "Contraseña"
        )

        Spacer(modifier = Modifier.height(24.dp))

        // REACCIÓN AL ESTADO (Carga / Botón)
        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = { viewModel.register() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = isFormValid
            ) {
                Text("Registrarse")
            }

            TextButton(onClick = onNavigateToLogin) {
                Text("¿Ya tienes cuenta? Inicia sesión")
            }
        }

        // MANEJO DE ERRORES
        uiState.error?.let { msg ->
            Text(text = msg, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 16.dp))
        }

        // NAVEGACIÓN EXITOSA
        LaunchedEffect(uiState.isLoginSuccess) {
            if (uiState.isLoginSuccess) {
                onRegisterSuccess()
            }
        }
    }
}