package com.ale.stylepin.features.pins.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.ale.stylepin.core.navigation.FeatureNavGraph
import com.ale.stylepin.core.navigation.PinsRoute
import com.ale.stylepin.core.navigation.AddPinRoute
import com.ale.stylepin.features.pins.di.PinModule
import com.ale.stylepin.features.pins.presentation.screens.PinsScreen
import com.ale.stylepin.features.pins.presentation.screens.AddPinScreen
import com.ale.stylepin.features.pins.presentation.viewmodels.PinsViewModel
import com.ale.stylepin.features.pins.presentation.viewmodels.AddPinViewModel

class PinsNavGraph(private val pinsModule: PinModule) : FeatureNavGraph {
    override fun registerGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {


        navGraphBuilder.composable<PinsRoute> {
            val viewModel: PinsViewModel = viewModel(
                factory = pinsModule.providePinsViewModelFactory()
            )
            // Pasamos la acción de navegar al presionar el botón de agregar
            PinsScreen(
                viewModel = viewModel,
                onNavigateToAddPin = { navController.navigate(AddPinRoute) }
            )
        }

        // 2. Pantalla para Agregar un Pin Nuevo
        navGraphBuilder.composable<AddPinRoute> {
            val addViewModel: AddPinViewModel = viewModel(
                factory = pinsModule.provideAddPinViewModelFactory()
            )
            AddPinScreen(
                viewModel = addViewModel,
                onBack = { navController.popBackStack() } // Regresa al feed
            )
        }
    }
}