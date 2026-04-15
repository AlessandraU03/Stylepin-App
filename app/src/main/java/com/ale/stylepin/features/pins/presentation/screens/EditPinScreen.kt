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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.ale.stylepin.features.pins.presentation.components.ChipSelectionGroup
import com.ale.stylepin.features.pins.presentation.viewmodels.PinFormEvent
import com.ale.stylepin.features.pins.presentation.viewmodels.PinsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPinScreen(pinId: String, viewModel: PinsViewModel, onBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(pinId) { viewModel.loadPinById(pinId) }

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
            uiState.isLoadingDetail -> Box(modifier = Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            uiState.error != null && uiState.pinDetail == null -> Box(modifier = Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) { Text(text = uiState.error ?: "", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp)) }
            else -> {
                Column(modifier = Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState())) {
                    if (uiState.imageUrl.isNotEmpty()) {
                        AsyncImage(model = uiState.imageUrl, contentDescription = "Imagen del pin", modifier = Modifier.fillMaxWidth().height(200.dp).border(1.dp, Color.Gray, RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop)
                        Spacer(Modifier.height(16.dp))
                    }

                    OutlinedTextField(value = uiState.title, onValueChange = { viewModel.onFormEvent(PinFormEvent.TitleChanged(it)) }, label = { Text("Título *") }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = uiState.description, onValueChange = { viewModel.onFormEvent(PinFormEvent.DescriptionChanged(it)) }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                    Spacer(Modifier.height(16.dp))

                    ChipSelectionGroup(title = "Categoría *", options = mapOf("outfit_completo" to "Outfit Completo", "prenda_individual" to "Prenda Individual", "accesorio" to "Accesorio", "calzado" to "Calzado"), selectedOption = uiState.selectedCategory, onOptionSelected = { viewModel.onFormEvent(PinFormEvent.CategoryChanged(it)) })
                    ChipSelectionGroup(title = "Temporada", options = mapOf("todo_el_ano" to "Todo el año", "primavera" to "Primavera", "verano" to "Verano", "otono" to "Otoño", "invierno" to "Invierno"), selectedOption = uiState.selectedSeason, onOptionSelected = { viewModel.onFormEvent(PinFormEvent.SeasonChanged(it)) })
                    ChipSelectionGroup(title = "Rango de precio", options = mapOf("bajo_500" to "Bajo $500", "500_1000" to "$500 - $1000", "1000_2000" to "$1000 - $2000", "mas_2000" to "Más de $2000"), selectedOption = uiState.priceRange, onOptionSelected = { viewModel.onFormEvent(PinFormEvent.PriceRangeChanged(it)) })

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

                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = uiState.isPrivate, onCheckedChange = { viewModel.onFormEvent(PinFormEvent.IsPrivateChanged(it)) })
                        Text("¿Hacer este pin privado?")
                    }

                    Spacer(Modifier.height(24.dp))

                    Button(onClick = { viewModel.savePin(pinId = pinId) { onBack() } }, modifier = Modifier.fillMaxWidth(), enabled = !uiState.isLoading && uiState.title.isNotBlank()) {
                        if (uiState.isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                        else Text("Actualizar Pin")
                    }

                    uiState.error?.let { error -> Text(text = error, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp)) }
                }
            }
        }
    }
}