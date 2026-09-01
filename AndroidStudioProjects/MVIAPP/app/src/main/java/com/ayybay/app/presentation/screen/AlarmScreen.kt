package com.ayybay.app.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ayybay.app.domain.model.Alarm
import com.ayybay.app.presentation.component.AppTopBar
import com.ayybay.app.presentation.component.LanguageToggle
import com.ayybay.app.presentation.language.AppLanguage
import com.ayybay.app.presentation.language.LocalAppLanguage
import com.ayybay.app.presentation.language.tr
import com.ayybay.app.ui.theme.AlarmPurple
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmScreen(
    alarms: List<Alarm>,
    onAddAlarm: () -> Unit,
    onEditAlarm: (Alarm) -> Unit,
    onDeleteAlarm: (Alarm) -> Unit,
    onToggleAlarm: (Alarm, Boolean) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = { Text(text = tr("Alarms", "অ্যালার্ম"), fontWeight = FontWeight.Bold) },
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
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddAlarm, containerColor = AlarmPurple) {
                Icon(Icons.Default.Add, contentDescription = tr("Add Alarm", "অ্যালার্ম যোগ করুন"), tint = androidx.compose.ui.graphics.Color.White)
            }
        }
    ) { paddingValues ->
        if (alarms.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = tr("No alarms yet — tap + to add one", "কোনো অ্যালার্ম নেই — যোগ করতে + চাপুন"),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(alarms, key = { it.id }) { alarm ->
                    AlarmCard(
                        alarm = alarm,
                        onClick = { onEditAlarm(alarm) },
                        onDelete = { onDeleteAlarm(alarm) },
                        onToggle = { enabled -> onToggleAlarm(alarm, enabled) }
                    )
                }
                item { Spacer(modifier = Modifier.height(72.dp)) }
            }
        }
    }
}

private val dayAbbrevEn = mapOf(
    Calendar.SUNDAY to "Sun", Calendar.MONDAY to "Mon", Calendar.TUESDAY to "Tue",
    Calendar.WEDNESDAY to "Wed", Calendar.THURSDAY to "Thu", Calendar.FRIDAY to "Fri", Calendar.SATURDAY to "Sat"
)
private val dayAbbrevBn = mapOf(
    Calendar.SUNDAY to "রবি", Calendar.MONDAY to "সোম", Calendar.TUESDAY to "মঙ্গল",
    Calendar.WEDNESDAY to "বুধ", Calendar.THURSDAY to "বৃহঃ", Calendar.FRIDAY to "শুক্র", Calendar.SATURDAY to "শনি"
)

@Composable
private fun repeatSummary(repeatDays: Set<Int>): String = when {
    repeatDays.isEmpty() -> tr("One-time", "একবার")
    repeatDays.size == 7 -> tr("Every day", "প্রতিদিন")
    else -> {
        val labels = if (LocalAppLanguage.current == AppLanguage.EN) dayAbbrevEn else dayAbbrevBn
        repeatDays.sorted().joinToString(", ") { labels[it] ?: "" }
    }
}

@Composable
private fun AlarmCard(
    alarm: Alarm,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onToggle: (Boolean) -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AlarmPurple.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Alarm, contentDescription = null, tint = AlarmPurple, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "%02d:%02d".format(alarm.hour, alarm.minute),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
                if (alarm.label.isNotBlank()) {
                    Text(text = alarm.label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(
                    text = repeatSummary(alarm.repeatDays),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(checked = alarm.isEnabled, onCheckedChange = onToggle)
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = tr("More options", "আরও অপশন"))
                }
                DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                    DropdownMenuItem(
                        text = { Text(tr("Edit", "সম্পাদনা")) },
                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                        onClick = { menuExpanded = false; onClick() }
                    )
                    DropdownMenuItem(
                        text = { Text(tr("Delete", "মুছুন")) },
                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) },
                        onClick = { menuExpanded = false; onDelete() }
                    )
                }
            }
        }
    }
}
