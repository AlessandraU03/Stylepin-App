// com/ale/stylepin/features/pins/presentation/screens/PinDetailScreen.kt
package com.ale.stylepin.features.pins.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PinDetailScreen(
    pinId: String,
    viewModel: PinsViewModel,
    onBack: () -> Unit,
    onEditClick: (String) -> Unit // <--- NUEVA ACCIÓN PARA EDITAR
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(pinId) {
        viewModel.loadPinById(pinId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.pinDetail?.title ?: "") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") }
                },
                actions = {
                    // BOTÓN DE EDITAR EN LA BARRA SUPERIOR
                    uiState.pinDetail?.let { pin ->
                        // Idealmente aquí checas si pin.userId == tuUsuarioId para mostrar el botón
                        IconButton(onClick = { onEditClick(pin.id) }) {
                            Icon(Icons.Default.Edit, "Editar Pin")
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
                    Text(uiState.error ?: "", color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
                }
                uiState.pinDetail != null -> {
                    PinDetailContent(pin = uiState.pinDetail!!)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PinDetailContent(pin: Pin) {
    // Estado local para simular el Like por ahora (hasta conectar el ViewModel de Likes)
    var isLiked by remember { mutableStateOf(pin.isLikedByMe) }
    var likesCount by remember { mutableIntStateOf(pin.likesCount) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Box {
            AsyncImage(
                model = pin.imageUrl,
                contentDescription = pin.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
            // BOTÓN FLOTANTE DE LIKE SOBRE LA IMAGEN
            FloatingActionButton(
                onClick = {
                    isLiked = !isLiked
                    likesCount += if(isLiked) 1 else -1
                    // TODO: Llamar a viewModel.toggleLike(pin.id)
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Icon(
                    imageVector = if (isLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Like",
                    tint = if (isLiked) Color.Red else MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // SECCIÓN DEL AUTOR CORREGIDA
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                // Si no tiene avatar, usamos ui-avatars para que no quede feo
                model = pin.userAvatarUrl ?: "https://ui-avatars.com/api/?name=${pin.userFullName.replace(" ", "+")}",
                contentDescription = "Avatar de ${pin.userFullName}",
                modifier = Modifier.size(48.dp).clip(CircleShape).background(Color.LightGray)
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(pin.userFullName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text("@${pin.username}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.weight(1f))
            Button(onClick = { /* TODO: Seguir usuario */ }) {
                Text("Seguir")
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(pin.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

        if (!pin.description.isNullOrBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(pin.description, style = MaterialTheme.typography.bodyLarge)
        }

        Spacer(Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(Modifier.height(16.dp))

        DetailRow("Categoría", pin.category.replace("_", " ").replaceFirstChar { it.uppercase() })
        DetailRow("Temporada", pin.season.replace("_", " ").replaceFirstChar { it.uppercase() })
        DetailRow("Precio", pin.priceRange.replace("_", " ").replaceFirstChar { it.uppercase() })
        if (!pin.whereToBuy.isNullOrBlank()) DetailRow("Dónde comprar", pin.whereToBuy)
        if (!pin.purchaseLink.isNullOrBlank()) {
            Text("Link de compra:", fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(pin.purchaseLink, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(vertical = 4.dp))
        }

        if (pin.tags.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            Text("Etiquetas", fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                pin.tags.forEach { tag ->
                    SuggestionChip(onClick = {}, label = { Text("#$tag") })
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp)).padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem("Likes", likesCount) // Usa el local simulado
            StatItem("Guardados", pin.savesCount)
            StatItem("Vistas", pin.viewsCount)
        }
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun StatItem(label: String, count: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(count.toString(), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}