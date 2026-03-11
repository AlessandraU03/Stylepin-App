package com.ale.stylepin.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute

// Importaciones de los componentes y pantallas
import com.ale.stylepin.core.presentation.components.StylePinBottomBar
import com.ale.stylepin.features.auth.presentation.screens.LoginScreen
import com.ale.stylepin.features.auth.presentation.screens.RegisterScreen
import com.ale.stylepin.features.auth.presentation.viewmodels.LoginViewModel
import com.ale.stylepin.features.auth.presentation.viewmodels.RegisterViewModel
import com.ale.stylepin.features.pins.presentation.screens.AddPinScreen
import com.ale.stylepin.features.pins.presentation.screens.EditPinScreen
import com.ale.stylepin.features.pins.presentation.screens.PinsScreen
import com.ale.stylepin.features.pins.presentation.viewmodels.AddPinViewModel
import com.ale.stylepin.features.pins.presentation.viewmodels.EditPinViewModel
import com.ale.stylepin.features.pins.presentation.viewmodels.PinsViewModel
import com.ale.stylepin.features.profile.presentation.screens.ProfileScreen
import com.ale.stylepin.features.profile.presentation.viewmodels.ProfileViewModel

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()
    // Observamos la pila de navegación para saber en qué ruta estamos actualmente
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Condición: Mostrar la barra inferior SÓLO si estamos en una de estas 4 rutas
    val showBottomBar = currentDestination?.hasRoute(PinsRoute::class) == true ||
            currentDestination?.hasRoute(SearchRoute::class) == true ||
            currentDestination?.hasRoute(AlertsRoute::class) == true ||
            currentDestination?.hasRoute(ProfileRoute::class) == true

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                StylePinBottomBar(
                    currentRoute = currentDestination?.route,
                    onNavigate = { title ->
                        when (title) {
                            "Inicio" -> navController.navigate(PinsRoute) { popUpTo(PinsRoute) { inclusive = true } }
                            "Explorar" -> navController.navigate(SearchRoute) { popUpTo(PinsRoute) }
                            "Alertas" -> navController.navigate(AlertsRoute) { popUpTo(PinsRoute) }
                            "Perfil" -> navController.navigate(ProfileRoute) { popUpTo(PinsRoute) }
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            // El FAB rojo circular que va en medio de la barra
            if (showBottomBar) {
                FloatingActionButton(
                    onClick = { navController.navigate(AddPinRoute) },
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar Pin")
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = LoginRoute, // Iniciamos en el Login
            modifier = Modifier.padding(innerPadding) // Respetamos el espacio de la BottomBar
        ) {

            // --- SECCIÓN AUTH (Sin barra inferior) ---
            composable<LoginRoute> {
                val viewModel: LoginViewModel = hiltViewModel()
                LoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = {
                        navController.navigate(PinsRoute) {
                            popUpTo(LoginRoute) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate(RegisterRoute)
                    }
                )
            }

            composable<RegisterRoute> {
                val viewModel: RegisterViewModel = hiltViewModel()
                RegisterScreen(
                    viewModel = viewModel,
                    onRegisterSuccess = {
                        navController.navigate(PinsRoute) {
                            popUpTo(RegisterRoute) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.popBackStack()
                    }
                )
            }

            // --- TABS PRINCIPALES DE LA BARRA ---
            composable<PinsRoute> {
                val viewModel: PinsViewModel = hiltViewModel()
                PinsScreen(
                    viewModel = viewModel,
                    onNavigateToAddPin = {
                        navController.navigate(AddPinRoute)
                    },
                    onNavigateToEditPin = { pin ->
                        navController.navigate(EditPinRoute(pin.id, pin.title, pin.imageUrl, pin.category, pin.season))
                    }
                )
            }

            // Pantallas temporales (placeholders) para la barra
            composable<SearchRoute> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Pantalla de Explorar")
                }
            }

            composable<AlertsRoute> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Pantalla de Alertas")
                }
            }

            composable<ProfileRoute> {
                val viewModel: ProfileViewModel = hiltViewModel()
                ProfileScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onEditProfileClick = { navController.navigate(EditProfileRoute) }
                )
            }

            // --- PANTALLAS SECUNDARIAS (Sin barra inferior) ---
            composable<AddPinRoute> {
                val viewModel: AddPinViewModel = hiltViewModel()
                AddPinScreen(
                    viewModel = viewModel,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable<EditPinRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<EditPinRoute>()
                val viewModel: EditPinViewModel = hiltViewModel()

                // Inicializamos la data en el ViewModel cuando entra a la ruta
                LaunchedEffect(route) {
                    viewModel.initData(route.title, route.imageUrl, route.category, route.season)
                }

                EditPinScreen(
                    pinId = route.id,
                    viewModel = viewModel,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable<EditProfileRoute> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Pantalla para Editar Perfil (Próximamente)")
                }
            }
        }
    }
}