package com.ale.stylepin.features.auth.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ale.stylepin.features.auth.presentation.components.StylePinPasswordField
import com.ale.stylepin.features.auth.presentation.components.StylePinTextField
import com.ale.stylepin.features.auth.presentation.viewmodels.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit
) {
    var identity by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val uiState = viewModel.uiState

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "StylePin", style = MaterialTheme.typography.displayMedium, color = MaterialTheme.colorScheme.primary)

        Spacer(modifier = Modifier.height(32.dp))

        StylePinTextField(value = identity, onValueChange = { identity = it }, label = "Usuario")
        Spacer(modifier = Modifier.height(16.dp))
        StylePinPasswordField(value = password, onValueChange = { password = it }, label = "Contraseña")

        Spacer(modifier = Modifier.height(24.dp))

        // Corrección imagen image_7310a0.png: Acceso directo a la propiedad
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
        }

        // Corrección imagen image_730940.jpg: Observar el booleano de éxito
        LaunchedEffect(uiState.isLoginSuccess) {
            if (uiState.isLoginSuccess) {
                onLoginSuccess()
            }
        }

        // Corrección error: Mostrar mensaje si existe
        uiState.error?.let { msg ->
            Text(text = msg, color = Color.Red, modifier = Modifier.padding(top = 16.dp))
        }
    }
}