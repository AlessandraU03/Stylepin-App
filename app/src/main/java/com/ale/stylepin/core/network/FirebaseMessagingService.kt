package com.ale.stylepin.core.network

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.ale.stylepin.MainActivity
import com.ale.stylepin.R
import com.ale.stylepin.features.notifications.domain.usecases.SendFCMTokenUseCase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var sendFCMTokenUseCase: SendFCMTokenUseCase

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // ✅ Se llama cuando Firebase rota el token automáticamente
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        println("🆕 Nuevo FCM Token: ${token.take(30)}...")
        
        serviceScope.launch {
            try {
                // ✅ Llamar correctamente con .execute()
                sendFCMTokenUseCase.execute(token, Build.MODEL)
                println("✅ Token enviado al backend exitosamente")
            } catch (e: Exception) {
                println("❌ Error enviando token: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    // ✅ Recibir notificaciones push
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        
        println("📨 Notificación recibida:")
        println("   Title: ${message.notification?.title}")
        println("   Body: ${message.notification?.body}")
        println("   Data: ${message.data}")

        message.notification?.let {
            val data = message.data
            val notificationType = data["type"] ?: "default"
            
            showNotification(
                title = it.title ?: "StylePin",
                body = it.body ?: "",
                notificationType = notificationType,
                data = data
            )
        }
    }

    private fun showNotification(
        title: String,
        body: String,
        notificationType: String,
        data: Map<String, String>
    ) {
        val channelId = "stylepin_notifications"
        val manager = getSystemService(NotificationManager::class.java)

        // Crear canal (Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Notificaciones StylePin",
                NotificationManager.IMPORTANCE_HIGH
            )
            manager.createNotificationChannel(channel)
        }

        // 🎯 Intent con datos según tipo de notificación
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            
            when (notificationType) {
                "like" -> {  // ✅ Coincide con backend
                    putExtra("action", "view_likes")
                    putExtra("username", data["actor_username"])
                }
                "follow" -> {  // ✅ Coincide con backend
                    putExtra("action", "view_profile")
                    putExtra("username", data["actor_username"])
                }
                "comment" -> {  // ✅ Coincide con backend
                    putExtra("action", "view_pin")
                    putExtra("username", data["actor_username"])
                }
                "board_collaboration" -> {  // ✅ Coincide con backend
                    putExtra("action", "view_board")
                    putExtra("board_name", data["board_name"])
                }
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        manager.notify(System.currentTimeMillis().toInt(), notification)
    }
}