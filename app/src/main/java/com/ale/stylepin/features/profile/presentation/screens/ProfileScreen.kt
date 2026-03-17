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
import androidx.compose.material.icons.filled.CameraAlt
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.ale.stylepin.features.boards.domain.entities.Board
import com.ale.stylepin.features.pins.domain.entities.Pin
import com.ale.stylepin.features.profile.presentation.viewmodels.ProfileViewModel
import kotlin.math.roundToInt

// --- COMPONENTES LOCALES PARA ASEGURAR QUE SE DIBUJEN BIEN ---

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

fun formatNumber(number: Int): String {
    return when {
        number >= 1_000_000 -> String.format("%.1fm", number / 1_000_000.0)
        number >= 1_000 -> String.format("%.1fk", number / 1_000.0)
        else -> number.toString()
    }
}

// --- PANTALLA PRINCIPAL ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onBack: () -> Unit,
    onEditProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onCommunityClick: (Int) -> Unit,
    onNavigateToPinDetail: (String) -> Unit,
    onNavigateToBoardDetail: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.refresh()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) viewModel.updateAvatar(uri)
    }

    var selectedTab by remember { mutableIntStateOf(0) }

    val pinsGridState = rememberLazyStaggeredGridState()
    val boardsGridState = rememberLazyGridState()
    val savedGridState = rememberLazyStaggeredGridState()

    // 👇 AUMENTAMOS LA ALTURA PARA QUE TODO QUEPA (Antes 340, ahora 380)
    val profileInfoHeight = 380.dp
    val tabHeight = 48.dp

    val density = LocalDensity.current
    val profileInfoHeightPx = with(density) { profileInfoHeight.toPx() }
    val tabHeightPx = with(density) { tabHeight.toPx() }

    var headerOffsetPx by remember { mutableFloatStateOf(0f) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                if (delta < 0) { // 👇 QUITAMOS LA RESTRICCIÓN DE SCROLL, AHORA SIEMPRE DESLIZA
                    val newOffset = headerOffsetPx + delta
                    val clamped = newOffset.coerceIn(-profileInfoHeightPx, 0f)
                    val consumed = clamped - headerOffsetPx
                    headerOffsetPx = clamped
                    return Offset(0f, consumed)
                }
                return Offset.Zero
            }

            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                if (available.y > 0) { // Al deslizar hacia abajo
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
                title = { Text("Mi Perfil", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, null) } },
                actions = {
                    IconButton(onClick = {}) { Icon(Icons.Outlined.Share, null) }
                    IconButton(onClick = onSettingsClick) { Icon(Icons.Outlined.MoreVert, null) }
                }
            )
        }
    ) { padding ->
        uiState.profile?.let { profile ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .nestedScroll(nestedScrollConnection)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // --- GRID DINÁMICO (Layout optimizado para no cortar abajo) ---
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .layout { measurable, constraints ->
                            // La altura exacta de la cuadrícula es la pantalla menos las pestañas
                            val gridHeight = constraints.maxHeight - tabHeightPx.roundToInt()
                            val placeable = measurable.measure(constraints.copy(minHeight = gridHeight, maxHeight = gridHeight))

                            layout(placeable.width, constraints.maxHeight) {
                                // Se desplaza hacia arriba y hacia abajo siguiendo al header
                                val yPosition = (profileInfoHeightPx + headerOffsetPx + tabHeightPx).roundToInt()
                                placeable.placeRelative(0, yPosition)
                            }
                        }
                ) {
                    when (selectedTab) {
                        0 -> PinterestStylePinGrid(pins = uiState.userPins, columns = 3, state = pinsGridState, emptyMessage = "Aún no has creado pins", onPinClick = onNavigateToPinDetail)
                        1 -> PinterestStyleBoardGrid(boards = uiState.userBoards, state = boardsGridState, emptyMessage = "No tienes tableros", onBoardClick = onNavigateToBoardDetail)
                        2 -> PinterestStylePinGrid(pins = uiState.savedPins, columns = 2, state = savedGridState, emptyMessage = "No has guardado pins", onPinClick = onNavigateToPinDetail)
                    }
                }

                // --- HEADER Y TABS (El Header se oculta, los Tabs se pegan) ---
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
                            // Avatar con Botón de Cámara
                            Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier.size(100.dp)) {
                                val fallbackUrl = "https://ui-avatars.com/api/?name=${profile.fullName.replace(" ", "+")}&background=random&size=200"
                                AsyncImage(
                                    model = profile.avatarUrl.takeIf { it.isNotBlank() } ?: fallbackUrl,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize().clip(CircleShape).background(Color.LightGray).clickable {
                                        photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                    },
                                    contentScale = ContentScale.Crop
                                )
                                if (uiState.isUploadingAvatar) {
                                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center).size(30.dp), color = Color.White)
                                }
                                Box(
                                    modifier = Modifier.size(32.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary),
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
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurface)
                            ) {
                                Text("Editar Perfil", fontWeight = FontWeight.SemiBold)
                            }

                            Spacer(Modifier.height(16.dp))

                            // FILA DE ESTADÍSTICAS
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                StatItemProfile(value = formatNumber(profile.pinsCount), label = "Pins", onClick = null)
                                StatItemProfile(value = formatNumber(profile.followersCount), label = "Seguidores", onClick = { onCommunityClick(0) })
                                StatItemProfile(value = formatNumber(profile.followingCount), label = "Siguiendo", onClick = { onCommunityClick(1) })
                            }
                        }
                    }

                    TabRow(selectedTabIndex = selectedTab, containerColor = MaterialTheme.colorScheme.background, modifier = Modifier.height(tabHeight)) {
                        listOf("Pins", "Tableros", "Guardados").forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = { Text(title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// COMPONENTES MODULARES
// -------------------------------------------------------------------------

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
            columns = StaggeredGridCells.Fixed(columns),
            state = state,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp, start = 8.dp, end = 8.dp, top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalItemSpacing = 12.dp
        ) {
            items(pins, key = { it.id }) { pin ->
                Column(
                    modifier = Modifier.fillMaxWidth().clickable { onPinClick(pin.id) }
                ) {
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
                            text = pin.title,
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
            state = state,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp, start = 12.dp, end = 12.dp, top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(boards, key = { it.id }) { board ->
                Column(
                    modifier = Modifier.fillMaxWidth().clickable { onBoardClick(board.id) }
                ) {
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
                                    text = board.name.take(1).uppercase(),
                                    style = MaterialTheme.typography.displayMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    Text(
                        text = board.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(top = 8.dp, start = 4.dp, end = 4.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}