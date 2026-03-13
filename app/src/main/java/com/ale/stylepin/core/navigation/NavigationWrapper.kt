package com.ale.stylepin.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute

// Imports de tus pantallas y componentes
import com.ale.stylepin.core.presentation.components.StylePinBottomBar
import com.ale.stylepin.features.auth.presentation.screens.LoginScreen
import com.ale.stylepin.features.auth.presentation.screens.RegisterScreen
import com.ale.stylepin.features.auth.presentation.viewmodels.LoginViewModel
import com.ale.stylepin.features.auth.presentation.viewmodels.RegisterViewModel
import com.ale.stylepin.features.pins.presentation.screens.AddPinScreen
import com.ale.stylepin.features.pins.presentation.screens.EditPinScreen
import com.ale.stylepin.features.pins.presentation.screens.PinDetailScreen
import com.ale.stylepin.features.pins.presentation.screens.PinsScreen
import com.ale.stylepin.features.pins.presentation.viewmodels.PinsViewModel

// Imports de Profile y Settings
import com.ale.stylepin.features.profile.presentation.screens.ProfileScreen
import com.ale.stylepin.features.profile.presentation.screens.SettingsScreen
import com.ale.stylepin.features.profile.presentation.viewmodels.ProfileViewModel
import com.ale.stylepin.features.profile.presentation.viewmodels.SettingsViewModel

// Imports de Community
import com.ale.stylepin.features.community.presentation.screens.CommunityScreen
import com.ale.stylepin.features.community.presentation.viewmodels.CommunityViewModel

import com.ale.stylepin.features.profile.presentation.viewmodels.EditProfileViewModel
import com.ale.stylepin.features.profile.presentation.screens.EditProfileScreen

// Imports de Boards
import com.ale.stylepin.features.boards.presentation.screens.BoardsScreen
import com.ale.stylepin.features.boards.presentation.screens.BoardDetailScreen
import com.ale.stylepin.features.boards.presentation.screens.CreateBoardScreen
import com.ale.stylepin.features.boards.presentation.screens.EditBoardScreen
import com.ale.stylepin.features.boards.presentation.viewmodels.BoardsViewModel

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.hasRoute(PinsRoute::class) == true ||
            currentDestination?.hasRoute(SearchRoute::class) == true ||
            currentDestination?.hasRoute(AlertsRoute::class) == true ||
            currentDestination?.hasRoute(ProfileRoute::class) == true ||
            currentDestination?.hasRoute(BoardsRoute::class) == true

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                StylePinBottomBar(
                    currentRoute = currentDestination?.route,
                    onNavigate = { title ->
                        when (title) {
                            "Inicio"   -> navController.navigate(PinsRoute) { popUpTo(PinsRoute) { inclusive = true } }
                            "Explorar" -> navController.navigate(BoardsRoute) { popUpTo(PinsRoute) }
                            "Alertas"  -> navController.navigate(AlertsRoute) { popUpTo(PinsRoute) }
                            "Perfil"   -> navController.navigate(ProfileRoute) { popUpTo(PinsRoute) }
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            if (showBottomBar && currentDestination?.hasRoute(PinsRoute::class) == true) {
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
            startDestination = LoginRoute,
            modifier = Modifier.padding(innerPadding)
        ) {

            // --- AUTH ---
            composable<LoginRoute> {
                val viewModel: LoginViewModel = hiltViewModel()
                LoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = {
                        navController.navigate(PinsRoute) {
                            popUpTo(LoginRoute) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = { navController.navigate(RegisterRoute) }
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
                    onNavigateToLogin = { navController.popBackStack() }
                )
            }

            // --- PINS ---
            composable<PinsRoute> {
                val viewModel: PinsViewModel = hiltViewModel()
                PinsScreen(
                    viewModel = viewModel,
                    onNavigateToAddPin = { navController.navigate(AddPinRoute) },
                    onNavigateToPinDetail = { pinId ->
                        navController.navigate(PinDetailRoute(id = pinId))
                    },
                    onNavigateToEditPin = { pin ->
                        navController.navigate(EditPinRoute(id = pin.id))
                    }
                )
            }

            composable<AddPinRoute> {
                val viewModel: PinsViewModel = hiltViewModel()
                AddPinScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable<PinDetailRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<PinDetailRoute>()
                val viewModel: PinsViewModel = hiltViewModel()
                PinDetailScreen(
                    pinId = route.id,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onNavigateToEditPin = { pin ->
                        navController.navigate(EditPinRoute(id = pin.id))
                    }
                )
            }

            composable<EditPinRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<EditPinRoute>()
                val viewModel: PinsViewModel = hiltViewModel()
                EditPinScreen(
                    pinId = route.id,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            // --- BOARDS ---
            composable<BoardsRoute> {
                val viewModel: BoardsViewModel = hiltViewModel()
                val pinsViewModel: PinsViewModel = hiltViewModel()
                val pinsState by pinsViewModel.uiState.collectAsStateWithLifecycle()
                
                BoardsScreen(
                    userId = pinsState.currentUserId ?: "",
                    viewModel = viewModel,
                    onNavigateToBoardDetail = { boardId ->
                        navController.navigate(BoardDetailRoute(id = boardId))
                    },
                    onNavigateToCreateBoard = {
                        navController.navigate(CreateBoardRoute(userId = pinsState.currentUserId ?: ""))
                    },
                    onNavigateToEditBoard = { board ->
                        navController.navigate(EditBoardRoute(id = board.id))
                    }
                )
            }

            composable<BoardDetailRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<BoardDetailRoute>()
                val viewModel: BoardsViewModel = hiltViewModel()
                BoardDetailScreen(
                    boardId = route.id,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onPinClick = { pinId ->
                        navController.navigate(PinDetailRoute(id = pinId))
                    },
                    onEditBoard = { boardId ->
                        navController.navigate(EditBoardRoute(id = boardId))
                    }
                )
            }

            composable<CreateBoardRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<CreateBoardRoute>()
                val viewModel: BoardsViewModel = hiltViewModel()
                CreateBoardScreen(
                    userId = route.userId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable<EditBoardRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<EditBoardRoute>()
                val viewModel: BoardsViewModel = hiltViewModel()
                val pinsViewModel: PinsViewModel = hiltViewModel()
                val pinsState by pinsViewModel.uiState.collectAsStateWithLifecycle()
                
                EditBoardScreen(
                    boardId = route.id,
                    userId = pinsState.currentUserId ?: "",
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            // --- PLACEHOLDERS / OTROS ---
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

            // --- PROFILE Y COMMUNITY ---
            composable<ProfileRoute> {
                val viewModel: ProfileViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                ProfileScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onEditProfileClick = { navController.navigate(EditProfileRoute) },
                    onSettingsClick = { navController.navigate(SettingsRoute) },
                    onCommunityClick = { tabIndex ->
                        uiState.profile?.id?.let { userId ->
                            navController.navigate(CommunityRoute(initialTab = tabIndex, userId = userId))
                        }
                    }
                )
            }

            composable<EditProfileRoute> {
                val viewModel: EditProfileViewModel = hiltViewModel()
                EditProfileScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable<SettingsRoute> {
                val viewModel: SettingsViewModel = hiltViewModel()
                SettingsScreen(
                    onBack = { navController.popBackStack() },
                    onLogout = {
                        viewModel.logout()
                        navController.navigate(LoginRoute) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable<CommunityRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<CommunityRoute>()
                val viewModel: CommunityViewModel = hiltViewModel()

                LaunchedEffect(route.userId) {
                    viewModel.loadData(route.userId)
                }

                CommunityScreen(
                    initialTab = route.initialTab,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
