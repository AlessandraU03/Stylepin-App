package com.ale.stylepin.features.pins.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(pinId) {
        viewModel.loadPinById(pinId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.pinDetail?.title ?: "Detalle del Pin") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
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
                    PinDetailContent(pin = uiState.pinDetail!!, modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}

@Composable
private fun PinDetailContent(pin: Pin, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        AsyncImage(
            model = pin.imageUrl,
            contentDescription = pin.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )

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
            StatItem("Likes", pin.likesCount)
            StatItem("Guardados", pin.savesCount)
            StatItem("Comentarios", pin.commentsCount)
            StatItem("Vistas", pin.viewsCount)
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
private fun StatItem(label: String, count: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(count.toString(), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}