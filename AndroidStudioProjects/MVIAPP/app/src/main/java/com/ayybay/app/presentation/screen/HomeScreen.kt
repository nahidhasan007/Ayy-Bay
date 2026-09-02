package com.ayybay.app.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ayybay.app.domain.model.PrayerName
import com.ayybay.app.domain.model.PrayerTime
import com.ayybay.app.domain.model.Transaction
import com.ayybay.app.domain.model.TransactionType
import com.ayybay.app.presentation.component.LanguageToggle
import com.ayybay.app.presentation.language.AppLanguage
import com.ayybay.app.presentation.language.LocalAppLanguage
import com.ayybay.app.presentation.language.label
import com.ayybay.app.presentation.language.tr
import com.ayybay.app.presentation.mvi.TransactionUiState
import com.ayybay.app.presentation.util.banglaWeekday
import com.ayybay.app.presentation.util.formatBanglaDate
import com.ayybay.app.presentation.util.formatCountdown
import com.ayybay.app.presentation.util.formatTaka
import com.ayybay.app.presentation.util.nextPrayerOf
import com.ayybay.app.presentation.util.rememberTickingNow
import com.ayybay.app.service.AdhanForegroundService
import com.ayybay.app.ui.theme.BalanceOrange
import com.ayybay.app.ui.theme.BalanceOrangeTint
import com.ayybay.app.ui.theme.ExpenseRed
import com.ayybay.app.ui.theme.ExpenseRedTint
import com.ayybay.app.ui.theme.IncomeGreen
import com.ayybay.app.ui.theme.IncomeGreenTint
import com.ayybay.app.ui.theme.IslamicGoldLight
import com.ayybay.app.ui.theme.IslamicGreen
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    uiState: TransactionUiState,
    userName: String = "Guest",
    userEmail: String? = null,
    prayerTimes: List<PrayerTime> = emptyList(),
    onTogglePrayerNotification: (PrayerName, Boolean) -> Unit = { _, _ -> },
    unreadNotificationCount: Int = 0,
    onNavigateNotifications: () -> Unit = {},
    onNavigatePrayerTimes: () -> Unit = {},
    onNavigateFinance: () -> Unit = {},
    onNavigateJobs: () -> Unit = {},
    onNavigateWebsites: () -> Unit = {},
    onNavigateBooks: () -> Unit = {},
    onNavigateNotes: () -> Unit = {},
    onNavigateSalahTracker: () -> Unit = {},
    onNavigateBmiCalculator: () -> Unit = {},
    onNavigateProfile: () -> Unit = {},
    onSignOut: () -> Unit = {}
) {
    Scaffold(modifier = modifier) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                HomeHeader(
                    userName = userName,
                    userEmail = userEmail,
                    unreadNotificationCount = unreadNotificationCount,
                    onNavigateNotifications = onNavigateNotifications,
                    onNavigateProfile = onNavigateProfile,
                    onSignOut = onSignOut
                )
            }

            item {
                NextPrayerCard(
                    prayerTimes = prayerTimes,
                    onToggleNotification = onTogglePrayerNotification,
                    onClick = onNavigatePrayerTimes
                )
            }

            item {
                QuickAccessSection(
                    onNavigateFinance = onNavigateFinance,
                    onNavigateJobs = onNavigateJobs,
                    onNavigateWebsites = onNavigateWebsites,
                    onNavigateBooks = onNavigateBooks,
                    onNavigateNotes = onNavigateNotes,
                    onNavigateSalahTracker = onNavigateSalahTracker,
                    onNavigateBmiCalculator = onNavigateBmiCalculator
                )
            }

            item { TodaysOverviewSection(uiState = uiState, onViewAll = onNavigateFinance) }
        }
    }
}

@Composable
private fun HomeHeader(
    userName: String,
    userEmail: String?,
    unreadNotificationCount: Int,
    onNavigateNotifications: () -> Unit,
    onNavigateProfile: () -> Unit,
    onSignOut: () -> Unit
) {
    val today = remember { Date() }
    val language = LocalAppLanguage.current
    val englishDateFormat = remember { SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()) }
    val englishWeekdayFormat = remember { SimpleDateFormat("EEEE", Locale.getDefault()) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = tr("Assalamu Alaikum", "আসসালামু আলাইকুম"),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = userName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (language == AppLanguage.EN) englishDateFormat.format(today) else formatBanglaDate(today),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = if (language == AppLanguage.EN) englishWeekdayFormat.format(today) else banglaWeekday(today),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box {
                IconButton(onClick = onNavigateNotifications) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = tr("Notifications", "নোটিফিকেশন"),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                if (unreadNotificationCount > 0) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .align(Alignment.TopEnd)
                            .offset(x = (-8).dp, y = 8.dp)
                            .clip(CircleShape)
                            .background(ExpenseRed)
                    )
                }
            }
            Spacer(modifier = Modifier.width(4.dp))
            LanguageToggle()
            Spacer(modifier = Modifier.width(4.dp))
            AccountAvatarMenu(
                userName = userName,
                userEmail = userEmail,
                onNavigateProfile = onNavigateProfile,
                onSignOut = onSignOut
            )
        }
    }
}

