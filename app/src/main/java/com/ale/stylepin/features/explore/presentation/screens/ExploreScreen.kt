package com.ale.stylepin.features.explore.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.ale.stylepin.features.boards.domain.entities.Board
import com.ale.stylepin.features.explore.presentation.viewmodels.ExploreViewModel
import com.ale.stylepin.features.explore.presentation.viewmodels.UserBoardsGroup
import com.ale.stylepin.features.pins.domain.entities.Pin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    viewModel: ExploreViewModel,
    onNavigateToBoardDetail: (String) -> Unit,
    onNavigateToPinDetail: (String) -> Unit,
    onNavigateToUserProfile: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Explorar", fontWeight = FontWeight.Bold) })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Barra de búsqueda
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Buscar pines y usuarios...") },
                leadingIcon = { Icon(Icons.Outlined.Search, null) },
                shape = CircleShape,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent
                )
            )

            if (uiState.searchQuery.isBlank()) {
                // ── MODO EXPLORAR: tableros agrupados por usuario ──
                when {
                    uiState.isLoadingBoards -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }

                    uiState.userBoardGroups.isEmpty() -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No hay tableros públicos todavía", color = Color.Gray)
                        }
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 24.dp)
                        ) {
                            items(uiState.userBoardGroups, key = { it.userId }) { group ->
                                UserBoardsRow(
                                    group = group,
                                    onBoardClick = onNavigateToBoardDetail,
                                    onUserClick = onNavigateToUserProfile
                                )
                            }
                        }
                    }
                }
            } else {
                // ── MODO BÚSQUEDA ──
                TabRow(
                    selectedTabIndex = uiState.searchSelectedTab,
                    containerColor = Color.Transparent
                ) {
                    Tab(
                        selected = uiState.searchSelectedTab == 0,
                        onClick = { viewModel.setSearchTab(0) },
                        text = { Text("Pines", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = uiState.searchSelectedTab == 1,
                        onClick = { viewModel.setSearchTab(1) },
                        text = { Text("Usuarios", fontWeight = FontWeight.Bold) }
                    )
                }

                if (uiState.isSearching) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                        CircularProgressIndicator(modifier = Modifier.padding(32.dp))
                    }
                } else {
                    when (uiState.searchSelectedTab) {
                        0 -> {
                            if (uiState.searchPinResults.isEmpty()) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("No se encontraron pines.", color = Color.Gray)
                                }
                            } else {
                                ExplorePinGrid(
                                    pins = uiState.searchPinResults,
                                    onPinClick = onNavigateToPinDetail
                                )
                            }
                        }

                        1 -> {
                            if (uiState.searchUserResults.isEmpty()) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("No se encontraron usuarios.", color = Color.Gray)
                                }
                            } else {
                                LazyColumn(modifier = Modifier.fillMaxSize()) {
                                    items(uiState.searchUserResults, key = { it.id }) { user ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { onNavigateToUserProfile(user.id) }
                                                .padding(horizontal = 16.dp, vertical = 12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            val fallbackUrl =
                                                "https://ui-avatars.com/api/?name=${user.fullName.replace(" ", "+")}"
                                            AsyncImage(
                                                model = user.avatarUrl.takeIf { it.isNotBlank() } ?: fallbackUrl,
                                                contentDescription = null,
                                                modifier = Modifier.size(48.dp).clip(CircleShape),
                                                contentScale = ContentScale.Crop
                                            )
                                            Spacer(Modifier.width(12.dp))
                                            Column {
                                                Text(
                                                    user.fullName,
                                                    fontWeight = FontWeight.Bold,
                                                    style = MaterialTheme.typography.bodyLarge
                                                )
                                                Text(
                                                    "@${user.username}",
                                                    color = Color.Gray,
                                                    style = MaterialTheme.typography.bodyMedium
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
    }
}

/**
 * Fila de tableros de un usuario con scroll horizontal.
 * Estructura: avatar + nombre de usuario → lista de tableros deslizable a los lados.
 */
@Composable
private fun UserBoardsRow(
    group: UserBoardsGroup,
    onBoardClick: (String) -> Unit,
    onUserClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        // Cabecera de usuario
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onUserClick(group.userId) }
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val fallbackUrl =
                "https://ui-avatars.com/api/?name=${group.userFullName.replace(" ", "+")}&background=random"
            AsyncImage(
                model = group.userAvatarUrl?.takeIf { it.isNotBlank() } ?: fallbackUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(10.dp))
            Column {
                val displayName = group.userFullName.ifBlank { group.username }
                Text(
                    text = displayName,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "@${group.username}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
            Spacer(Modifier.weight(1f))
            Text(
                text = "${group.boards.size} tableros",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 11.sp
            )
        }

        // Fila horizontal de tableros
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(group.boards, key = { it.id }) { board ->
                BoardHorizontalCard(board = board, onClick = { onBoardClick(board.id) })
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
    }
}

/**
 * Tarjeta de tablero para el scroll horizontal: imagen cuadrada + nombre.
 */
@Composable
private fun BoardHorizontalCard(board: Board, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(130.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(130.dp)
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
                // Inicial del nombre del tablero como placeholder
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = board.name.take(1).uppercase(),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            // Badge de cantidad de pins
            if (board.pinsCount > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(6.dp)
                        .background(Color.Black.copy(alpha = 0.55f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${board.pinsCount} pins",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontSize = 10.sp
                    )
                }
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = board.name,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun ExplorePinGrid(pins: List<Pin>, onPinClick: (String) -> Unit) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalItemSpacing = 12.dp
    ) {
        items(pins) { pin ->
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
                        text = pin.title,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}