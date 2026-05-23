package com.buildingblocks.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.*
import androidx.navigation.compose.*
import com.buildingblocks.app.feature.addeditset.AddEditSetScreen
import com.buildingblocks.app.feature.auth.AuthScreen
import com.buildingblocks.app.feature.backlog.BacklogScreen
import com.buildingblocks.app.feature.collection.CollectionScreen
import com.buildingblocks.app.feature.home.HomeScreen
import com.buildingblocks.app.feature.premium.PremiumScreen
import com.buildingblocks.app.feature.profile.ProfileScreen
import com.buildingblocks.app.feature.setdetails.SetDetailsScreen
import com.buildingblocks.app.feature.storage.StorageScreen
import com.buildingblocks.app.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val sessionManager: SessionManager
) : ViewModel() {
    val userId: StateFlow<java.util.UUID?> = sessionManager.userId
}

private data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

private val navItems = listOf(
    BottomNavItem(Screen.Home, "Home", Icons.Filled.Home, Icons.Outlined.Home),
    BottomNavItem(Screen.Collection, "Collection", Icons.Filled.Layers, Icons.Outlined.Layers),
    BottomNavItem(Screen.Backlog, "Backlog", Icons.Filled.Build, Icons.Outlined.Build),
    BottomNavItem(Screen.Storage, "Storage", Icons.Filled.Inventory2, Icons.Outlined.Inventory2),
)

// Placeholder UUID used in local/offline mode — means the user has not signed in with a real account.
private val LOCAL_PLACEHOLDER_ID = java.util.UUID.fromString("00000000-0000-0000-0000-000000000001")

@Composable
fun AppNavigation(vm: AppViewModel = hiltViewModel()) {
    val userId by vm.userId.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route

    // When the user signs out (userId becomes localPlaceholder and we're not on Auth), go to Auth.
    LaunchedEffect(userId) {
        if (userId == LOCAL_PLACEHOLDER_ID && currentRoute != null && currentRoute != Screen.Auth.route) {
            navController.navigate(Screen.Auth.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    val showBottomBar = navItems.any { it.screen.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    navItems.forEach { item ->
                        val selected = currentRoute == item.screen.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            // Start at Auth; signed-in users are immediately redirected to Home below.
            startDestination = Screen.Auth.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Auth.route) {
                // If already signed in (real account or local mode), skip Auth.
                LaunchedEffect(userId) {
                    if (userId != null) {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Auth.route) { inclusive = true }
                        }
                    }
                }
                AuthScreen(onSignedIn = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                })
            }
            composable(Screen.Home.route) {
                HomeScreen(
                    onAddSet = { navController.navigate(Screen.AddSet.route) },
                    onProfileClick = { navController.navigate(Screen.Profile.route) }
                )
            }
            composable(Screen.Collection.route) {
                CollectionScreen(
                    onSetClick = { id -> navController.navigate(Screen.SetDetails.createRoute(id)) },
                    onAddSet = { navController.navigate(Screen.AddSet.route) },
                    onProfileClick = { navController.navigate(Screen.Profile.route) }
                )
            }
            composable(Screen.Backlog.route) {
                BacklogScreen(onSetClick = { id -> navController.navigate(Screen.SetDetails.createRoute(id)) })
            }
            composable(Screen.Storage.route) {
                StorageScreen()
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateToPremium = { navController.navigate(Screen.Premium.route) }
                )
            }
            composable(Screen.Premium.route) {
                PremiumScreen(onBack = { navController.popBackStack() })
            }
            composable(Screen.AddSet.route) {
                AddEditSetScreen(
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() },
                    onLimitReached = { navController.navigate(Screen.Premium.route) }
                )
            }
            composable(
                Screen.SetDetails.ROUTE,
                arguments = listOf(navArgument("setId") { type = NavType.StringType })
            ) {
                SetDetailsScreen(
                    onBack = { navController.popBackStack() },
                    onEdit = { id -> navController.navigate(Screen.EditSet.createRoute(id)) },
                    onDeleted = { navController.popBackStack() }
                )
            }
            composable(
                Screen.EditSet.ROUTE,
                arguments = listOf(navArgument("setId") { type = NavType.StringType })
            ) {
                AddEditSetScreen(
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() },
                    onLimitReached = { navController.navigate(Screen.Premium.route) }
                )
            }
        }
    }
}
