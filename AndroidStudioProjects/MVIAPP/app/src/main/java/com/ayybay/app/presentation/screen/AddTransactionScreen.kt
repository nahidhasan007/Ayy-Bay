package com.ayybay.app.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ayybay.app.domain.model.Transaction
import com.ayybay.app.domain.model.TransactionType
import com.ayybay.app.presentation.component.AppTopBar
import com.ayybay.app.presentation.component.LanguageToggle
import com.ayybay.app.presentation.language.tr
import com.ayybay.app.presentation.util.ExpenseCategory
import com.ayybay.app.presentation.util.IncomeCategory
import com.ayybay.app.ui.theme.ExpenseRed
import com.ayybay.app.ui.theme.IncomeGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val paymentMethods = listOf("Cash", "bKash", "Nagad", "Rocket", "Card", "Bank Transfer")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    transaction: Transaction?,
    onSave: (Transaction) -> Unit,
    onBack: () -> Unit
) {
    var type by remember { mutableStateOf(transaction?.type ?: TransactionType.EXPENSE) }
    var amount by remember { mutableStateOf(transaction?.amount?.let { if (it == 0.0) "" else it.toString() } ?: "") }
    var selectedExpenseCategory by remember {
        mutableStateOf(
            transaction?.category?.let { cat -> ExpenseCategory.entries.find { it.label.equals(cat, ignoreCase = true) } }
                ?: ExpenseCategory.FOOD
        )
    }
    var selectedIncomeCategory by remember {
        mutableStateOf(
            transaction?.category?.let { cat -> IncomeCategory.entries.find { it.label.equals(cat, ignoreCase = true) } }
                ?: IncomeCategory.SALARY
        )
    }
    var description by remember { mutableStateOf(transaction?.description ?: "") }
    var date by remember { mutableStateOf(transaction?.date ?: Date()) }
    var paymentMethod by remember { mutableStateOf(transaction?.paymentMethod ?: "Cash") }
    var note by remember { mutableStateOf(transaction?.note ?: "") }
    var showDatePicker by remember { mutableStateOf(false) }
    var paymentMenuExpanded by remember { mutableStateOf(false) }

    val dateFormat = remember { SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()) }
    val weekdayFormat = remember { SimpleDateFormat("EEEE", Locale.getDefault()) }

    val isEditing = transaction != null
    val categoryLabel = if (type == TransactionType.EXPENSE) selectedExpenseCategory.label else selectedIncomeCategory.label
    val canSave = amount.toDoubleOrNull()?.let { it > 0 } == true && description.isNotBlank()

    Scaffold(
        topBar = {
            AppTopBar(
                title = { Text(text = if (isEditing) tr("Edit Transaction", "লেনদেন সম্পাদনা") else tr("Add Transaction", "লেনদেন যোগ করুন"), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = tr("Back", "পেছনে")) }
                },
                actions = {
                    LanguageToggle(
                        modifier = Modifier.padding(end = 12.dp),
                        selectedColor = MaterialTheme.colorScheme.onPrimary,
                        unselectedColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                        borderColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.4f),
                        dividerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Expense / Income toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    .padding(4.dp)
            ) {
                TypeToggleButton(
                    label = tr("Expense", "খরচ"),
                    icon = Icons.Default.ArrowDownward,
                    selected = type == TransactionType.EXPENSE,
                    color = ExpenseRed,
                    onClick = { type = TransactionType.EXPENSE },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                TypeToggleButton(
                    label = tr("Income", "আয়"),
                    icon = Icons.Default.ArrowUpward,
                    selected = type == TransactionType.INCOME,
                    color = IncomeGreen,
                    onClick = { type = TransactionType.INCOME },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = tr("Amount", "টাকার পরিমাণ"), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = amount,
                onValueChange = { input -> amount = input.filter { it.isDigit() || it == '.' } },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                leadingIcon = { Text(text = "৳", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) },
                trailingIcon = {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Calculate, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = tr("Category", "ধরন"), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(10.dp))

            if (type == TransactionType.EXPENSE) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(ExpenseCategory.entries) { cat ->
                        CategoryTile(
                            label = cat.label,
                            labelBn = cat.labelBn,
                            icon = cat.icon,
                            color = cat.color,
                            selected = selectedExpenseCategory == cat,
                            onClick = { selectedExpenseCategory = cat }
                        )
                    }
                }
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(IncomeCategory.entries) { cat ->
                        CategoryTile(
                            label = cat.label,
                            labelBn = cat.labelBn,
                            icon = cat.icon,
                            color = cat.color,
                            selected = selectedIncomeCategory == cat,
                            onClick = { selectedIncomeCategory = cat }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = tr("Description", "বিবরণ"), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) },
                placeholder = { Text(tr("e.g. Electricity Bill", "যেমন: বিদ্যুৎ বিল")) },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = tr("Date", "তারিখ"), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker = true }
                    ) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(text = dateFormat.format(date), fontWeight = FontWeight.Medium, fontSize = 13.sp)
                                Text(text = weekdayFormat.format(date), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = tr("Payment Method", "পরিশোধের মাধ্যম"), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold, maxLines = 1)
                    Spacer(modifier = Modifier.height(6.dp))
                    Box {
                        OutlinedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { paymentMenuExpanded = true }
                        ) {
                            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Payments, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = paymentMethod, fontWeight = FontWeight.Medium)
                            }
                        }
                        DropdownMenu(expanded = paymentMenuExpanded, onDismissRequest = { paymentMenuExpanded = false }) {
                            paymentMethods.forEach { method ->
                                DropdownMenuItem(text = { Text(method) }, onClick = { paymentMethod = method; paymentMenuExpanded = false })
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = tr("Note (optional)", "ঐচ্ছিক নোট"), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                placeholder = { Text(tr("Add a note (optional)", "একটি নোট যোগ করুন (ঐচ্ছিক)")) },
                minLines = 2
            )

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = {
                    onSave(
                        Transaction(
                            id = transaction?.id ?: 0,
                            type = type,
                            amount = amount.toDoubleOrNull() ?: 0.0,
                            category = categoryLabel,
                            description = description,
                            date = date,
                            paymentMethod = paymentMethod,
                            note = note
                        )
                    )
                },
                enabled = canSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = if (isEditing) tr("Update Transaction", "লেনদেন আপডেট করুন") else tr("Save Transaction", "লেনদেন সংরক্ষণ করুন"), fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = date.time)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis -> date = Date(millis) }
                    showDatePicker = false
                }) { Text(tr("OK", "ঠিক আছে")) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text(tr("Cancel", "বাতিল")) }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun TypeToggleButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) color else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun CategoryTile(
    label: String,
    labelBn: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box {
        Column(
            modifier = Modifier
                .width(76.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (selected) color.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface)
                .clickable(onClick = onClick)
                .padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = tr(label, labelBn), tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = tr(label, labelBn), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold, maxLines = 1)
        }
        if (selected) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(11.dp))
            }
        }
    }
}
