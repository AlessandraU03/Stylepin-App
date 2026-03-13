package com.ale.stylepin.features.profile.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
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
import com.ale.stylepin.features.profile.presentation.viewmodels.PublicProfileViewModel
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicProfileScreen(userId: String, viewModel: PublicProfileViewModel, onBack: () -> Unit, onPinClick: (String) -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }
    LaunchedEffect(userId) { viewModel.loadProfile(userId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.profile?.username ?: "Cargando...", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Atrás") } }
            )
        }
    ) { padding ->
        if (uiState.isLoading) { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else if (uiState.profile != null) {
            val profile = uiState.profile!!
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                modifier = Modifier.fillMaxSize().padding(padding).background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalItemSpacing = 8.dp
            ) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        AsyncImage(model = profile.avatarUrl.ifEmpty { "https://ui-avatars.com/api/?name=${profile.fullName.replace(" ", "+")}" }, contentDescription = "Avatar", modifier = Modifier.size(100.dp).clip(CircleShape).background(Color.LightGray), contentScale = ContentScale.Crop)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = profile.fullName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(text = "@${profile.username}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (profile.bio.isNotEmpty()) { Text(profile.bio, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp)) }
                        Button(onClick = { viewModel.toggleFollow() }, modifier = Modifier.fillMaxWidth(0.5f).padding(vertical = 12.dp), colors = ButtonDefaults.buttonColors(containerColor = if (profile.isFollowing) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary, contentColor = if (profile.isFollowing) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onPrimary)) {
                            Text(if (profile.isFollowing) "Siguiendo" else "Seguir")
                        }
                        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                            ProfileStatItem(formatStatNumber(profile.pinsCount), "PINS")
                            ProfileStatItem(formatStatNumber(profile.followersCount), "SEGUIDORES")
                            ProfileStatItem(formatStatNumber(profile.followingCount), "SIGUIENDO")
                        }
                    }
                }
                item(span = StaggeredGridItemSpan.FullLine) {
                    TabRow(selectedTabIndex = selectedTab) {
                        listOf("Pins", "Tableros", "Guardados").forEachIndexed { index, text -> Tab(selected = selectedTab == index, onClick = { selectedTab = index }, text = { Text(text) }) }
                    }
                }
                if (uiState.isLoadingContent) {
                    item(span = StaggeredGridItemSpan.FullLine) { Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() } }
                } else {
                    when (selectedTab) {
                        0 -> { items(uiState.pins) { pin -> PublicGridImageItem(pin.imageUrl) { onPinClick(pin.id) } } }
                        1 -> { items(uiState.boards) { board ->
                            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                                AsyncImage(model = board.coverImageUrl.ifEmpty { "https://placehold.co/400x400/png" }, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxWidth().aspectRatio(1f).clip(RoundedCornerShape(16.dp)).background(Color.LightGray))
                                Text(text = board.name, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp, start = 4.dp))
                            }
                        } }
                        2 -> { items(uiState.savedPins) { saved -> PublicGridImageItem(saved.imageUrl) { onPinClick(saved.pinId) } } }
                    }
                }
            }
        }
    }
}
@Composable
fun PublicGridImageItem(imageUrl: String, onClick: () -> Unit) {
    val height = remember { Random.nextInt(150, 300).dp }
    AsyncImage(model = imageUrl.ifEmpty { "https://placehold.co/400x600/png" }, contentDescription = "Pin", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxWidth().height(height).clip(RoundedCornerShape(16.dp)).background(Color.LightGray).clickable { onClick() })
}