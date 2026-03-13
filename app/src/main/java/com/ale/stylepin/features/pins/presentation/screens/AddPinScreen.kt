// com/ale/stylepin/features/pins/presentation/screens/AddPinScreen.kt
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.ale.stylepin.features.pins.presentation.viewmodels.PinFormEvent
import com.ale.stylepin.features.pins.presentation.viewmodels.PinsViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
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
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Pin") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Selector de imagen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                    .clickable { showImageSourceDialog = true },
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
                        Icon(Icons.Default.Image, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Toca para añadir foto", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
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
                label = { Text("¿Dónde comprar? (ej. Zara, H&M)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.purchaseLink,
                onValueChange = { viewModel.onFormEvent(PinFormEvent.PurchaseLinkChanged(it)) },
                label = { Text("Link de compra (http...)") },
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
                onClick = { viewModel.savePin { onBack() } },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !uiState.isLoading && uiState.title.isNotBlank() && uiState.selectedCategory.isNotBlank() && uiState.imageUrl.isNotEmpty()
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("Publicar Outfit")
                }
            }

            uiState.error?.let { error ->
                Text(text = error, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 16.dp))
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}