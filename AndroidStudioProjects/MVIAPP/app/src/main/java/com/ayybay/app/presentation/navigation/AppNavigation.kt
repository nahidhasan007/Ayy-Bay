package com.ayybay.app.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.*
import androidx.navigation.compose.*
import com.ayybay.app.presentation.language.tr
import com.ayybay.app.presentation.mvi.AuthUiEffect
import com.ayybay.app.presentation.mvi.AuthUiIntent
import com.ayybay.app.presentation.mvi.LinkUiIntent
import com.ayybay.app.presentation.mvi.NoteUiIntent
import com.ayybay.app.presentation.mvi.TrackerUiIntent
import com.ayybay.app.presentation.mvi.TransactionUiIntent
import com.ayybay.app.presentation.screen.AddNoteScreen
import com.ayybay.app.presentation.screen.AddTransactionScreen
import com.ayybay.app.presentation.screen.AgeCalculatorScreen
import com.ayybay.app.presentation.screen.BmiCalculatorScreen
import com.ayybay.app.presentation.screen.BookListScreen
import com.ayybay.app.presentation.screen.BooksScreen
import com.ayybay.app.presentation.screen.DailyLinksScreen
import com.ayybay.app.presentation.screen.FinanceScreen
import com.ayybay.app.presentation.screen.FitnessAdviceScreen
import com.ayybay.app.presentation.screen.HomeScreen
import com.ayybay.app.presentation.screen.JobsScreen
import com.ayybay.app.presentation.screen.LinkListScreen
import com.ayybay.app.presentation.screen.LinkWebViewScreen
import com.ayybay.app.presentation.screen.LoginScreen
import com.ayybay.app.presentation.screen.MoreScreen
import com.ayybay.app.presentation.screen.NotesScreen
import com.ayybay.app.presentation.screen.PrayerTimesScreen
import com.ayybay.app.presentation.screen.ProfileScreen
import com.ayybay.app.presentation.screen.QuranProgressScreen
import com.ayybay.app.presentation.screen.SalahTrackerScreen
import com.ayybay.app.presentation.screen.SignUpScreen
import com.ayybay.app.presentation.screen.SurahListScreen
import com.ayybay.app.presentation.screen.SurahWebViewScreen
import com.ayybay.app.data.local.QuranSurahData
import com.ayybay.app.presentation.viewmodel.AuthViewModel
import com.ayybay.app.presentation.viewmodel.HealthViewModel
import com.ayybay.app.presentation.viewmodel.LinkViewModel
import com.ayybay.app.presentation.viewmodel.NoteViewModel
import com.ayybay.app.presentation.viewmodel.PrayerViewModel
import com.ayybay.app.presentation.viewmodel.TrackerViewModel
import com.ayybay.app.presentation.viewmodel.TransactionViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object SignUp : Screen("sign_up")
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
    object SurahList : Screen("surah_list")
    object SurahWebView : Screen("surah_webview/{surahNumber}") {
        fun createRoute(surahNumber: Int) = "surah_webview/$surahNumber"
    }
    object DailyLinks : Screen("daily_links")
    object LinkList : Screen("link_list/{category}") {
        fun createRoute(category: String) = "link_list/$category"
    }
    object LinkWebView : Screen("link_webview/{linkId}") {
        fun createRoute(linkId: Long) = "link_webview/$linkId"
    }
    object More : Screen("more")
    object Profile : Screen("profile")
    object Notes : Screen("notes")
    object AddNote : Screen("add_note?noteId={noteId}") {
        fun createRoute(noteId: Long = -1L) = "add_note?noteId=$noteId"
    }
    object SalahTracker : Screen("salah_tracker")
    object QuranProgress : Screen("quran_progress")
    object AgeCalculator : Screen("age_calculator")
    object BmiCalculator : Screen("bmi_calculator")
    object FitnessAdvice : Screen("fitness_advice")
}