@Composable
private fun AccountAvatarMenu(
    userName: String,
    userEmail: String?,
    onNavigateProfile: () -> Unit,
    onSignOut: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(IslamicGreen)
                .clickable { expanded = true },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = userName.firstOrNull()?.uppercase() ?: "U",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            Column(
                modifier = Modifier
                    .clickable {
                        expanded = false
                        onNavigateProfile()
                    }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(text = userName, fontWeight = FontWeight.Bold)
                if (userEmail != null) {
                    Text(
                        text = userEmail,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            HorizontalDivider()
            DropdownMenuItem(
                text = { Text(tr("Profile", "প্রোফাইল")) },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                onClick = {
                    expanded = false
                    onNavigateProfile()
                }
            )
            DropdownMenuItem(
                text = { Text(tr("Sign out", "সাইন আউট")) },
                leadingIcon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null) },
                onClick = {
                    expanded = false
                    onSignOut()
                }
            )
        }
    }
}

@Composable
private fun NextPrayerCard(
    prayerTimes: List<PrayerTime>,
    onToggleNotification: (PrayerName, Boolean) -> Unit,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val now by rememberTickingNow()
    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val sortedPrayers = remember(prayerTimes) { prayerTimes.sortedBy { it.prayerName.ordinal } }
    val nextPrayer = remember(prayerTimes, now) { nextPrayerOf(prayerTimes, now) }
    val allEnabled = prayerTimes.isNotEmpty() && prayerTimes.all { it.isEnabled }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = tr("Next Prayer", "পরবর্তী নামাজ"),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        text = nextPrayer?.first?.prayerName?.label() ?: "-",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = IslamicGoldLight
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = tr("Play Adhan", "আজান বাজান"),
                    tint = Color.White,
                    modifier = Modifier
                        .size(26.dp)
                        .clickable {
                            val label = nextPrayer?.first?.prayerName?.displayName ?: "Prayer"
                            AdhanForegroundService.startAdhan(context, label, 90)
                        }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = IslamicGoldLight,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = nextPrayer?.let { formatCountdown(it.second.time - now.time) } ?: "--:--:--",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = IslamicGoldLight
                )
            }
            Text(
                text = tr("remaining", "বাকি আছে"),
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = tr("Dhaka, Bangladesh", "ঢাকা, বাংলাদেশ"),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = tr("Adhan Notifications", "আজানের নোটিফিকেশন"),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Switch(
                        checked = allEnabled,
                        onCheckedChange = { enabled ->
                            prayerTimes.forEach { onToggleNotification(it.prayerName, enabled) }
                        },
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = IslamicGoldLight,
                            checkedThumbColor = Color.White
                        ),
                        modifier = Modifier.height(20.dp)
                    )
                }
            }

            if (sortedPrayers.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White.copy(alpha = 0.12f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp, horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        sortedPrayers.forEach { prayer ->
                            val isNext = prayer.prayerName == nextPrayer?.first?.prayerName
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = prayer.prayerName.label(),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isNext) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isNext) IslamicGoldLight else Color.White.copy(alpha = 0.75f)
                                )
                                Text(
                                    text = timeFormat.format(prayer.time),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isNext) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isNext) IslamicGoldLight else Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class QuickAccessItem(
    val labelEn: String,
    val labelBn: String,
    val icon: ImageVector,
    val color: Color,
    val onClick: () -> Unit
)

