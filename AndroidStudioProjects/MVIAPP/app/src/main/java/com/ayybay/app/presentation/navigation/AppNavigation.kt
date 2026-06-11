package com.ayybay.app.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.*
import androidx.navigation.compose.*
import com.ayybay.app.presentation.mvi.LinkUiIntent
import com.ayybay.app.presentation.mvi.TransactionUiIntent
import com.ayybay.app.presentation.screen.DailyLinksScreen
import com.ayybay.app.presentation.screen.HomeScreen
import com.ayybay.app.presentation.screen.LinkListScreen
import com.ayybay.app.presentation.screen.LinkWebViewScreen
import com.ayybay.app.presentation.viewmodel.LinkViewModel
import com.ayybay.app.presentation.viewmodel.PrayerViewModel
import com.ayybay.app.presentation.viewmodel.TransactionViewModel

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object DailyLinks : Screen("daily_links")
    object LinkList : Screen("link_list/{category}") {
        fun createRoute(category: String) = "link_list/$category"
    }
    object LinkWebView : Screen("link_webview/{linkId}") {
        fun createRoute(linkId: Long) = "link_webview/$linkId"
    }
}

private data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

@Composable
fun AppNavigation(
    transactionViewModel: TransactionViewModel,
    prayerViewModel: PrayerViewModel,
    linkViewModel: LinkViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavItems = listOf(
        BottomNavItem(Screen.Home.route, "Home", Icons.Default.Home),
        BottomNavItem(Screen.DailyLinks.route, "Daily Links", Icons.Default.Link)
    )

    val showBottomBar = currentRoute == Screen.Home.route || currentRoute == Screen.DailyLinks.route

    val transactionUiState by transactionViewModel.uiState.collectAsState()
    val prayerTimes by prayerViewModel.prayerTimes.collectAsState()

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    uiState = transactionUiState,
                    onAddTransaction = { t ->
                        transactionViewModel.handleIntent(TransactionUiIntent.AddTransaction(t))
                    },
                    onUpdateTransaction = { t ->
                        transactionViewModel.handleIntent(TransactionUiIntent.UpdateTransaction(t))
                    },
                    onDeleteTransaction = { t ->
                        transactionViewModel.handleIntent(TransactionUiIntent.DeleteTransaction(t))
                    },
                    prayerTimes = prayerTimes,
                    onTogglePrayerNotification = { name, enabled ->
                        prayerViewModel.togglePrayerNotification(name, enabled)
                    }
                )
            }

            composable(Screen.DailyLinks.route) {
                val linkUiState by linkViewModel.uiState.collectAsState()
                DailyLinksScreen(
                    uiState = linkUiState,
                    onCategoryClick = { category ->
                        navController.navigate(Screen.LinkList.createRoute(category))
                    }
                )
            }

            composable(
                route = Screen.LinkList.route,
                arguments = listOf(navArgument("category") { type = NavType.StringType })
            ) { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category") ?: return@composable
                val linkUiState by linkViewModel.uiState.collectAsState()

                LaunchedEffect(category) {
                    linkViewModel.handleIntent(LinkUiIntent.LoadLinksByCategory(category))
                }

                LinkListScreen(
                    category = category,
                    uiState = linkUiState,
                    onLinkClick = { link ->
                        navController.navigate(Screen.LinkWebView.createRoute(link.id))
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.LinkWebView.route,
                arguments = listOf(navArgument("linkId") { type = NavType.LongType })
            ) { backStackEntry ->
                val linkId = backStackEntry.arguments?.getLong("linkId") ?: return@composable
                LinkWebViewScreen(
                    linkId = linkId,
                    linkViewModel = linkViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}