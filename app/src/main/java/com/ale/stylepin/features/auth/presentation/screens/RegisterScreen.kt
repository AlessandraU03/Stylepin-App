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
import androidx.lifecycle.compose.collectAsStateWithLifecycle // Importación clave
import com.ale.stylepin.features.auth.presentation.components.StylePinPasswordField
import com.ale.stylepin.features.auth.presentation.components.StylePinTextField
import com.ale.stylepin.features.auth.presentation.viewmodels.RegisterViewModel

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    // 1. Mantener estados locales para el formulario
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("male") }

    // 2. Recolectar el estado del ViewModel usando collectAsStateWithLifecycle
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    // Lógica de validación (Se mantiene en la UI por ser puramente visual)
    val passwordHasUppercase = password.any { it.isUpperCase() }
    val passwordValid = password.length >= 8 && passwordHasUppercase
    val isFormValid = username.isNotBlank() &&
            email.contains("@") &&
            passwordValid &&
            fullName.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "StylePin",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(text = "Crea tu cuenta", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(32.dp))

        // --- CAMPOS DE TEXTO ---
        StylePinTextField(value = fullName, onValueChange = { fullName = it }, label = "Nombre Completo")
        Spacer(modifier = Modifier.height(16.dp))

        StylePinTextField(value = username, onValueChange = { username = it }, label = "Nombre de usuario")
        Spacer(modifier = Modifier.height(16.dp))

        StylePinTextField(value = email, onValueChange = { email = it }, label = "Correo electrónico")
        if (email.isNotEmpty() && !email.contains("@")) {
            Text(text = "Email inválido", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.height(16.dp))

        // --- SELECTOR DE GÉNERO ---
        Text("Género:", style = MaterialTheme.typography.bodyMedium)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            listOf("male" to "Hombre", "female" to "Mujer", "other" to "Otro").forEach { (value, label) ->
                FilterChip(
                    selected = gender == value,
                    onClick = { gender = value },
                    label = { Text(label) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        StylePinPasswordField(value = password, onValueChange = { password = it }, label = "Contraseña")

        // --- VALIDACIÓN VISUAL ---
        if (password.isNotEmpty()) {
            Column(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                Text(
                    text = if (password.length >= 8) "✓ Mínimo 8 caracteres" else "✗ Mínimo 8 caracteres",
                    color = if (password.length >= 8) Color.Green else MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = if (passwordHasUppercase) "✓ Una letra mayúscula" else "✗ Una letra mayúscula",
                    color = if (passwordHasUppercase) Color.Green else MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 3. Reacción al estado de carga de uiState
        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = { viewModel.register(username, email, password, fullName, gender) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = isFormValid
            ) {
                Text("Registrarse")
            }

            TextButton(onClick = onNavigateToLogin) {
                Text("¿Ya tienes cuenta? Inicia sesión")
            }
        }

        // 4. Navegación al éxito
        LaunchedEffect(uiState.isLoginSuccess) {
            if (uiState.isLoginSuccess) {
                onRegisterSuccess()
            }
        }

        // 5. Manejo de errores desde el StateFlow
        uiState.error?.let { msg ->
            Text(
                text = msg,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 16.dp),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}