private data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel,
    transactionViewModel: TransactionViewModel,
    prayerViewModel: PrayerViewModel,
    linkViewModel: LinkViewModel,
    noteViewModel: NoteViewModel,
    trackerViewModel: TrackerViewModel,
    healthViewModel: HealthViewModel
) {
    val authUiState by authViewModel.uiState.collectAsState()

    if (authUiState.isCheckingSession) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    LaunchedEffect(Unit) {
        authViewModel.uiEffect.collect { effect ->
            when (effect) {
                is AuthUiEffect.NavigateToHome -> navController.navigate(Screen.Home.route) {
                    popUpTo(navController.graph.id) { inclusive = true }
                    launchSingleTop = true
                }
                is AuthUiEffect.NavigateToLogin -> navController.navigate(Screen.Login.route) {
                    popUpTo(navController.graph.id) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }

    val bottomNavItems = listOf(
        BottomNavItem(Screen.Home.route, tr("Home", "হোম"), Icons.Default.Home),
        BottomNavItem(Screen.Finance.route, tr("Finance", "আর্থিক"), Icons.Default.AccountBalanceWallet),
        BottomNavItem(Screen.Jobs.route, tr("Jobs", "চাকরি"), Icons.Default.Work),
        BottomNavItem(Screen.Books.route, tr("Books", "বই"), Icons.AutoMirrored.Filled.MenuBook),
        BottomNavItem(Screen.More.route, tr("More", "আরও"), Icons.Default.Apps)
    )

    val showBottomBar = bottomNavItems.any { it.route == currentRoute }

    val transactionUiState by transactionViewModel.uiState.collectAsState()
    val prayerTimes by prayerViewModel.prayerTimes.collectAsState()
    val prayerSettings by prayerViewModel.prayerSettings.collectAsState()
    val noteUiState by noteViewModel.uiState.collectAsState()
    val trackerUiState by trackerViewModel.uiState.collectAsState()
    val healthUiState by healthViewModel.uiState.collectAsState()

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
            startDestination = Screen.Home.route,/* if (authUiState.isLoggedIn) Screen.Home.route else Screen.Login.route*/
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Login.route) {
                val context = LocalContext.current
                LoginScreen(
                    isSigningIn = authUiState.isSigningIn,
                    error = authUiState.error,
                    onGoogleSignIn = { authViewModel.handleIntent(AuthUiIntent.SignInWithGoogle(context)) },
                    onNavigateToSignUp = { navController.navigate(Screen.SignUp.route) },
                    onDismissError = { authViewModel.handleIntent(AuthUiIntent.ClearError) }
                )
            }

            composable(Screen.SignUp.route) {
                val context = LocalContext.current
                SignUpScreen(
                    isSigningIn = authUiState.isSigningIn,
                    error = authUiState.error,
                    onGoogleSignUp = { authViewModel.handleIntent(AuthUiIntent.SignInWithGoogle(context)) },
                    onNavigateToLogin = { navController.popBackStack() },
                    onDismissError = { authViewModel.handleIntent(AuthUiIntent.ClearError) }
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    uiState = transactionUiState,
                    userName = authUiState.user?.displayName ?: tr("Guest", "অতিথি"),
                    userEmail = authUiState.user?.email,
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
                    },
                    onNavigateNotes = { navController.navigate(Screen.Notes.route) },
                    onNavigateSalahTracker = { navController.navigate(Screen.SalahTracker.route) },
                    onNavigateBmiCalculator = { navController.navigate(Screen.BmiCalculator.route) },
                    onNavigateProfile = { navController.navigate(Screen.Profile.route) },
                    onSignOut = { authViewModel.handleIntent(AuthUiIntent.SignOut) }
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    userName = authUiState.user?.displayName ?: tr("Guest", "অতিথি"),
                    userEmail = authUiState.user?.email,
                    onBack = { navController.popBackStack() },
                    onSignOut = { authViewModel.handleIntent(AuthUiIntent.SignOut) }
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
                BookListScreen(
                    religionId = religionId,
                    onOpenQuran = { navController.navigate(Screen.SurahList.route) },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.SurahList.route) {
                val completedNumbers = trackerUiState.quranProgress.filter { it.isCompleted }.map { it.surahNumber }.toSet()
                SurahListScreen(
                    completedNumbers = completedNumbers,
                    onToggleComplete = { surahNumber, completed ->
                        trackerViewModel.handleIntent(TrackerUiIntent.ToggleSurahComplete(surahNumber, completed))
                    },
                    onSurahClick = { surah -> navController.navigate(Screen.SurahWebView.createRoute(surah.number)) },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.SurahWebView.route,
                arguments = listOf(navArgument("surahNumber") { type = NavType.IntType })
            ) { backStackEntry ->
                val surahNumber = backStackEntry.arguments?.getInt("surahNumber") ?: return@composable
                val surah = QuranSurahData.surahs().find { it.number == surahNumber } ?: return@composable
                LaunchedEffect(surahNumber) {
                    trackerViewModel.handleIntent(TrackerUiIntent.MarkSurahOpened(surahNumber))
                }
                SurahWebViewScreen(
                    url = surah.url,
                    title = "${surah.number}. ${tr(surah.name, surah.nameBn)}",
                    onBack = { navController.popBackStack() }
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

            composable(Screen.More.route) {
                MoreScreen(
                    onNavigateNotes = { navController.navigate(Screen.Notes.route) },
                    onNavigateSalahTracker = { navController.navigate(Screen.SalahTracker.route) },
                    onNavigateQuranProgress = { navController.navigate(Screen.QuranProgress.route) },
                    onNavigateAgeCalculator = { navController.navigate(Screen.AgeCalculator.route) },
                    onNavigateBmiCalculator = { navController.navigate(Screen.BmiCalculator.route) },
                    onNavigateFitnessAdvice = { navController.navigate(Screen.FitnessAdvice.route) },
                    onNavigateWebsites = { navController.navigate(Screen.DailyLinks.route) }
                )
            }

            composable(Screen.Notes.route) {
                NotesScreen(
                    notes = noteUiState.visibleNotes,
                    searchQuery = noteUiState.searchQuery,
                    onSearchChange = { query -> noteViewModel.handleIntent(NoteUiIntent.Search(query)) },
                    onAddNote = { navController.navigate(Screen.AddNote.createRoute()) },
                    onEditNote = { note -> navController.navigate(Screen.AddNote.createRoute(note.id)) },
                    onDeleteNote = { note -> noteViewModel.handleIntent(NoteUiIntent.DeleteNote(note)) },
                    onTogglePin = { note -> noteViewModel.handleIntent(NoteUiIntent.TogglePin(note)) },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.AddNote.route,
                arguments = listOf(navArgument("noteId") { type = NavType.LongType; defaultValue = -1L })
            ) { backStackEntry ->
                val noteId = backStackEntry.arguments?.getLong("noteId") ?: -1L
                val existing = noteUiState.allNotes.find { it.id == noteId }
                AddNoteScreen(
                    note = existing,
                    onSave = { note -> noteViewModel.handleIntent(NoteUiIntent.SaveNote(note)) },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.SalahTracker.route) {
                SalahTrackerScreen(
                    prayerTimes = prayerTimes,
                    todayPrayerLogs = trackerUiState.todayPrayerLogs,
                    weeklyProgress = trackerUiState.weeklyPrayerProgress,
                    onTogglePrayer = { prayerName, isPrayed ->
                        trackerViewModel.handleIntent(TrackerUiIntent.TogglePrayer(prayerName, isPrayed))
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.QuranProgress.route) {
                QuranProgressScreen(
                    completedCount = trackerUiState.quranCompletedCount,
                    totalSurahs = QuranSurahData.surahs().size,
                    streakDays = trackerUiState.quranStreak,
                    weeklyReading = trackerUiState.quranWeeklyReading,
                    onOpenSurahList = { navController.navigate(Screen.SurahList.route) },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.AgeCalculator.route) {
                AgeCalculatorScreen(
                    dateOfBirth = healthUiState.dateOfBirth,
                    onSetDateOfBirth = { millis -> healthViewModel.setDateOfBirth(millis) },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.BmiCalculator.route) {
                BmiCalculatorScreen(
                    initialHeightCm = healthUiState.heightCm,
                    initialWeightKg = healthUiState.weightKg,
                    onSave = { heightCm, weightKg ->
                        healthViewModel.setHeightCm(heightCm)
                        healthViewModel.setWeightKg(weightKg)
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.FitnessAdvice.route) {
                FitnessAdviceScreen(
                    dateOfBirth = healthUiState.dateOfBirth,
                    heightCm = healthUiState.heightCm,
                    weightKg = healthUiState.weightKg,
                    onNavigateAge = { navController.navigate(Screen.AgeCalculator.route) },
                    onNavigateBmi = { navController.navigate(Screen.BmiCalculator.route) },
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
