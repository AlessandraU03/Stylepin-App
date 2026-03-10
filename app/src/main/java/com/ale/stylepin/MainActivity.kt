package com.ale.stylepin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ale.stylepin.core.navigation.NavigationWrapper
import com.ale.stylepin.features.pins.navigation.PinsNavGraph
import com.ale.stylepin.core.ui.theme.StylepinTheme
import com.ale.stylepin.features.auth.navigation.AuthNavGraph
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StylepinTheme {
                NavigationWrapper()   // ✅ Sin parámetros
            }
        }
    }
}