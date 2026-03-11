package com.ale.stylepin.features.auth.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AlternateEmail
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    val isFormValid = uiState.username.isNotBlank() && uiState.email.contains("@") &&
            uiState.password.length >= 8 && uiState.fullName.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "StylePin",
            fontSize = 48.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(48.dp))

        StylePinTextField(
            value = uiState.fullName,
            onValueChange = { viewModel.onFullNameChanged(it) },
            label = "Nombre completo",
            icon = Icons.Outlined.Person
        )
        Spacer(modifier = Modifier.height(16.dp))

        StylePinTextField(
            value = uiState.username,
            onValueChange = { viewModel.onUsernameChanged(it) },
            label = "Usuario",
            icon = Icons.Outlined.AlternateEmail
        )
        Spacer(modifier = Modifier.height(16.dp))

        StylePinTextField(
            value = uiState.email,
            onValueChange = { viewModel.onEmailChanged(it) },
            label = "Correo electrónico",
            icon = Icons.Outlined.Email
        )
        Spacer(modifier = Modifier.height(16.dp))

        StylePinPasswordField(
            value = uiState.password,
            onValueChange = { viewModel.onPasswordChanged(it) },
            label = "Contraseña",
            icon = Icons.Outlined.Lock
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Selector de Género
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            val genders = listOf("female" to "Mujer", "male" to "Hombre", "other" to "Otro")
            genders.forEach { (value, label) ->
                FilterChip(
                    selected = uiState.gender == value,
                    onClick = { viewModel.onGenderChanged(value) },
                    label = { Text(label, modifier = Modifier.padding(horizontal = 8.dp)) },
                    shape = MaterialTheme.shapes.large,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { viewModel.register() },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = MaterialTheme.shapes.extraLarge,
            enabled = isFormValid && !uiState.isLoading
        ) {
            if (uiState.isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            else Text("Registrarse", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onNavigateToLogin) {
            Text("¿Ya tienes cuenta? Entrar", color = MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.height(48.dp))

        LaunchedEffect(uiState.isLoginSuccess) {
            if (uiState.isLoginSuccess) onRegisterSuccess()
        }
    }
}