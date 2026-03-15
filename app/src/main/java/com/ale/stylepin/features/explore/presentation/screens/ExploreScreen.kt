package com.ale.stylepin.features.explore.presentation.screens

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.ale.stylepin.features.explore.presentation.components.TrendingBoardCard
import com.ale.stylepin.features.explore.presentation.viewmodels.ExploreViewModel
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
        topBar = { TopAppBar(title = { Text("Explorar", fontWeight = FontWeight.Bold) }) }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).background(MaterialTheme.colorScheme.background)
        ) {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
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
                if (uiState.isLoadingTrending) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                } else if (uiState.trendingBoards.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No hay tendencias.", color = Color.Gray) }
                } else {
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalItemSpacing = 16.dp
                    ) {
                        items(uiState.trendingBoards, key = { it.board.id }) { boardItem ->
                            TrendingBoardCard(item = boardItem, onClick = { onNavigateToBoardDetail(boardItem.board.id) })
                        }
                    }
                }
            } else {
                TabRow(selectedTabIndex = uiState.searchSelectedTab, containerColor = Color.Transparent) {
                    Tab(selected = uiState.searchSelectedTab == 0, onClick = { viewModel.setSearchTab(0) }, text = { Text("Pines", fontWeight = FontWeight.Bold) })
                    Tab(selected = uiState.searchSelectedTab == 1, onClick = { viewModel.setSearchTab(1) }, text = { Text("Usuarios", fontWeight = FontWeight.Bold) })
                }

                if (uiState.isSearching) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) { CircularProgressIndicator(modifier = Modifier.padding(32.dp)) }
                } else {
                    when (uiState.searchSelectedTab) {
                        0 -> {
                            if (uiState.searchPinResults.isEmpty()) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No se encontraron pines.", color = Color.Gray) }
                            } else {
                                ExplorePinGrid(pins = uiState.searchPinResults, onPinClick = onNavigateToPinDetail)
                            }
                        }
                        1 -> {
                            if (uiState.searchUserResults.isEmpty()) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No se encontraron usuarios.", color = Color.Gray) }
                            } else {
                                LazyColumn(modifier = Modifier.fillMaxSize()) {
                                    items(uiState.searchUserResults, key = { it.id }) { user ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth().clickable { onNavigateToUserProfile(user.id) }.padding(horizontal = 16.dp, vertical = 12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            val fallbackUrl = "https://ui-avatars.com/api/?name=${user.fullName.replace(" ", "+")}"
                                            AsyncImage(
                                                model = user.avatarUrl.takeIf { it.isNotBlank() } ?: fallbackUrl,
                                                contentDescription = null,
                                                modifier = Modifier.size(48.dp).clip(CircleShape),
                                                contentScale = ContentScale.Crop
                                            )
                                            Spacer(Modifier.width(12.dp))
                                            Column {
                                                Text(user.fullName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                                                Text("@${user.username}", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
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

@Composable
fun ExplorePinGrid(pins: List<Pin>, onPinClick: (String) -> Unit) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalItemSpacing = 12.dp
    ) {
        items(pins, key = { it.id }) { pin ->
            Column(modifier = Modifier.fillMaxWidth().clickable { onPinClick(pin.id) }) {
                AsyncImage(
                    model = pin.imageUrl,
                    contentDescription = pin.title,
                    modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 100.dp).clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.FillWidth
                )
                if (pin.title.isNotBlank()) {
                    Text(
                        text = pin.title, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp), maxLines = 2, overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}