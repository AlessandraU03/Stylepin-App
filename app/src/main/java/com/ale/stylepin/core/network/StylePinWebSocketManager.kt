package com.ale.stylepin.core.network

import android.util.Log
import com.ale.stylepin.features.auth.domain.repositories.AuthRepository
import com.ale.stylepin.features.community.domain.entities.Notification
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import okhttp3.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StylePinWebSocketManager @Inject constructor(
    private val client: OkHttpClient,
    private val authRepository: AuthRepository,
    private val gson: Gson
) {
    private var webSocket: WebSocket? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _notifications = MutableSharedFlow<Notification>(
        extraBufferCapacity = 10,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val notifications = _notifications.asSharedFlow()

    private val _connectionStatus = MutableSharedFlow<Boolean>(replay = 1)
    val connectionStatus = _connectionStatus.asSharedFlow()

    fun connect() {
        if (webSocket != null) return

        val token = authRepository.getStoredToken()
        if (token == null) return

        val wsUrl = "wss://stylepin.ddns.net/ws?token=$token"
        val request = Request.Builder().url(wsUrl).build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                scope.launch { 
                    _connectionStatus.emit(true)
                    _notifications.emit(Notification(type = "system", message = "Conectado al servidor"))
                }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("WebSocket", "Raw message: $text")
                try {
                    var notification = gson.fromJson(text, Notification::class.java)
                    
                    // Si el backend manda el mensaje de "connected" inicial sin message
                    if (notification.type == "connected" && notification.message.isNullOrEmpty()) {
                        notification = notification.copy(message = "Sesión activa en el servidor")
                    }

                    scope.launch { _notifications.emit(notification) }
                } catch (e: Exception) {
                    scope.launch { 
                        _notifications.emit(Notification(type = "text", message = text))
                    }
                }
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                this@StylePinWebSocketManager.webSocket = null
                scope.launch { _connectionStatus.emit(false) }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                this@StylePinWebSocketManager.webSocket = null
                scope.launch { _connectionStatus.emit(false) }
            }
        })
    }

    fun disconnect() {
        webSocket?.close(1000, "App disconnect")
        webSocket = null
    }
}
