package com.ale.stylepin

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.fragment.app.FragmentActivity
import com.ale.stylepin.core.navigation.NavigationWrapper
import com.ale.stylepin.core.network.StylePinWebSocketManager
import com.ale.stylepin.core.ui.theme.StylepinTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject
    lateinit var webSocketManager: StylePinWebSocketManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StylepinTheme {
                // Escucha global de notificaciones por WebSocket
                LaunchedEffect(Unit) {
                    webSocketManager.connect()
                    webSocketManager.notifications.collect { notification ->
                        Toast.makeText(
                            applicationContext,
                            "Notificación: ${notification.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                NavigationWrapper()
            }
        }
    }
}
