package com.ale.stylepin.features.profile.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.ale.stylepin.features.profile.presentation.viewmodels.PublicProfileViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicProfileScreen(
    userId: String,
    viewModel: PublicProfileViewModel,
    onBack: () -> Unit,
    onNavigateToPinDetail: (String) -> Unit,
    onNavigateToBoardDetail: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(userId) { viewModel.loadProfile(userId) }

    // --- LÓGICA DE SCROLL EXACTAMENTE IGUAL AL PERFIL PERSONAL ---
    val profileInfoHeight = 320.dp
    val tabHeight = 48.dp
    val density = LocalDensity.current
    val profileInfoHeightPx = with(density) { profileInfoHeight.toPx() }
    val tabHeightPx = with(density) { tabHeight.toPx() }
    var headerOffsetPx by remember { mutableFloatStateOf(0f) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                if (delta < 0) {
                    val newOffset = headerOffsetPx + delta
                    val clamped = newOffset.coerceIn(-profileInfoHeightPx, 0f)
                    val consumed = clamped - headerOffsetPx
                    headerOffsetPx = clamped
                    return Offset(0f, consumed)
                }
                return Offset.Zero
            }
            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                if (available.y > 0) {
                    val newOffset = headerOffsetPx + available.y
                    val clamped = newOffset.coerceIn(-profileInfoHeightPx, 0f)
                    val consumedY = clamped - headerOffsetPx
                    headerOffsetPx = clamped
                    return Offset(0f, consumedY)
                }
                return Offset.Zero
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.profile?.username ?: "") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") } }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.profile != null) {
            val profile = uiState.profile!!

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .nestedScroll(nestedScrollConnection)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // CONTENIDO DESLIZABLE (Pines y Tableros)
                Box(
                    modifier = Modifier.fillMaxSize().layout { measurable, constraints ->
                        val gridHeight = constraints.maxHeight - tabHeightPx.roundToInt()
                        val placeable = measurable.measure(constraints.copy(minHeight = gridHeight, maxHeight = gridHeight))
                        layout(placeable.width, constraints.maxHeight) {
                            val yPosition = (profileInfoHeightPx + headerOffsetPx + tabHeightPx).roundToInt()
                            placeable.placeRelative(0, yPosition)
                        }
                    }
                ) {
                    when (selectedTab) {
                        0 -> { // PINES
                            if (uiState.publicPins.isEmpty()) {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) { Text("Aún no tiene pines", color = Color.Gray, modifier = Modifier.padding(top = 32.dp)) }
                            } else {
                                LazyVerticalStaggeredGrid(
                                    columns = StaggeredGridCells.Fixed(2),
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(bottom = 100.dp, start = 8.dp, end = 8.dp, top = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalItemSpacing = 12.dp
                                ) {
                                    items(uiState.publicPins, key = { it.id }) { pin ->
                                        Column(modifier = Modifier.fillMaxWidth().clickable { onNavigateToPinDetail(pin.id) }) {
                                            AsyncImage(
                                                model = pin.imageUrl, contentDescription = pin.title,
                                                modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 100.dp).clip(RoundedCornerShape(16.dp)),
                                                contentScale = ContentScale.FillWidth
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        1 -> { // TABLEROS
                            if (uiState.publicBoards.isEmpty()) {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) { Text("No tiene tableros públicos", color = Color.Gray, modifier = Modifier.padding(top = 32.dp)) }
                            } else {
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(2),
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(bottom = 100.dp, start = 12.dp, end = 12.dp, top = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    items(uiState.publicBoards, key = { it.id }) { board ->
                                        Column(modifier = Modifier.fillMaxWidth().clickable { onNavigateToBoardDetail(board.id) }) {
                                            Box(modifier = Modifier.fillMaxWidth().aspectRatio(1f).clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surfaceVariant)) {
                                                if (!board.coverImageUrl.isNullOrBlank()) {
                                                    AsyncImage(model = board.coverImageUrl, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                                                }
                                            }
                                            Text(text = board.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp), maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // CABECERA Y TABS
                Column(
                    modifier = Modifier.fillMaxWidth().offset { IntOffset(0, headerOffsetPx.roundToInt()) }.zIndex(1f)
                ) {
                    Box(modifier = Modifier.fillMaxWidth().height(profileInfoHeight).background(MaterialTheme.colorScheme.background)) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val fallbackUrl = "https://ui-avatars.com/api/?name=${profile.fullName.replace(" ", "+")}&background=random"
                            AsyncImage(
                                model = profile.avatarUrl.takeIf { it.isNotBlank() } ?: fallbackUrl,
                                contentDescription = "Avatar",
                                modifier = Modifier.size(100.dp).clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(text = profile.fullName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text(text = "@${profile.username}", color = MaterialTheme.colorScheme.onSurfaceVariant)

                            if (profile.bio.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = profile.bio, style = MaterialTheme.typography.bodyMedium)
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // ESTADÍSTICAS REALES
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                StatItemProfile(value = "${profile.pinsCount}", label = "Pins", onClick = null)
                                StatItemProfile(value = "${profile.followersCount}", label = "Seguidores", onClick = null)
                                StatItemProfile(value = "${profile.followingCount}", label = "Siguiendo", onClick = null)
                            }

                            Spacer(modifier = Modifier.height(16.dp))

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
                    }

                    TabRow(selectedTabIndex = selectedTab, containerColor = MaterialTheme.colorScheme.background, modifier = Modifier.height(tabHeight)) {
                        Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Pines", fontWeight = if(selectedTab == 0) FontWeight.Bold else FontWeight.Normal) })
                        Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Tableros", fontWeight = if(selectedTab == 1) FontWeight.Bold else FontWeight.Normal) })
                    }
                }
            }
        }
    }
}