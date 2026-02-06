package com.ale.stylepin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ale.stylepin.core.di.AppContainer
import com.ale.stylepin.core.navigation.NavigationWrapper
import com.ale.stylepin.features.auth.di.AuthModule
import com.ale.stylepin.features.auth.navigation.AuthNavGraph
import com.ale.stylepin.features.pins.di.PinModule
import com.ale.stylepin.features.pins.navigation.PinsNavGraph
import com.ale.stylepin.core.ui.theme.StylepinTheme

class MainActivity : ComponentActivity() {
    private lateinit var appContainer: AppContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appContainer = AppContainer(this)

        // Inicializamos ambos módulos pasando el appContainer
        val authModule = AuthModule(appContainer)
        val pinsModule = PinModule(appContainer)

        // Lista de grafos: Ahora incluimos PinsNavGraph
        val navGraphs = listOf(
            AuthNavGraph(authModule),
            PinsNavGraph(pinsModule)
        )

        enableEdgeToEdge()
        setContent {
            StylepinTheme {
                // El NavigationWrapper se encarga de registrar todos los grafos de la lista
                NavigationWrapper(navGraphs)
            }
        }
    }
}