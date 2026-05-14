package com.shaalevikas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.shaalevikas.data.model.UserProfile
import com.shaalevikas.data.model.UserRole
import com.shaalevikas.ui.navigation.NavRoutes
import com.shaalevikas.ui.screens.admin.AdminNeedEditorScreen
import com.shaalevikas.ui.screens.auth.LoginScreen
import com.shaalevikas.ui.screens.auth.SignupScreen
import com.shaalevikas.ui.screens.dashboard.DashboardScreen
import com.shaalevikas.ui.screens.detail.NeedDetailScreen
import com.shaalevikas.ui.screens.halloffame.HallOfFameScreen
import com.shaalevikas.ui.theme.ShaaleVikasTheme
import com.shaalevikas.utils.UiState
import com.shaalevikas.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShaaleVikasTheme {
                ShaaleVikasAppRoot()
            }
        }
    }
}

@Composable
private fun ShaaleVikasAppRoot(
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsStateWithLifecycle()
    val profileState by authViewModel.profileState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val profile = (profileState as? UiState.Success<UserProfile?>)?.data
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val showBottomBar = isLoggedIn && currentDestination?.route in setOf(NavRoutes.DASHBOARD, NavRoutes.HALL_OF_FAME)
    val isAdmin = profile?.userRole == UserRole.ADMIN

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            navController.navigate(NavRoutes.DASHBOARD) {
                popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                launchSingleTop = true
            }
        } else {
            navController.navigate(NavRoutes.LOGIN) {
                popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    val items = listOf(
                        NavRoutes.DASHBOARD to Pair("Needs", Icons.Outlined.Home),
                        NavRoutes.HALL_OF_FAME to Pair("Hall of Fame", Icons.Outlined.Celebration)
                    )
                    items.forEach { (route, meta) ->
                        NavigationBarItem(
                            selected = currentDestination?.hierarchy?.any { it.route == route } == true,
                            onClick = {
                                navController.navigate(route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(meta.second, contentDescription = meta.first) },
                            label = { Text(meta.first) }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (showBottomBar && isAdmin && currentDestination?.route == NavRoutes.DASHBOARD) {
                FloatingActionButton(onClick = { navController.navigate(NavRoutes.ADD_NEED) }) {
                    Icon(Icons.Outlined.AddCircle, contentDescription = "Add Need")
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavRoutes.LOGIN,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavRoutes.LOGIN) {
                LoginScreen(
                    authViewModel = authViewModel,
                    onLoginSuccess = {
                        navController.navigate(NavRoutes.DASHBOARD) {
                            popUpTo(NavRoutes.LOGIN) { inclusive = true }
                        }
                    },
                    onNavigateSignup = { navController.navigate(NavRoutes.SIGNUP) },
                    snackbarHostState = snackbarHostState
                )
            }
            composable(NavRoutes.SIGNUP) {
                SignupScreen(
                    authViewModel = authViewModel,
                    onSignupSuccess = {
                        navController.navigate(NavRoutes.DASHBOARD) {
                            popUpTo(NavRoutes.LOGIN) { inclusive = true }
                        }
                    },
                    onNavigateLogin = { navController.popBackStack() },
                    snackbarHostState = snackbarHostState
                )
            }
            composable(NavRoutes.DASHBOARD) {
                DashboardScreen(
                    profile = profile,
                    onOpenNeed = { needId -> navController.navigate("${NavRoutes.DETAIL_BASE}/$needId") },
                    onOpenHallOfFame = { navController.navigate(NavRoutes.HALL_OF_FAME) },
                    onLogout = { authViewModel.logout() },
                    onEditNeed = { needId -> navController.navigate("${NavRoutes.EDIT_NEED_BASE}/$needId") }
                )
            }
            composable(
                route = NavRoutes.DETAIL,
                arguments = listOf(navArgument("needId") { defaultValue = "" })
            ) {
                NeedDetailScreen(
                    isAdmin = isAdmin,
                    onBack = { navController.popBackStack() },
                    onEdit = { needId -> navController.navigate("${NavRoutes.EDIT_NEED_BASE}/$needId") },
                    snackbarHostState = snackbarHostState
                )
            }
            composable(NavRoutes.HALL_OF_FAME) {
                HallOfFameScreen(onBack = { navController.popBackStack() })
            }
            composable(NavRoutes.ADD_NEED) {
                AdminNeedEditorScreen(
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() },
                    snackbarHostState = snackbarHostState
                )
            }
            composable(
                route = NavRoutes.EDIT_NEED,
                arguments = listOf(navArgument("needId") { defaultValue = "" })
            ) {
                AdminNeedEditorScreen(
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() },
                    snackbarHostState = snackbarHostState
                )
            }
        }
    }
}
