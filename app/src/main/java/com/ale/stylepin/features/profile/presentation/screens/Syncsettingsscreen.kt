package com.ale.stylepin.features.profile.presentation.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.work.WorkInfo
import com.ale.stylepin.features.pins.data.workers.SyncWorker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyncSettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current

    // ← CLAVE: observeAsState() registra un observer real en el LiveData
    val workInfos by SyncWorker.getManualSyncState(context).observeAsState()
    val workInfo  = workInfos?.firstOrNull()

    val syncState = workInfo?.state
    val progress  = workInfo?.progress?.getInt(SyncWorker.KEY_PROGRESS, 0) ?: 0
    val stage     = workInfo?.progress?.getString(SyncWorker.KEY_STAGE) ?: ""

    val isSyncing  = syncState == WorkInfo.State.RUNNING || syncState == WorkInfo.State.ENQUEUED
    val syncOk     = syncState == WorkInfo.State.SUCCEEDED
    val syncFailed = syncState == WorkInfo.State.FAILED

    // Progreso efectivo: 100 si terminó bien
    val effectiveProgress = when {
        syncOk -> 100
        else   -> progress
    }
    val effectiveStage = when {
        syncOk     -> "Sincronización completada"
        syncFailed -> "Error al sincronizar"
        else       -> stage
    }

    val animatedProgress by animateFloatAsState(
        targetValue = effectiveProgress / 100f,
        animationSpec = tween(durationMillis = 500),
        label = "sync_progress"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sincronización de pines", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Tarjeta informativa
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CloudDownload,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "Sincronización automática",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Los pines se sincronizan automáticamente cada 12 horas " +
                                "cuando hay Wi-Fi y la batería no está baja, " +
                                "para que puedas verlos sin conexión.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // ── Bloque de progreso ─────────────────────────────
            when {
                isSyncing || syncOk -> {
                    val cardColor = if (syncOk) Color(0xFFE8F5E9)
                    else MaterialTheme.colorScheme.primaryContainer
                    val textColor = if (syncOk) Color(0xFF2E7D32)
                    else MaterialTheme.colorScheme.onPrimaryContainer
                    val barColor  = if (syncOk) Color(0xFF4CAF50)
                    else MaterialTheme.colorScheme.primary

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = cardColor)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Etapa + porcentaje
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (syncOk) {
                                        Icon(
                                            Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = Color(0xFF4CAF50),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Text(
                                        text = effectiveStage.ifEmpty { "Iniciando..." },
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = textColor
                                    )
                                }
                                Text(
                                    text = "$effectiveProgress%",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = barColor,
                                    fontSize = 16.sp
                                )
                            }

                            // Barra de progreso animada
                            LinearProgressIndicator(
                                progress = { animatedProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp),
                                color = barColor,
                                trackColor = barColor.copy(alpha = 0.2f),
                            )

                            // Etiquetas de etapas
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                listOf(
                                    "Inicio"   to 5,
                                    "Conexión" to 20,
                                    "Descarga" to 45,
                                    "Guardado" to 80,
                                    "Listo"    to 100
                                ).forEach { (label, threshold) ->
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (effectiveProgress >= threshold) barColor
                                        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                        fontWeight = if (effectiveProgress >= threshold)
                                            FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }

                syncFailed -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                Icons.Default.WifiOff,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                "Error al sincronizar.\nRevisa tu conexión e intenta de nuevo.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }

            // Botón sincronizar
            Button(
                onClick = { SyncWorker.runNow(context) },
                enabled = !isSyncing,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isSyncing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Sincronizando...")
                } else {
                    Icon(
                        Icons.Default.Sync,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Sincronizar ahora", fontWeight = FontWeight.SemiBold)
                }
            }

            Text(
                "Puedes sincronizar manualmente en cualquier momento, " +
                        "independientemente del Wi-Fi o el nivel de batería.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}