package com.ale.stylepin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ale.stylepin.core.navigation.NavigationWrapper
import com.ale.stylepin.core.ui.theme.StylepinTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StylepinTheme {
                NavigationWrapper()
            }
        }
    }
}