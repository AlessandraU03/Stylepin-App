package com.ale.stylepin.features.profile.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onBack: () -> Unit,
    onEditProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onCommunityClick: (Int) -> Unit,
    onPinClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("StylePin", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Atrás") } },
                actions = {
                    IconButton(onClick = { }) { Icon(Icons.Outlined.Share, contentDescription = "Compartir") }
                    IconButton(onClick = onSettingsClick) { Icon(Icons.Outlined.MoreVert, contentDescription = "Más opciones") }
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
                // Usamos StaggeredGrid como contenedor principal para que la cabecera pueda scrollear
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize().padding(padding).background(MaterialTheme.colorScheme.background),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalItemSpacing = 8.dp
                ) {
                    // 1. LA CABECERA DEL PERFIL (Ocupa las 2 columnas)
                    item(span = StaggeredGridItemSpan.FullLine) {
                        ProfileHeader(
                            profile = profile,
                            onEditProfileClick = onEditProfileClick,
                            onCommunityClick = onCommunityClick
                        )
                    }

                    // 2. LAS PESTAÑAS (Ocupa las 2 columnas)
                    item(span = StaggeredGridItemSpan.FullLine) {
                        TabRow(
                            selectedTabIndex = selectedTab,
                            containerColor = MaterialTheme.colorScheme.background,
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            listOf("Pins", "Tableros", "Guardados").forEachIndexed { index, text ->
                                Tab(
                                    selected = selectedTab == index,
                                    onClick = { selectedTab = index },
                                    text = { Text(text, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
                                )
                            }
                        }
                    }

                    // 3. EL CONTENIDO DE LA CUADRÍCULA SEGÚN LA PESTAÑA
                    if (uiState.isLoadingContent) {
                        item(span = StaggeredGridItemSpan.FullLine) {
                            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                    } else {
                        when (selectedTab) {
                            0 -> { // PINS
                                if (uiState.pins.isEmpty()) {
                                    item(span = StaggeredGridItemSpan.FullLine) { EmptyStateMessage("Aún no tienes pins creados") }
                                } else {
                                    items(uiState.pins) { pin ->
                                        GridImageItem(
                                            imageUrl = pin.imageUrl,
                                            onClick = { onPinClick(pin.id) } // <--- Mandamos el clic con el ID
                                        )
                                    }
                                }
                            }

                            1 -> { // TABLEROS
                                if (uiState.boards.isEmpty()) {
                                    item(span = StaggeredGridItemSpan.FullLine) { EmptyStateMessage("Aún no tienes tableros") }
                                } else {
                                    items(uiState.boards) { board ->
                                        GridBoardItem(name = board.name, imageUrl = board.coverImageUrl, count = board.pinsCount)
                                    }
                                }
                            }
                            2 -> { // GUARDADOS
                                if (uiState.savedPins.isEmpty()) {
                                    item(span = StaggeredGridItemSpan.FullLine) { EmptyStateMessage("No has guardado ningún pin") }
                                } else {
                                    items(uiState.savedPins) { saved ->
                                        // ¡AQUÍ FALTABA EL onClick!
                                        GridImageItem(
                                            imageUrl = saved.imageUrl,
                                            onClick = { onPinClick(saved.pinId) }
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
}

// --- SUB-COMPONENTES DE LA PANTALLA ---

@Composable
fun ProfileHeader(
    profile: com.ale.stylepin.features.profile.domain.entities.Profile,
    onEditProfileClick: () -> Unit,
    onCommunityClick: (Int) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        AsyncImage(
            model = profile.avatarUrl.ifEmpty { "https://ui-avatars.com/api/?name=${profile.fullName.replace(" ", "+")}&background=random" },
            contentDescription = "Avatar",
            modifier = Modifier.size(100.dp).clip(CircleShape).background(Color.LightGray),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = profile.fullName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(text = "@${profile.username}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

        if (profile.bio.isNotEmpty()) {
            Text(
                text = profile.bio,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp)
            )
        }

        Button(
            onClick = onEditProfileClick,
            modifier = Modifier.fillMaxWidth(0.6f).padding(vertical = 12.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Text("Editar Perfil")
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.padding(8.dp), contentAlignment = Alignment.Center) {
                ProfileStatItem(formatStatNumber(profile.pinsCount), "PINS")
            }
            Box(modifier = Modifier.clip(MaterialTheme.shapes.small).clickable { onCommunityClick(0) }.padding(8.dp), contentAlignment = Alignment.Center) {
                ProfileStatItem(formatStatNumber(profile.followersCount), "SEGUIDORES")
            }
            Box(modifier = Modifier.clip(MaterialTheme.shapes.small).clickable { onCommunityClick(1) }.padding(8.dp), contentAlignment = Alignment.Center) {
                ProfileStatItem(formatStatNumber(profile.followingCount), "SIGUIENDO")
            }
        }
    }
}

@Composable
fun GridImageItem(imageUrl: String, onClick: () -> Unit) { // <--- Agregamos onClick aquí
    val height = remember { kotlin.random.Random.nextInt(150, 300).dp }

    AsyncImage(
        model = imageUrl.ifEmpty { "https://placehold.co/400x600/png" },
        contentDescription = "Pin",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.LightGray)
            .clickable { onClick() } // <--- ¡Esto hace que la imagen sea tocable!
    )
}

@Composable
fun GridBoardItem(name: String, imageUrl: String, count: Int) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
        AsyncImage(
            model = imageUrl.ifEmpty { "https://placehold.co/400x400/png" },
            contentDescription = "Board Cover",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f) // Cuadrado para los tableros
                .clip(RoundedCornerShape(16.dp))
                .background(Color.LightGray)
        )
        Text(text = name, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp, start = 4.dp))
        Text(text = "$count pines", style = MaterialTheme.typography.bodySmall, color = Color.Gray, modifier = Modifier.padding(start = 4.dp))
    }
}

@Composable
fun EmptyStateMessage(message: String) {
    Text(
        text = message,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(32.dp)
    )
}