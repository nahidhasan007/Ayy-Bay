package com.ayybay.app.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.*
import androidx.navigation.compose.*
import com.ayybay.app.presentation.mvi.LinkUiIntent
import com.ayybay.app.presentation.mvi.TransactionUiIntent
import com.ayybay.app.presentation.screen.AddTransactionScreen
import com.ayybay.app.presentation.screen.BookListScreen
import com.ayybay.app.presentation.screen.BooksScreen
import com.ayybay.app.presentation.screen.DailyLinksScreen
import com.ayybay.app.presentation.screen.FinanceScreen
import com.ayybay.app.presentation.screen.HomeScreen
import com.ayybay.app.presentation.screen.JobsScreen
import com.ayybay.app.presentation.screen.LinkListScreen
import com.ayybay.app.presentation.screen.LinkWebViewScreen
import com.ayybay.app.presentation.screen.PrayerTimesScreen
import com.ayybay.app.presentation.viewmodel.LinkViewModel
import com.ayybay.app.presentation.viewmodel.PrayerViewModel
import com.ayybay.app.presentation.viewmodel.TransactionViewModel

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Finance : Screen("finance")
    object AddTransaction : Screen("add_transaction?transactionId={transactionId}") {
        fun createRoute(transactionId: Long = -1L) = "add_transaction?transactionId=$transactionId"
    }
    object PrayerTimes : Screen("prayer_times")
    object Jobs : Screen("jobs")
    object Books : Screen("books")
    object BookList : Screen("book_list/{religionId}") {
        fun createRoute(religionId: String) = "book_list/$religionId"
    }
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
        BottomNavItem(Screen.Finance.route, "Finance", Icons.Default.AccountBalanceWallet),
        BottomNavItem(Screen.Jobs.route, "Jobs", Icons.Default.Work),
        BottomNavItem(Screen.DailyLinks.route, "Websites", Icons.Default.Public),
        BottomNavItem(Screen.Books.route, "Books", Icons.AutoMirrored.Filled.MenuBook)
    )

    val showBottomBar = bottomNavItems.any { it.route == currentRoute }

    val transactionUiState by transactionViewModel.uiState.collectAsState()
    val prayerTimes by prayerViewModel.prayerTimes.collectAsState()
    val prayerSettings by prayerViewModel.prayerSettings.collectAsState()

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
                    prayerTimes = prayerTimes,
                    onTogglePrayerNotification = { name, enabled ->
                        prayerViewModel.togglePrayerNotification(name, enabled)
                    },
                    onNavigatePrayerTimes = { navController.navigate(Screen.PrayerTimes.route) },
                    onNavigateFinance = {
                        navController.navigate(Screen.Finance.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateJobs = {
                        navController.navigate(Screen.Jobs.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateWebsites = {
                        navController.navigate(Screen.DailyLinks.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateBooks = {
                        navController.navigate(Screen.Books.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            composable(Screen.Finance.route) {
                FinanceScreen(
                    uiState = transactionUiState,
                    onFilterByMonth = { month, year ->
                        transactionViewModel.handleIntent(TransactionUiIntent.FilterByMonth(month, year))
                    },
                    onAddTransaction = { navController.navigate(Screen.AddTransaction.createRoute()) },
                    onEditTransaction = { t -> navController.navigate(Screen.AddTransaction.createRoute(t.id)) },
                    onDeleteTransaction = { t ->
                        transactionViewModel.handleIntent(TransactionUiIntent.DeleteTransaction(t))
                    }
                )
            }

            composable(
                route = Screen.AddTransaction.route,
                arguments = listOf(navArgument("transactionId") { type = NavType.LongType; defaultValue = -1L })
            ) { backStackEntry ->
                val transactionId = backStackEntry.arguments?.getLong("transactionId") ?: -1L
                val existing = transactionUiState.transactions.find { it.id == transactionId }
                AddTransactionScreen(
                    transaction = existing,
                    onSave = { transaction ->
                        if (existing == null) {
                            transactionViewModel.handleIntent(TransactionUiIntent.AddTransaction(transaction))
                        } else {
                            transactionViewModel.handleIntent(TransactionUiIntent.UpdateTransaction(transaction))
                        }
                        navController.popBackStack()
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.PrayerTimes.route) {
                PrayerTimesScreen(
                    prayerTimes = prayerTimes,
                    prayerSettings = prayerSettings,
                    onTogglePrayerNotification = { name, enabled ->
                        prayerViewModel.togglePrayerNotification(name, enabled)
                    },
                    onUpdateSettings = { settings -> prayerViewModel.updatePrayerSettings(settings) },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Jobs.route) {
                JobsScreen()
            }

            composable(Screen.Books.route) {
                BooksScreen(onCategoryClick = { religionId -> navController.navigate(Screen.BookList.createRoute(religionId)) })
            }

            composable(
                route = Screen.BookList.route,
                arguments = listOf(navArgument("religionId") { type = NavType.StringType })
            ) { backStackEntry ->
                val religionId = backStackEntry.arguments?.getString("religionId") ?: return@composable
                BookListScreen(religionId = religionId, onBack = { navController.popBackStack() })
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
