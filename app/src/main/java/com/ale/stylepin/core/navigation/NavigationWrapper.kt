package com.ale.stylepin.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
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

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = LoginRoute) {

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
    }
}