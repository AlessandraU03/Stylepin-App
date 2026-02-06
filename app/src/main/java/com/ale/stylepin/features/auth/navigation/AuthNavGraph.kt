package com.ale.stylepin.features.auth.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.ale.stylepin.core.navigation.FeatureNavGraph
import com.ale.stylepin.core.navigation.LoginRoute
import com.ale.stylepin.core.navigation.PinsRoute
import com.ale.stylepin.features.auth.di.AuthModule
import com.ale.stylepin.features.auth.presentation.screens.LoginScreen
import com.ale.stylepin.features.auth.presentation.viewmodels.LoginViewModel

class AuthNavGraph(
    private val authModule: AuthModule
) : FeatureNavGraph {

    override fun registerGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.composable<LoginRoute> {
            val viewModel: LoginViewModel = viewModel(
                factory = authModule.provideLoginViewModelFactory()
            )

            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {
                    navController.navigate(PinsRoute)
                }
            )
        }
    }
}