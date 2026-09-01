package com.ayybay.app.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ayybay.app.presentation.component.AppTopBar
import com.ayybay.app.presentation.component.LanguageToggle
import com.ayybay.app.presentation.language.tr
import com.ayybay.app.presentation.util.calculateAge
import com.ayybay.app.ui.theme.IncomeGreen
import com.ayybay.app.ui.theme.IncomeGreenTint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgeCalculatorScreen(
    dateOfBirth: Long?,
    onSetDateOfBirth: (Long) -> Unit,
    onBack: () -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormat = remember { SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()) }
    val now = remember { System.currentTimeMillis() }
    val breakdown = remember(dateOfBirth, now) { dateOfBirth?.let { calculateAge(it, now) } }

    Scaffold(
        topBar = {
            AppTopBar(
                title = { Text(text = "🎂  " + tr("Age Calculator", "বয়স ক্যালকুলেটর"), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = tr("Back", "পেছনে"))
                    }
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = tr("Date of Birth", "জন্ম তারিখ"),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
            item {
                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true }
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CalendarToday, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = dateOfBirth?.let { dateFormat.format(Date(it)) } ?: tr("Select your date of birth", "আপনার জন্ম তারিখ নির্বাচন করুন"),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            if (breakdown != null) {
                item {
                    Card(shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Cake, contentDescription = null, tint = IncomeGreen)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = tr("Your Age", "আপনার বয়স"), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                AgeUnitTile(value = breakdown.years, labelEn = "Years", labelBn = "বছর", modifier = Modifier.weight(1f))
                                AgeUnitTile(value = breakdown.months, labelEn = "Months", labelBn = "মাস", modifier = Modifier.weight(1f))
                                AgeUnitTile(value = breakdown.days, labelEn = "Days", labelBn = "দিন", modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
                item {
                    Card(shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            InfoLine(tr("Total days lived", "মোট বেঁচে থাকা দিন"), "${breakdown.totalDays}")
                            Spacer(modifier = Modifier.height(8.dp))
                            InfoLine(tr("Next birthday in", "পরবর্তী জন্মদিন"), tr("${breakdown.nextBirthdayInDays} days", "${breakdown.nextBirthdayInDays} দিন"))
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dateOfBirth ?: now)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { onSetDateOfBirth(it) }
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
private fun AgeUnitTile(value: Int, labelEn: String, labelBn: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(IncomeGreenTint, RoundedCornerShape(14.dp))
            .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "$value", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = IncomeGreen)
        Text(text = tr(labelEn, labelBn), style = MaterialTheme.typography.labelSmall, color = IncomeGreen)
    }
}

@Composable
private fun InfoLine(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontWeight = FontWeight.SemiBold)
    }
}
