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
    // 1. Estados locales para los inputs (esto sigue igual, son de la vista)
    var identity by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // 2. RECOLECCIÓN DEL ESTADO: Usamos collectAsStateWithLifecycle
    // Esto requiere la dependencia: androidx.lifecycle:lifecycle-runtime-compose
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

        StylePinTextField(
            value = identity,
            onValueChange = { identity = it },
            label = "Usuario"
        )
        Spacer(modifier = Modifier.height(16.dp))

        StylePinPasswordField(
            value = password,
            onValueChange = { password = it },
            label = "Contraseña"
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 3. Uso del estado recolectado (uiState)
        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = { viewModel.login(identity, password) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = identity.isNotEmpty() && password.isNotEmpty()
            ) {
                Text("Iniciar Sesión")
            }

            TextButton(
                onClick = onNavigateToRegister,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("¿No tienes cuenta? Regístrate aquí")
            }
        }

        // 4. Efecto para navegación (reacciona al cambio en isLoginSuccess)
        LaunchedEffect(uiState.isLoginSuccess) {
            if (uiState.isLoginSuccess) {
                onLoginSuccess()
            }
        }

        // 5. Mostrar errores
        uiState.error?.let { msg ->
            Text(
                text = msg,
                color = Color.Red,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}