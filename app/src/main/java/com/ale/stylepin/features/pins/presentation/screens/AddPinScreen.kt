package com.ale.stylepin.features.pins.presentation.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.ale.stylepin.features.pins.presentation.components.ChipSelectionGroup
import com.ale.stylepin.features.pins.presentation.viewmodels.PinFormEvent
import com.ale.stylepin.features.pins.presentation.viewmodels.PinsViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPinScreen(viewModel: PinsViewModel, onBack: () -> Unit) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }
    var showImageSourceDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.webSocketManager.notifications.collect { notification ->
            Toast.makeText(context, "Notificación: ${notification.message}", Toast.LENGTH_LONG).show()
        }
    }

    fun createImageUri(): Uri {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { viewModel.onFormEvent(PinFormEvent.ImageUrlChanged(it.toString())) }
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) tempImageUri?.let { viewModel.onFormEvent(PinFormEvent.ImageUrlChanged(it.toString())) }
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            val uri = createImageUri()
            tempImageUri = uri
            cameraLauncher.launch(uri)
        }
    }

    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Seleccionar imagen") },
            text = { Text("¿Desde dónde quieres añadir la foto?") },
            confirmButton = {
                TextButton(onClick = {
                    val permissionCheckResult = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                    if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                        val uri = createImageUri()
                        tempImageUri = uri
                        cameraLauncher.launch(uri)
                    } else {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                    showImageSourceDialog = false
                }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Cámara")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    galleryLauncher.launch("image/*")
                    showImageSourceDialog = false
                }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Galería")
                    }
                }
            }
        )
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Nuevo Pin") }) }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(12.dp)).border(1.dp, Color.Gray, RoundedCornerShape(12.dp)).clickable { showImageSourceDialog = true },
                contentAlignment = Alignment.Center
            ) {
                if (uiState.imageUrl.isNotEmpty()) {
                    AsyncImage(model = uiState.imageUrl, contentDescription = "Imagen seleccionada", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.Image, contentDescription = null, modifier = Modifier.size(48.dp))
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

            Spacer(Modifier.height(16.dp))

            ChipSelectionGroup(
                title = "Categoría *",
                options = mapOf("outfit_completo" to "Outfit Completo", "prenda_individual" to "Prenda Individual", "accesorio" to "Accesorio", "calzado" to "Calzado"),
                selectedOption = uiState.selectedCategory,
                onOptionSelected = { viewModel.onFormEvent(PinFormEvent.CategoryChanged(it)) }
            )

            ChipSelectionGroup(
                title = "Temporada",
                options = mapOf("todo_el_ano" to "Todo el año", "primavera" to "Primavera", "verano" to "Verano", "otono" to "Otoño", "invierno" to "Invierno"),
                selectedOption = uiState.selectedSeason,
                onOptionSelected = { viewModel.onFormEvent(PinFormEvent.SeasonChanged(it)) }
            )

            ChipSelectionGroup(
                title = "Rango de precio",
                options = mapOf("bajo_500" to "Bajo $500", "500_1000" to "$500 - $1000", "1000_2000" to "$1000 - $2000", "mas_2000" to "Más de $2000"),
                selectedOption = uiState.priceRange,
                onOptionSelected = { viewModel.onFormEvent(PinFormEvent.PriceRangeChanged(it)) }
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

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = uiState.isPrivate, onCheckedChange = { viewModel.onFormEvent(PinFormEvent.IsPrivateChanged(it)) })
                Text("¿Hacer este pin privado?")
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { viewModel.savePin { onBack() } },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading && uiState.title.isNotBlank() && uiState.imageUrl.isNotEmpty() && uiState.selectedCategory.isNotBlank()
            ) {
                if (uiState.isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                else Text("Publicar Outfit")
            }

            uiState.error?.let { error -> Text(text = error, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp)) }
        }
    }
}