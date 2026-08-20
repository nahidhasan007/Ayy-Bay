package com.ayybay.app.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ayybay.app.domain.model.Transaction
import com.ayybay.app.presentation.component.DonutChart
import com.ayybay.app.presentation.component.TransactionCard
import com.ayybay.app.presentation.language.tr
import com.ayybay.app.presentation.mvi.TransactionUiState
import com.ayybay.app.presentation.util.ExpenseCategory
import com.ayybay.app.presentation.util.expenseBucketOf
import com.ayybay.app.presentation.util.formatTaka
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceScreen(
    uiState: TransactionUiState,
    onFilterByMonth: (month: Int, year: Int) -> Unit,
    onAddTransaction: () -> Unit,
    onEditTransaction: (Transaction) -> Unit,
    onDeleteTransaction: (Transaction) -> Unit
) {
    var categoryFilter by remember { mutableStateOf<ExpenseCategory?>(null) }

    val monthTransactions = remember(uiState.transactions, uiState.selectedMonth, uiState.selectedYear) {
        val cal = Calendar.getInstance()
        uiState.transactions.filter {
            cal.time = it.date
            (cal.get(Calendar.MONTH) + 1) == uiState.selectedMonth && cal.get(Calendar.YEAR) == uiState.selectedYear
        }
    }

    val (income, expense, savings) = remember(monthTransactions, uiState.selectedMonth, uiState.selectedYear) {
        monthlyTotals(monthTransactions, uiState.selectedMonth, uiState.selectedYear)
    }

    val expenseByCategory = remember(monthTransactions) {
        monthTransactions.filter { it.type == com.ayybay.app.domain.model.TransactionType.EXPENSE }
            .groupBy { expenseBucketOf(it.category) }
            .mapValues { (_, list) -> list.sumOf { it.amount } }
    }

    val visibleTransactions = remember(monthTransactions, categoryFilter) {
        if (categoryFilter == null) monthTransactions
        else monthTransactions.filter {
            it.type == com.ayybay.app.domain.model.TransactionType.EXPENSE && expenseBucketOf(it.category) == categoryFilter
        }
    }.sortedByDescending { it.date }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = tr("My Finance", "আমার হিসাব"), fontWeight = FontWeight.Bold)
                        Text(text = tr("Track your income & expenses", "আপনার আয়-ব্যয়ের হিসাব রাখুন"), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {}) { Icon(Icons.Default.Menu, contentDescription = tr("Menu", "মেনু")) }
                },
                actions = {
                    IconButton(onClick = {}) { Icon(Icons.Default.FilterList, contentDescription = tr("Filter", "ফিল্টার")) }
                    IconButton(onClick = onAddTransaction) { Icon(Icons.Default.Add, contentDescription = tr("Add Transaction", "লেনদেন যোগ করুন")) }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTransaction, containerColor = MaterialTheme.colorScheme.primary) {
                Icon(Icons.Default.Add, contentDescription = tr("Add Transaction", "লেনদেন যোগ করুন"))
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                MonthSelector(
                    month = uiState.selectedMonth,
                    year = uiState.selectedYear,
                    onSelect = onFilterByMonth
                )
            }

            item { TotalBalanceCard(income = income, expense = expense, savings = savings) }

            if (expense > 0) {
                item { MonthlySpendingCard(expenseByCategory = expenseByCategory, totalExpense = expense) }
            }

            item {
                ExpenseCategoriesGrid(
                    selected = categoryFilter,
                    onSelect = { categoryFilter = if (categoryFilter == it) null else it }
                )
            }

            item {
                val recentTransactionsLabel = tr("Recent Transactions", "সাম্প্রতিক লেনদেন")
                Text(
                    text = if (categoryFilter == null) recentTransactionsLabel else "$recentTransactionsLabel · ${categoryFilter!!.label}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (visibleTransactions.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = tr("No transactions for this month yet", "এই মাসে এখনো কোনো লেনদেন নেই"),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(visibleTransactions, key = { it.id }) { transaction ->
                    TransactionCard(
                        transaction = transaction,
                        onEdit = onEditTransaction,
                        onDelete = onDeleteTransaction
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(72.dp)) }
        }
    }
}

@Composable
private fun MonthSelector(month: Int, year: Int, onSelect: (Int, Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val label = remember(month, year) {
        val cal = Calendar.getInstance()
        cal.set(Calendar.MONTH, month - 1)
        cal.set(Calendar.YEAR, year)
        SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(cal.time)
    }
    val options = remember {
        val cal = Calendar.getInstance()
        (0 until 12).map {
            val m = cal.get(Calendar.MONTH) + 1
            val y = cal.get(Calendar.YEAR)
            val display = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(cal.time)
            cal.add(Calendar.MONTH, -1)
            Triple(m, y, display)
        }
    }

    Box {
        OutlinedButton(onClick = { expanded = true }, shape = RoundedCornerShape(50)) {
            Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(label)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { (m, y, display) ->
                DropdownMenuItem(text = { Text(display) }, onClick = { onSelect(m, y); expanded = false })
            }
        }
    }
}

@Composable
private fun TotalBalanceCard(income: Double, expense: Double, savings: Double) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = tr("Total Balance", "মোট ব্যালেন্স"), color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.85f))
                Text(
                    text = formatTaka(savings),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = androidx.compose.ui.graphics.Color.White
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    BalanceStat(label = tr("Income", "আয়"), amount = income)
                    BalanceStat(label = tr("Expense", "ব্যয়"), amount = expense)
                    BalanceStat(label = tr("Savings", "সঞ্চয়"), amount = savings)
                }
            }
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(androidx.compose.ui.graphics.Color.White.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = androidx.compose.ui.graphics.Color.White, modifier = Modifier.size(28.dp))
            }
        }
    }
}

@Composable
private fun BalanceStat(label: String, amount: Double) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.75f))
        Text(text = formatTaka(amount), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = androidx.compose.ui.graphics.Color.White)
    }
}

@Composable
private fun MonthlySpendingCard(expenseByCategory: Map<ExpenseCategory, Double>, totalExpense: Double) {
    Card(shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = tr("Monthly Spending", "মাসিক খরচ"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(120.dp), contentAlignment = Alignment.Center) {
                    val slices = ExpenseCategory.entries.mapNotNull { cat ->
                        val amount = expenseByCategory[cat] ?: 0.0
                        if (amount <= 0) null else cat.color to (amount / totalExpense).toFloat()
                    }
                    DonutChart(slices = slices, modifier = Modifier.fillMaxSize())
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = formatTaka(totalExpense), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text(text = tr("Total", "মোট"), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    ExpenseCategory.entries.forEach { cat ->
                        val amount = expenseByCategory[cat] ?: 0.0
                        if (amount > 0) {
                            val percent = (amount / totalExpense * 100).toInt()
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(9.dp).clip(CircleShape).background(cat.color))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = cat.label, style = MaterialTheme.typography.bodySmall)
                                }
                                Text(text = "$percent%", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpenseCategoriesGrid(selected: ExpenseCategory?, onSelect: (ExpenseCategory) -> Unit) {
    Column {
        Text(text = tr("Expense Categories", "খরচের ধরন"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))
        val rows = ExpenseCategory.entries.chunked(4)
        rows.forEach { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { cat ->
                    val isSelected = selected == cat
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) cat.color.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface)
                            .clickable { onSelect(cat) }
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(cat.color.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(cat.icon, contentDescription = cat.label, tint = cat.color, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = cat.label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Medium)
                    }
                }
                repeat(4 - row.size) { Spacer(modifier = Modifier.weight(1f)) }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
