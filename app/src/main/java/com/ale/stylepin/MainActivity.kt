package com.ale.stylepin

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import com.ale.stylepin.core.navigation.NavigationWrapper
import com.ale.stylepin.core.network.StylePinWebSocketManager
import com.ale.stylepin.core.ui.theme.StylepinTheme
import com.ale.stylepin.features.pins.domain.repository.PinSyncManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject
    lateinit var webSocketManager: StylePinWebSocketManager

    @Inject
    lateinit var pinSyncManager: PinSyncManager

    // ✅ Registrador de permisos (SIN intención de guardar token aquí)
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            println("✅ Permiso de notificaciones concedido")
            // ✅ NO guardar token aquí - esperar a que el usuario inicie sesión
        } else {
            println("❌ Permiso de notificaciones denegado")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // ✅ Solo pedir permiso, NO registrar token
        requestNotificationPermission()
        
        handleNotificationIntent(intent)

        pinSyncManager.schedulePeriodicSync()
        
        setContent {
            StylepinTheme {
                NavigationWrapper()
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(
                android.Manifest.permission.POST_NOTIFICATIONS
            )
        }
    }

    private fun handleNotificationIntent(intent: Intent?) {
        val action = intent?.getStringExtra("action") ?: return
        val username = intent.getStringExtra("username")
        val boardName = intent.getStringExtra("board_name")

        when (action) {
            "view_likes" -> {
                println("👀 Navegar a likes de: $username")
            }
            "view_profile" -> {
                println("👀 Navegar a perfil de: $username")
            }
            "view_pin" -> {
                println("👀 Navegar a comentarios de pin")
            }
            "view_board" -> {
                println("👀 Navegar a tablero: $boardName")
            }
        }
    }
}