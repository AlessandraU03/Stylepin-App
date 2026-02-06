package com.ale.stylepin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ale.stylepin.core.di.AppContainer
import com.ale.stylepin.core.navigation.NavigationWrapper
import com.ale.stylepin.features.auth.di.AuthModule
import com.ale.stylepin.features.auth.navigation.AuthNavGraph
import com.ale.stylepin.core.ui.theme.StylepinTheme


class MainActivity : ComponentActivity() {
    // Usamos la clase directamente como en tu ejemplo
    private lateinit var appContainer: AppContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appContainer = AppContainer(this)

        // Ahora el módulo recibe el contenedor completo sin errores
        val authModule = AuthModule(appContainer)

        val navGraphs = listOf(
            AuthNavGraph(authModule)
        )

        enableEdgeToEdge()
        setContent {
            StylepinTheme {
                NavigationWrapper(navGraphs)
            }
        }
    }
}