package com.ale.stylepin.features.community.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ale.stylepin.core.network.StylePinWebSocketManager
import com.ale.stylepin.features.community.domain.entities.Notification
import com.ale.stylepin.features.notifications.domain.usecases.GetNotificationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para la pantalla de Alertas (tab de notificaciones).
 *
 * CORRECCIÓN: Ahora combina DOS fuentes de notificaciones:
 *  1. Las notificaciones históricas del servidor (GET /api/v1/notifications)
 *  2. Las notificaciones en tiempo real que llegan por WebSocket
 *
 * Antes solo escuchaba el WS, por lo que la lista aparecía vacía hasta que
 * llegaba un evento nuevo en esa sesión.
 */
@HiltViewModel
class AlertsViewModel @Inject constructor(
    private val webSocketManager: StylePinWebSocketManager,
    private val getNotificationsUseCase: GetNotificationsUseCase   // ← NUEVO
) : ViewModel() {

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications = _notifications.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        // 1. Cargar notificaciones históricas del servidor al abrir la pantalla
        loadServerNotifications()

        // 2. Escuchar WebSocket y agregar nuevas notificaciones al tope de la lista
        viewModelScope.launch {
            webSocketManager.notifications.collect { newNotif ->
                // Ignoramos el mensaje de sistema "connected"
                if (newNotif.type == "system" || newNotif.type == "connected") return@collect
                _notifications.update { currentList ->
                    // Mapeamos la entidad del WS (community.Notification) a un formato
                    // unificado usando los campos disponibles
                    val mapped = Notification(
                        type = newNotif.type,
                        message = newNotif.message ?: "",
                        actorId = newNotif.actorId,
                        actorUsername = newNotif.actorUsername,
                        pinId = newNotif.pinId,
                        createdAt = newNotif.createdAt
                    )
                    listOf(mapped) + currentList
                }
            }
        }
    }

    fun loadServerNotifications() {
        viewModelScope.launch {
            _isLoading.value = true
            getNotificationsUseCase().onSuccess { serverNotifs ->
                // Convertimos las notificaciones del servidor al formato de community.Notification
                // (que es el que usa esta pantalla / AlertsScreen)
                val mapped = serverNotifs.map { notif ->
                    Notification(
                        type = notif.type,
                        message = buildMessageFromNotification(notif.type, notif.title, notif.body),
                        actorId = null,
                        actorUsername = extractActorFromBody(notif.body),
                        pinId = null,
                        createdAt = notif.createdAt
                    )
                }
                // Fusionamos con lo que ya haya llegado por WS, evitando duplicados por índice
                _notifications.update { existing ->
                    val merged = (mapped + existing).distinctBy { it.createdAt + it.message }
                    merged
                }
            }
            _isLoading.value = false
        }
    }

    /**
     * Construye un mensaje legible combinando el tipo, título y body de la notificación del servidor.
     */
    private fun buildMessageFromNotification(type: String, title: String, body: String): String {
        return body.ifBlank { title }
    }

    /**
     * Intenta extraer el nombre de usuario del body del servidor.
     * Los mensajes del servidor suelen tener formato "X te siguió", "X comentó en tu pin", etc.
     */
    private fun extractActorFromBody(body: String): String? {
        return body.split(" ").firstOrNull()?.takeIf { it.isNotBlank() }
    }
}