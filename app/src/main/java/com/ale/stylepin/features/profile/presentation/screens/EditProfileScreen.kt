package com.ale.stylepin.features.profile.presentation.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.ale.stylepin.features.profile.presentation.viewmodels.EditProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(viewModel: EditProfileViewModel, onBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { viewModel.onAvatarSelected(it.toString()) }
    }

    LaunchedEffect(uiState.isSuccess) { if (uiState.isSuccess) onBack() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Perfil", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Outlined.Close, "Cancelar") } },
                actions = { TextButton(onClick = { viewModel.saveProfile() }, enabled = !uiState.isLoading) { Text("Guardar") } }
            )
        }
    ) { padding ->
        if (uiState.isLoading) { Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(24.dp))
                Box(modifier = Modifier.size(100.dp).clip(CircleShape).background(Color.LightGray).clickable { galleryLauncher.launch("image/*") }, contentAlignment = Alignment.Center) {
                    val imageToDisplay = uiState.newAvatarUri ?: uiState.avatarUrl.ifEmpty { "https://ui-avatars.com/api/?name=${uiState.fullName.replace(" ", "+")}" }
                    AsyncImage(model = imageToDisplay, contentDescription = "Avatar", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.PhotoCamera, contentDescription = "Cambiar", tint = Color.White)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(value = uiState.fullName, onValueChange = { viewModel.onFullNameChange(it) }, label = { Text("Nombre completo") }, modifier = Modifier.fillMaxWidth(), shape = CircleShape)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(value = uiState.bio, onValueChange = { viewModel.onBioChange(it) }, label = { Text("Biografía") }, modifier = Modifier.fillMaxWidth().height(120.dp), shape = MaterialTheme.shapes.large)
            }
        }
    }
}