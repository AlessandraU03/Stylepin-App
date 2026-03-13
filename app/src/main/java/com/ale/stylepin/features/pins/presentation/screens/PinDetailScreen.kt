package com.ale.stylepin.features.pins.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
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
import com.ale.stylepin.features.pins.domain.entities.Pin
import com.ale.stylepin.features.pins.presentation.viewmodels.PinsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PinDetailScreen(
    pinId: String,
    viewModel: PinsViewModel,
    onBack: () -> Unit,
    onNavigateToEditPin: (Pin) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(pinId) {
        viewModel.loadPinById(pinId)
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("¿Eliminar Pin?") },
            text = { Text("Esta acción eliminará el outfit permanentemente de tu cuenta.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deletePin(pinId)
                    showDeleteDialog = false
                    onBack()
                }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.pinDetail?.title ?: "Detalle del Pin") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    val isOwner = uiState.currentUserId != null && uiState.pinDetail?.userId == uiState.currentUserId
                    if (isOwner) {
                        IconButton(onClick = { uiState.pinDetail?.let(onNavigateToEditPin) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when {
                uiState.isLoadingDetail -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.error != null && uiState.pinDetail == null -> {
                    Text(
                        text = uiState.error ?: "",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp)
                    )
                }
                uiState.pinDetail != null -> {
                    PinDetailContent(
                        pin = uiState.pinDetail!!,
                        onLikeClick = { viewModel.toggleLike(pinId) },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
private fun PinDetailContent(
    pin: Pin,
    onLikeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Box {
            AsyncImage(
                model = pin.imageUrl,
                contentDescription = pin.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
            
            // Botón de Like flotante en el detalle
            FilledTonalIconButton(
                onClick = onLikeClick,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                )
            ) {
                Icon(
                    imageVector = if (pin.isLikedByMe) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Like",
                    tint = if (pin.isLikedByMe) Color.Red else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = pin.userAvatarUrl,
                contentDescription = "Avatar de ${pin.userFullName}",
                modifier = Modifier.size(40.dp).clip(CircleShape)
            )
            Spacer(Modifier.width(8.dp))
            Column {
                Text(pin.userFullName, fontWeight = FontWeight.SemiBold)
                Text("@${pin.username}", style = MaterialTheme.typography.bodySmall)
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(pin.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        if (!pin.description.isNullOrBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(pin.description, style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(Modifier.height(16.dp))

        DetailRow("Categoría", pin.category)
        DetailRow("Temporada", pin.season)
        DetailRow("Rango de precio", pin.priceRange)
        if (!pin.whereToBuy.isNullOrBlank()) DetailRow("Dónde comprar", pin.whereToBuy)
        if (!pin.purchaseLink.isNullOrBlank()) DetailRow("Link de compra", pin.purchaseLink)

        if (pin.styles.isNotEmpty()) TagSection("Estilos", pin.styles)
        if (pin.occasions.isNotEmpty()) TagSection("Ocasiones", pin.occasions)
        if (pin.brands.isNotEmpty()) TagSection("Marcas", pin.brands)
        if (pin.colors.isNotEmpty()) TagSection("Colores", pin.colors)
        if (pin.tags.isNotEmpty()) TagSection("Tags", pin.tags)

        Spacer(Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem("Likes", pin.likesCount, pin.isLikedByMe)
            StatItem("Guardados", pin.savesCount, pin.isSavedByMe)
            StatItem("Comentarios", pin.commentsCount, false)
            StatItem("Vistas", pin.viewsCount, false)
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value)
    }
}

@Composable
private fun TagSection(title: String, tags: List<String>) {
    Spacer(Modifier.height(12.dp))
    Text(title, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    Spacer(Modifier.height(4.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        tags.forEach { tag ->
            SuggestionChip(
                onClick = {},
                label = { Text(tag, style = MaterialTheme.typography.labelSmall) }
            )
        }
    }
}

@Composable
private fun StatItem(label: String, count: Int, isActive: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            count.toString(), 
            fontWeight = FontWeight.Bold, 
            style = MaterialTheme.typography.titleMedium,
            color = if (isActive && label == "Likes") Color.Red else MaterialTheme.colorScheme.onSurface
        )
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
