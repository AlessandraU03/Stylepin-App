package com.ale.stylepin.core.navigation

import androidx.compose.runtime.Composable
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
import com.ale.stylepin.features.pins.presentation.viewmodels.PinsViewModel

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = LoginRoute) {

        composable<LoginRoute> {
            LoginScreen(
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
            RegisterScreen(
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
            PinsScreen(
                onNavigateToAdd = {
                    navController.navigate(AddPinRoute)
                },
                onNavigateToEdit = { id, title, imageUrl, category, season ->
                    navController.navigate(EditPinRoute(id, title, imageUrl, category, season))
                }
            )
        }

        composable<AddPinRoute> {
            AddPinScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<EditPinRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<EditPinRoute>()
            EditPinScreen(
                pinId = route.id,
                title = route.title,
                imageUrl = route.imageUrl,
                category = route.category,
                season = route.season,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}