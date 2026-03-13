// com/ale/stylepin/features/pins/presentation/screens/EditPinScreen.kt
package com.ale.stylepin.features.pins.presentation.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditPinScreen(
    pinId: String,
    viewModel: PinsViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Carga el pin completo desde la API al entrar a la pantalla
    LaunchedEffect(pinId) {
        viewModel.loadPinById(pinId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Pin") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        when {
            uiState.isLoadingDetail -> {
                Box(modifier = Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null && uiState.pinDetail == null -> {
                Box(modifier = Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = uiState.error ?: "", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp) // Espaciado automático entre elementos
                ) {
                    Spacer(Modifier.height(8.dp))

                    if (uiState.imageUrl.isNotEmpty()) {
                        AsyncImage(
                            model = uiState.imageUrl,
                            contentDescription = "Imagen del pin",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    OutlinedTextField(
                        value = uiState.title,
                        onValueChange = { viewModel.onFormEvent(PinFormEvent.TitleChanged(it)) },
                        label = { Text("Título *") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = uiState.description,
                        onValueChange = { viewModel.onFormEvent(PinFormEvent.DescriptionChanged(it)) },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )

                    // --- CHIPS DE CATEGORÍA ---
                    Text("Categoría *", style = MaterialTheme.typography.labelLarge)
                    val categories = mapOf(
                        "outfit_completo" to "Outfit Completo",
                        "prenda_individual" to "Prenda",
                        "accesorio" to "Accesorio",
                        "calzado" to "Calzado"
                    )
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        categories.forEach { (key, label) ->
                            FilterChip(
                                selected = uiState.selectedCategory == key,
                                onClick = { viewModel.onFormEvent(PinFormEvent.CategoryChanged(key)) },
                                label = { Text(label) }
                            )
                        }
                    }

                    // --- CHIPS DE TEMPORADA ---
                    Text("Temporada", style = MaterialTheme.typography.labelLarge)
                    val seasons = mapOf(
                        "todo_el_ano" to "Todo el año",
                        "primavera" to "Primavera",
                        "verano" to "Verano",
                        "otono" to "Otoño",
                        "invierno" to "Invierno"
                    )
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        seasons.forEach { (key, label) ->
                            FilterChip(
                                selected = uiState.selectedSeason == key,
                                onClick = { viewModel.onFormEvent(PinFormEvent.SeasonChanged(key)) },
                                label = { Text(label) }
                            )
                        }
                    }

                    // --- CHIPS DE PRECIO ---
                    Text("Rango de Precio", style = MaterialTheme.typography.labelLarge)
                    val prices = mapOf(
                        "bajo_500" to "Menos de $500",
                        "500_1000" to "$500 - $1000",
                        "1000_2000" to "$1000 - $2000",
                        "mas_2000" to "Más de $2000"
                    )
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        prices.forEach { (key, label) ->
                            FilterChip(
                                selected = uiState.priceRange == key,
                                onClick = { viewModel.onFormEvent(PinFormEvent.PriceRangeChanged(key)) },
                                label = { Text(label) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = uiState.whereToBuy,
                        onValueChange = { viewModel.onFormEvent(PinFormEvent.WhereToBuyChanged(it)) },
                        label = { Text("Dónde comprar (ej. Zara, H&M)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = uiState.purchaseLink,
                        onValueChange = { viewModel.onFormEvent(PinFormEvent.PurchaseLinkChanged(it)) },
                        label = { Text("Link de compra") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = uiState.isPrivate,
                            onCheckedChange = { viewModel.onFormEvent(PinFormEvent.IsPrivateChanged(it)) }
                        )
                        Text("Hacer este pin privado")
                    }

                    Button(
                        onClick = { viewModel.savePin(pinId = pinId) { onBack() } },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        enabled = !uiState.isLoading && uiState.title.isNotBlank() && uiState.selectedCategory.isNotBlank()
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                        } else {
                            Text("Actualizar Pin")
                        }
                    }

                    uiState.error?.let { error ->
                        Text(text = error, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 16.dp))
                    }

                    Spacer(Modifier.height(32.dp))
                }
            }
        }
    }
}