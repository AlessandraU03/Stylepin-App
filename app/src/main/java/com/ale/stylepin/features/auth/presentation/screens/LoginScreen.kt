package com.ale.stylepin.features.auth.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
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
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "StylePin",
            fontSize = 48.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(48.dp))

        StylePinTextField(
            value = uiState.username,
            onValueChange = { viewModel.onIdentityChanged(it) },
            label = "Usuario",
            icon = Icons.Outlined.Person
        )

        Spacer(modifier = Modifier.height(16.dp))

        StylePinPasswordField(
            value = uiState.password,
            onValueChange = { viewModel.onPasswordChanged(it) },
            label = "Contraseña",
            icon = Icons.Outlined.Lock
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { viewModel.login() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = MaterialTheme.shapes.extraLarge,
            enabled = uiState.username.isNotEmpty() && uiState.password.isNotEmpty() && !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Entrar", fontSize = 16.sp)
            }
        }

        if (viewModel.canUseBiometrics()) {
            Spacer(modifier = Modifier.height(16.dp))
            IconButton(
                onClick = {
                    val activity = context as? FragmentActivity
                    activity?.let { viewModel.loginWithBiometrics(it) }
                },
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Fingerprint,
                    contentDescription = "Login con huella",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("¿No tienes cuenta? ", color = MaterialTheme.colorScheme.onSurfaceVariant)
            TextButton(onClick = onNavigateToRegister, contentPadding = PaddingValues(0.dp)) {
                Text("Regístrate", color = MaterialTheme.colorScheme.primary)
            }
        }

        LaunchedEffect(uiState.isLoginSuccess) {
            if (uiState.isLoginSuccess) onLoginSuccess()
        }

        uiState.error?.let { msg ->
            Text(text = msg, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 16.dp))
        }
    }
}
