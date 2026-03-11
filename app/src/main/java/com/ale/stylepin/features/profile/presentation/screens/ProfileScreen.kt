package com.ale.stylepin.features.profile.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.ale.stylepin.features.profile.presentation.components.ProfileStatItem
import com.ale.stylepin.features.profile.presentation.components.formatStatNumber
import com.ale.stylepin.features.profile.presentation.viewmodels.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onBack: () -> Unit,
    onEditProfileClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("StylePin", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Atrás") }
                },
                actions = {
                    IconButton(onClick = { /* Acción compartir */ }) { Icon(Icons.Outlined.Share, contentDescription = "Compartir") }
                    IconButton(onClick = { /* Menú opciones */ }) { Icon(Icons.Outlined.MoreVert, contentDescription = "Más opciones") }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.error != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: ${uiState.error}", color = MaterialTheme.colorScheme.error)
            }
        } else {
            uiState.profile?.let { profile ->
                Column(
                    modifier = Modifier.fillMaxSize().padding(padding).background(MaterialTheme.colorScheme.background),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Avatar
                    AsyncImage(
                        model = profile.avatarUrl.ifEmpty { "https://ui-avatars.com/api/?name=${profile.fullName.replace(" ", "+")}&background=random" },
                        contentDescription = "Avatar de ${profile.fullName}",
                        modifier = Modifier.size(100.dp).clip(CircleShape).background(Color.LightGray),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Info principal
                    Text(text = profile.fullName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(text = "@${profile.username} • ${formatStatNumber(profile.followersCount)} seguidores", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    // Bio
                    Text(
                        text = profile.bio,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp)
                    )

                    // Botones de acción
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = onEditProfileClick,
                            modifier = Modifier.weight(1f),
                            shape = MaterialTheme.shapes.large
                        ) {
                            Text("Editar Perfil")
                        }
                        OutlinedButton(
                            onClick = { /* Mensaje o Configuración */ },
                            modifier = Modifier.weight(1f),
                            shape = MaterialTheme.shapes.large
                        ) {
                            Text("Mensaje")
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Estadísticas (Pins, Seguidores, Siguiendo)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ProfileStatItem(formatStatNumber(profile.pinsCount), "PINS")
                        ProfileStatItem(formatStatNumber(profile.followersCount), "SEGUIDORES")
                        ProfileStatItem(formatStatNumber(profile.followingCount), "SIGUIENDO")
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Tabs
                    var selectedTab by remember { mutableIntStateOf(0) }
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = MaterialTheme.colorScheme.background
                    ) {
                        listOf("Pins", "Tableros", "Guardados").forEachIndexed { index, text ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = { Text(text, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
                            )
                        }
                    }

                    // Aquí iría el contenido de los Tabs (LazyVerticalGrid)
                    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                        Text("Contenido de ${listOf("Pins", "Tableros", "Guardados")[selectedTab]}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}