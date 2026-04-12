package com.ale.stylepin.features.profile.presentation.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Share
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.ale.stylepin.features.boards.domain.entities.Board
import com.ale.stylepin.features.pins.domain.entities.Pin
import com.ale.stylepin.features.profile.presentation.viewmodels.ProfileViewModel
import kotlin.math.roundToInt

// ── Componentes auxiliares ────────────────────────────────────────────────────

@Composable
fun StatItemProfile(value: String, label: String, onClick: (() -> Unit)? = null) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .let { if (onClick != null) it.clickable { onClick() } else it }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray,
            letterSpacing = 1.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

fun formatNumber(number: Int): String = when {
    number >= 1_000_000 -> String.format("%.1fm", number / 1_000_000.0)
    number >= 1_000     -> String.format("%.1fk", number / 1_000.0)
    else                -> number.toString()
}

// ── Pantalla principal ────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onBack: () -> Unit,
    onEditProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onCommunityClick: (Int) -> Unit,
    onNavigateToPinDetail: (String) -> Unit,
    onNavigateToBoardDetail: (String) -> Unit,
    onNavigateToCreateBoard: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val photoPickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> if (uri != null) viewModel.updateAvatar(uri) }

    var selectedTab by remember { mutableIntStateOf(0) }
    val pinsGridState  = rememberLazyStaggeredGridState()
    val boardsGridState = rememberLazyGridState()
    val savedGridState  = rememberLazyStaggeredGridState()

    // Diálogo confirmación para quitar un guardado
    var pinToRemove by remember { mutableStateOf<String?>(null) }

    val profileInfoHeight   = 380.dp
    val tabHeight           = 48.dp
    val density             = LocalDensity.current
    val profileInfoHeightPx = with(density) { profileInfoHeight.toPx() }
    val tabHeightPx         = with(density) { tabHeight.toPx() }
    var headerOffsetPx by remember { mutableFloatStateOf(0f) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y < 0) {
                    val clamped = (headerOffsetPx + available.y).coerceIn(-profileInfoHeightPx, 0f)
                    val consumed = clamped - headerOffsetPx
                    headerOffsetPx = clamped
                    return Offset(0f, consumed)
                }
                return Offset.Zero
            }
            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                if (available.y > 0) {
                    val clamped = (headerOffsetPx + available.y).coerceIn(-profileInfoHeightPx, 0f)
                    val consumedY = clamped - headerOffsetPx
                    headerOffsetPx = clamped
                    return Offset(0f, consumedY)
                }
                return Offset.Zero
            }
        }
    }

    // Diálogo confirmación eliminar guardado
    pinToRemove?.let { pinId ->
        AlertDialog(
            onDismissRequest = { pinToRemove = null },
            title = { Text("¿Quitar guardado?") },
            text  = { Text("Se quitará este pin de tus tableros guardados.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.removeSavedPin(pinId)
                    pinToRemove = null
                }) { Text("Quitar", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { pinToRemove = null }) { Text("Cancelar") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Mi Perfil",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, null)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val username = uiState.profile?.username ?: ""
                        val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(android.content.Intent.EXTRA_TEXT, "¡Mira mi perfil en StylePin! @$username")
                        }
                        context.startActivity(android.content.Intent.createChooser(intent, "Compartir perfil"))
                    }) { Icon(Icons.Outlined.Share, null) }
                    IconButton(onClick = onSettingsClick) { Icon(Icons.Outlined.MoreVert, null) }
                }
            )
        },
        // FAB solo visible en la pestaña Tableros
        floatingActionButton = {
            if (selectedTab == 1) {
                FloatingActionButton(
                    onClick = onNavigateToCreateBoard,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Nuevo tablero", tint = Color.White)
                }
            }
        }
    ) { padding ->

        // ── Loading inicial (sin datos aún) ──────────────────────────────
        if (uiState.isLoading && uiState.profile == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        // ── Error sin datos ──────────────────────────────────────────────
        if (uiState.error != null && uiState.profile == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(uiState.error ?: "Error al cargar el perfil", color = MaterialTheme.colorScheme.error)
                    Button(onClick = { viewModel.refresh() }) { Text("Reintentar") }
                }
            }
            return@Scaffold
        }

        val profile = uiState.profile ?: return@Scaffold

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .nestedScroll(nestedScrollConnection)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // ── Contenido de la pestaña ──────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .layout { measurable, constraints ->
                        val gridHeight = constraints.maxHeight - tabHeightPx.roundToInt()
                        val placeable = measurable.measure(
                            constraints.copy(minHeight = gridHeight, maxHeight = gridHeight)
                        )
                        layout(placeable.width, constraints.maxHeight) {
                            val y = (profileInfoHeightPx + headerOffsetPx + tabHeightPx).roundToInt()
                            placeable.placeRelative(0, y)
                        }
                    }
            ) {
                when (selectedTab) {
                    0 -> PinterestStylePinGrid(
                        pins        = uiState.userPins,
                        columns     = 3,
                        state       = pinsGridState,
                        emptyMessage = "Aún no has creado pins",
                        onPinClick  = onNavigateToPinDetail
                    )
                    1 -> if (uiState.isLoading) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else {
                        PinterestStyleBoardGrid(
                            boards       = uiState.userBoards,
                            state        = boardsGridState,
                            emptyMessage = "No tienes tableros",
                            onBoardClick = onNavigateToBoardDetail
                        )
                    }
                    2 -> if (uiState.isLoading) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else {
                        SavedPinsGrid(
                            pins       = uiState.savedPins,
                            state      = savedGridState,
                            onPinClick = onNavigateToPinDetail,
                            onRemovePin = { pinId -> pinToRemove = pinId }
                        )
                    }
                }
            }

            // ── Header + Tabs (sticky) ───────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset { IntOffset(0, headerOffsetPx.roundToInt()) }
                    .zIndex(1f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(profileInfoHeight)
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Avatar con cámara
                        Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier.size(100.dp)) {
                            val fallbackUrl = "https://ui-avatars.com/api/?name=${profile.fullName.replace(" ", "+")}&background=random&size=200"
                            AsyncImage(
                                model = profile.avatarUrl.takeIf { it.isNotBlank() } ?: fallbackUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(Color.LightGray)
                                    .clickable {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                contentScale = ContentScale.Crop
                            )
                            if (uiState.isUploadingAvatar) {
                                CircularProgressIndicator(
                                    modifier = Modifier.align(Alignment.Center).size(30.dp),
                                    color = Color.White
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.CameraAlt, null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }

                        Spacer(Modifier.height(12.dp))
                        Text(profile.fullName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("@${profile.username}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(12.dp))

                        Button(
                            onClick = onEditProfileClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor   = MaterialTheme.colorScheme.onSurface
                            )
                        ) { Text("Editar Perfil", fontWeight = FontWeight.SemiBold) }

                        Spacer(Modifier.height(16.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            StatItemProfile(formatNumber(profile.pinsCount), "Pins")
                            StatItemProfile(formatNumber(profile.followersCount), "Seguidores") { onCommunityClick(0) }
                            StatItemProfile(formatNumber(profile.followingCount), "Siguiendo") { onCommunityClick(1) }
                        }
                    }
                }

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor   = MaterialTheme.colorScheme.background,
                    modifier         = Modifier.height(tabHeight)
                ) {
                    listOf("Pins", "Tableros", "Guardados").forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick  = { selectedTab = index },
                            text     = {
                                Text(
                                    title,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

// ── Grid de Guardados con botón eliminar ──────────────────────────────────────

@Composable
fun SavedPinsGrid(
    pins: List<Pin>,
    state: LazyStaggeredGridState,
    onPinClick: (String) -> Unit,
    onRemovePin: (String) -> Unit
) {
    if (pins.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            Text("No has guardado pins", color = Color.Gray, modifier = Modifier.padding(top = 32.dp))
        }
    } else {
        LazyVerticalStaggeredGrid(
            columns      = StaggeredGridCells.Fixed(2),
            state        = state,
            modifier     = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp, start = 8.dp, end = 8.dp, top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalItemSpacing   = 12.dp
        ) {
            items(pins, key = { it.id }) { pin ->
                Box(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.fillMaxWidth().clickable { onPinClick(pin.id) }) {
                        AsyncImage(
                            model = pin.imageUrl,
                            contentDescription = pin.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 100.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.FillWidth
                        )
                        if (pin.title.isNotBlank()) {
                            Text(
                                text  = pin.title,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(top = 6.dp, start = 4.dp, end = 4.dp),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    // Botón eliminar sobre la imagen
                    IconButton(
                        onClick  = { onRemovePin(pin.id) },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .size(32.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Quitar guardado",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

// ── Grids reutilizables ───────────────────────────────────────────────────────

@Composable
fun PinterestStylePinGrid(
    pins: List<Pin>,
    columns: Int,
    state: LazyStaggeredGridState,
    emptyMessage: String,
    onPinClick: (String) -> Unit
) {
    if (pins.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            Text(emptyMessage, color = Color.Gray, modifier = Modifier.padding(top = 32.dp))
        }
    } else {
        LazyVerticalStaggeredGrid(
            columns      = StaggeredGridCells.Fixed(columns),
            state        = state,
            modifier     = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp, start = 8.dp, end = 8.dp, top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalItemSpacing   = 12.dp
        ) {
            items(pins, key = { it.id }) { pin ->
                Column(modifier = Modifier.fillMaxWidth().clickable { onPinClick(pin.id) }) {
                    AsyncImage(
                        model = pin.imageUrl,
                        contentDescription = pin.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 100.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.FillWidth
                    )
                    if (pin.title.isNotBlank()) {
                        Text(
                            text  = pin.title,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(top = 6.dp, start = 4.dp, end = 4.dp),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PinterestStyleBoardGrid(
    boards: List<Board>,
    state: LazyGridState,
    emptyMessage: String,
    onBoardClick: (String) -> Unit
) {
    if (boards.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            Text(emptyMessage, color = Color.Gray, modifier = Modifier.padding(top = 32.dp))
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            state   = state,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp, start = 12.dp, end = 12.dp, top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement   = Arrangement.spacedBy(16.dp)
        ) {
            items(boards, key = { it.id }) { board ->
                Column(modifier = Modifier.fillMaxWidth().clickable { onBoardClick(board.id) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        if (!board.coverImageUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = board.coverImageUrl,
                                contentDescription = board.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    text  = board.name.take(1).uppercase(),
                                    style = MaterialTheme.typography.displayMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    Text(
                        text  = board.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(top = 8.dp, start = 4.dp, end = 4.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text  = "${board.pinsCount} pins",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
    }
}