package com.ale.stylepin.features.pins.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import androidx.compose.runtime.LaunchedEffect
import com.ale.stylepin.core.navigation.FeatureNavGraph
import com.ale.stylepin.core.navigation.PinsRoute
import com.ale.stylepin.core.navigation.AddPinRoute
import com.ale.stylepin.core.navigation.EditPinRoute
import com.ale.stylepin.features.pins.di.PinModule
import com.ale.stylepin.features.pins.presentation.screens.PinsScreen
import com.ale.stylepin.features.pins.presentation.screens.AddPinScreen
import com.ale.stylepin.features.pins.presentation.screens.EditPinScreen
import com.ale.stylepin.features.pins.presentation.viewmodels.PinsViewModel
import com.ale.stylepin.features.pins.presentation.viewmodels.AddPinViewModel
import com.ale.stylepin.features.pins.presentation.viewmodels.EditPinViewModel

class PinsNavGraph(private val pinsModule: PinModule) : FeatureNavGraph {
    override fun registerGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {

        navGraphBuilder.composable<PinsRoute> {
            val viewModel: PinsViewModel = viewModel(
                factory = pinsModule.providePinsViewModelFactory()
            )
            // Pasamos las acciones de navegación
            PinsScreen(
                viewModel = viewModel,
                onNavigateToAddPin = { navController.navigate(AddPinRoute) },
                onNavigateToEditPin = { pin ->
                    navController.navigate(
                        EditPinRoute(pin.id, pin.title, pin.imageUrl, pin.category, pin.season)
                    )
                }
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

        // 3. Pantalla para Editar un Pin
        navGraphBuilder.composable<EditPinRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<EditPinRoute>()
            val editViewModel: EditPinViewModel = viewModel(
                factory = pinsModule.provideEditPinViewModelFactory()
            )

            LaunchedEffect(route) {
                editViewModel.initData(route.title, route.imageUrl, route.category, route.season)
            }

            EditPinScreen(
                pinId = route.id,
                viewModel = editViewModel,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}