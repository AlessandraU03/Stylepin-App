package com.ale.stylepin.features.auth.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ale.stylepin.features.auth.presentation.components.StylePinPasswordField
import com.ale.stylepin.features.auth.presentation.components.StylePinTextField
import com.ale.stylepin.features.auth.presentation.viewmodels.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "StylePin",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Identidad (Username/Email) vinculado al ViewModel
        StylePinTextField(
            value = uiState.username,
            onValueChange = { viewModel.onIdentityChanged(it) },
            label = "Usuario o Correo"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password vinculado al ViewModel
        StylePinPasswordField(
            value = uiState.password,
            onValueChange = { viewModel.onPasswordChanged(it) },
            label = "Contraseña"
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = { viewModel.login() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                // Lógica de habilitación derivada directamente del estado
                enabled = uiState.username.isNotEmpty() && uiState.password.isNotEmpty()
            ) {
                Text("Iniciar Sesión")
            }

            TextButton(onClick = onNavigateToRegister) {
                Text("¿No tienes cuenta? Regístrate aquí")
            }
        }

        // Navegación reactiva
        LaunchedEffect(uiState.isLoginSuccess) {
            if (uiState.isLoginSuccess) {
                onLoginSuccess()
            }
        }

        // Errores centralizados
        uiState.error?.let { msg ->
            Text(
                text = msg,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}