@Composable
private fun QuickAccessSection(
    onNavigateFinance: () -> Unit,
    onNavigateJobs: () -> Unit,
    onNavigateWebsites: () -> Unit,
    onNavigateBooks: () -> Unit,
    onNavigateNotes: () -> Unit,
    onNavigateSalahTracker: () -> Unit,
    onNavigateBmiCalculator: () -> Unit
) {
    Column {
        Text(
            text = tr("Quick Access", "দ্রুত ব্যবহার"),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))

        val firstRow = listOf(
            QuickAccessItem("Expense Tracker", "খরচ ট্র্যাকার", Icons.Default.AccountBalanceWallet, IncomeGreen, onNavigateFinance),
            QuickAccessItem("Govt Jobs", "সরকারি চাকরি", Icons.Default.Work, IncomeGreen, onNavigateJobs),
            QuickAccessItem("Useful Websites", "গুরুত্বপূর্ণ ওয়েবসাইট", Icons.Default.Public, Color(0xFF1976D2), onNavigateWebsites),
            QuickAccessItem("Religious Books", "ধর্মীয় গ্রন্থ", Icons.AutoMirrored.Filled.MenuBook, IncomeGreen, onNavigateBooks)
        )
        val secondRow = listOf(
            QuickAccessItem("Notes", "নোট", Icons.Default.Description, IncomeGreen, onNavigateNotes),
            QuickAccessItem("Salah Tracker", "নামাজ ট্র্যাকার", Icons.Default.Mosque, IncomeGreen, onNavigateSalahTracker),
            QuickAccessItem("BMI Calculator", "বিএমআই ক্যালকুলেটর", Icons.Default.MonitorWeight, BalanceOrange, onNavigateBmiCalculator)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            firstRow.forEach { item ->
                QuickAccessTile(item = item, modifier = Modifier.weight(1f))
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            secondRow.forEach { item ->
                QuickAccessTile(item = item, modifier = Modifier.weight(1f))
            }
            repeat(firstRow.size - secondRow.size) { Spacer(modifier = Modifier.weight(1f)) }
        }
    }
}

@Composable
private fun QuickAccessTile(item: QuickAccessItem, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = item.onClick)
            .padding(vertical = 14.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(item.color.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = item.icon, contentDescription = tr(item.labelEn, item.labelBn), tint = item.color, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = tr(item.labelEn, item.labelBn),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            minLines = 2,
            maxLines = 2
        )
    }
}

@Composable
private fun TodaysOverviewSection(
    uiState: TransactionUiState,
    onViewAll: () -> Unit
) {
    val (income, expense, balance) = remember(uiState.transactions, uiState.selectedMonth, uiState.selectedYear) {
        monthlyTotals(uiState.transactions, uiState.selectedMonth, uiState.selectedYear)
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = tr("Today's Overview", "আজকের সারাংশ"),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = tr("View All  >", "সব দেখুন  >"),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable(onClick = onViewAll)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatTile(
                titleEn = "Income",
                titleBn = "আয়",
                amount = income,
                trailingIcon = Icons.Default.ArrowUpward,
                icon = Icons.Default.AccountBalanceWallet,
                color = IncomeGreen,
                background = IncomeGreenTint,
                modifier = Modifier.weight(1f)
            )
            StatTile(
                titleEn = "Expense",
                titleBn = "খরচ",
                amount = expense,
                trailingIcon = Icons.Default.ArrowDownward,
                icon = Icons.Default.AccountBalanceWallet,
                color = ExpenseRed,
                background = ExpenseRedTint,
                modifier = Modifier.weight(1f)
            )
            StatTile(
                titleEn = "Balance",
                titleBn = "ব্যালেন্স",
                amount = balance,
                trailingIcon = Icons.Default.Star,
                icon = Icons.Default.AccountBalanceWallet,
                color = BalanceOrange,
                background = BalanceOrangeTint,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatTile(
    titleEn: String,
    titleBn: String,
    amount: Double,
    trailingIcon: ImageVector,
    icon: ImageVector,
    color: Color,
    background: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(background)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = tr(titleEn, titleBn),
                style = MaterialTheme.typography.labelSmall,
                color = color,
                maxLines = 1
            )
            Icon(imageVector = trailingIcon, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = formatTaka(amount),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = tr("This month", "এই মাসে"),
                style = MaterialTheme.typography.labelSmall,
                color = color.copy(alpha = 0.7f)
            )
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(12.dp))
            }
        }
    }
}

fun monthlyTotals(transactions: List<Transaction>, month: Int, year: Int): Triple<Double, Double, Double> {
    val cal = Calendar.getInstance()
    val filtered = transactions.filter {
        cal.time = it.date
        (cal.get(Calendar.MONTH) + 1) == month && cal.get(Calendar.YEAR) == year
    }
    val income = filtered.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
    val expense = filtered.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
    return Triple(income, expense, income - expense)
}
