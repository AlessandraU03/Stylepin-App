package com.ale.stylepin.features.profile.presentation.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.ale.stylepin.features.profile.presentation.viewmodels.PublicProfileViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PublicProfileScreen(
    userId: String,
    viewModel: PublicProfileViewModel,
    onBack: () -> Unit,
    onNavigateToPinDetail: (String) -> Unit,
    onNavigateToBoardDetail: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableStateOf(0) }

    LaunchedEffect(userId) {
        viewModel.loadProfile(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.profile?.username ?: "") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.profile != null) {
            val profile = uiState.profile!!

            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                // CABECERA DEL PERFIL
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val fallbackUrl = "https://ui-avatars.com/api/?name=${profile.fullName.replace(" ", "+")}&background=random"
                    AsyncImage(
                        model = profile.avatarUrl.takeIf { it.isNotBlank() } ?: fallbackUrl,
                        contentDescription = "Avatar",
                        modifier = Modifier.size(100.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = profile.fullName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text(text = "@${profile.username}", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

                    if (profile.bio.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = profile.bio, style = MaterialTheme.typography.bodyMedium)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${profile.followersCount}", fontWeight = FontWeight.Bold)
                            Text("Seguidores", color = Color.Gray)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${profile.followingCount}", fontWeight = FontWeight.Bold)
                            Text("Siguiendo", color = Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // BOTÓN DE SEGUIR
                    Button(
                        onClick = { viewModel.toggleFollow() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (uiState.isFollowing) Color.Black else MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth(0.5f)
                    ) {
                        Text(if (uiState.isFollowing) "Siguiendo" else "Seguir")
                    }
                }

                // PESTAÑAS (Pines y Tableros Públicos)
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Pines") })
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Tableros") })
                }

                // CONTENIDO DE LAS PESTAÑAS
                when (selectedTab) {
                    0 -> { // PINES
                        if (uiState.publicPins.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Aún no tiene pines públicos", color = Color.Gray)
                            }
                        } else {
                            LazyVerticalStaggeredGrid(
                                columns = StaggeredGridCells.Fixed(2),
                                contentPadding = PaddingValues(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalItemSpacing = 8.dp,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(uiState.publicPins, key = { it.id }) { pin ->
                                    AsyncImage(
                                        model = pin.imageUrl,
                                        contentDescription = pin.title,
                                        modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 120.dp).clip(RoundedCornerShape(12.dp)).clickable { onNavigateToPinDetail(pin.id) },
                                        contentScale = ContentScale.FillWidth
                                    )
                                }
                            }
                        }
                    }
                    1 -> { // TABLEROS
                        if (uiState.publicBoards.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Aún no tiene tableros públicos", color = Color.Gray)
                            }
                        } else {
                            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(uiState.publicBoards, key = { it.id }) { board ->
                                    ListItem(
                                        headlineContent = { Text(board.name, fontWeight = FontWeight.Medium) },
                                        supportingContent = { Text("${board.pinsCount} pines") },
                                        modifier = Modifier.clickable { onNavigateToBoardDetail(board.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}