package com.ale.stylepin.features.pins.presentation.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.ale.stylepin.features.pins.presentation.viewmodels.PinFormEvent
import com.ale.stylepin.features.pins.presentation.viewmodels.PinsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPinScreen(viewModel: PinsViewModel, onBack: () -> Unit) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.onFormEvent(PinFormEvent.ImageUrlChanged(it.toString())) }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Nuevo Pin") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Selector de imagen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                    .clickable { galleryLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (uiState.imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = uiState.imageUrl,
                        contentDescription = "Imagen seleccionada",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp)
                        )
                        Text("Toca para seleccionar imagen")
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.title,
                onValueChange = { viewModel.onFormEvent(PinFormEvent.TitleChanged(it)) },
                label = { Text("Título *") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.description,
                onValueChange = { viewModel.onFormEvent(PinFormEvent.DescriptionChanged(it)) },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.selectedCategory,
                onValueChange = { viewModel.onFormEvent(PinFormEvent.CategoryChanged(it)) },
                label = { Text("Categoría (outfit_completo, calzado, etc.) *") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.selectedSeason,
                onValueChange = { viewModel.onFormEvent(PinFormEvent.SeasonChanged(it)) },
                label = { Text("Temporada (todo_el_ano, verano, etc.)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.priceRange,
                onValueChange = { viewModel.onFormEvent(PinFormEvent.PriceRangeChanged(it)) },
                label = { Text("Rango de precio (bajo_500, etc.)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.whereToBuy,
                onValueChange = { viewModel.onFormEvent(PinFormEvent.WhereToBuyChanged(it)) },
                label = { Text("Dónde comprar") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.purchaseLink,
                onValueChange = { viewModel.onFormEvent(PinFormEvent.PurchaseLinkChanged(it)) },
                label = { Text("Link de compra") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = uiState.isPrivate,
                    onCheckedChange = { viewModel.onFormEvent(PinFormEvent.IsPrivateChanged(it)) }
                )
                Text("¿Hacer este pin privado?")
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { viewModel.savePin { onBack() } },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading && uiState.title.isNotBlank() && uiState.imageUrl.isNotEmpty()
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("Publicar Outfit")
                }
            }

            uiState.error?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}