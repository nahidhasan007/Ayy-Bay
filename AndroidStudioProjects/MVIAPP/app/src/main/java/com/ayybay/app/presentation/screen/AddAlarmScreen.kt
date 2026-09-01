package com.ayybay.app.presentation.screen

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ayybay.app.domain.model.Alarm
import com.ayybay.app.presentation.component.AppTopBar
import com.ayybay.app.presentation.component.LanguageToggle
import com.ayybay.app.presentation.language.tr
import java.util.Calendar

private val weekDays = listOf(
    Calendar.SUNDAY to ("Sun" to "রবি"),
    Calendar.MONDAY to ("Mon" to "সোম"),
    Calendar.TUESDAY to ("Tue" to "মঙ্গল"),
    Calendar.WEDNESDAY to ("Wed" to "বুধ"),
    Calendar.THURSDAY to ("Thu" to "বৃহঃ"),
    Calendar.FRIDAY to ("Fri" to "শুক্র"),
    Calendar.SATURDAY to ("Sat" to "শনি")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAlarmScreen(
    alarm: Alarm?,
    onSave: (Alarm) -> Unit,
    onBack: () -> Unit
) {
    val now = remember { Calendar.getInstance() }
    val timePickerState = rememberTimePickerState(
        initialHour = alarm?.hour ?: now.get(Calendar.HOUR_OF_DAY),
        initialMinute = alarm?.minute ?: now.get(Calendar.MINUTE),
        is24Hour = false
    )
    var label by remember { mutableStateOf(alarm?.label ?: "") }
    var repeatDays by remember { mutableStateOf(alarm?.repeatDays ?: emptySet()) }
    var vibrate by remember { mutableStateOf(alarm?.vibrate ?: true) }

    val isEditing = alarm != null

    Scaffold(
        topBar = {
            AppTopBar(
                title = {
                    Text(
                        text = if (isEditing) tr("Edit Alarm", "অ্যালার্ম সম্পাদনা") else tr("Add Alarm", "অ্যালার্ম যোগ করুন"),
                        fontWeight = FontWeight.Bold
                    )
                },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TimePicker(state = timePickerState)

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = tr("Repeat", "পুনরাবৃত্তি"),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRowDays(
                selected = repeatDays,
                onToggle = { day ->
                    repeatDays = if (repeatDays.contains(day)) repeatDays - day else repeatDays + day
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = tr("Label", "লেবেল"),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = label,
                onValueChange = { label = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(tr("e.g. Wake up", "যেমন: ঘুম থেকে উঠুন")) },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = tr("Vibrate", "কম্পন"), style = MaterialTheme.typography.bodyLarge)
                Switch(checked = vibrate, onCheckedChange = { vibrate = it })
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = {
                    onSave(
                        Alarm(
                            id = alarm?.id ?: 0,
                            hour = timePickerState.hour,
                            minute = timePickerState.minute,
                            label = label,
                            isEnabled = alarm?.isEnabled ?: true,
                            repeatDays = repeatDays,
                            vibrate = vibrate,
                            createdAt = alarm?.createdAt ?: 0L
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isEditing) tr("Update Alarm", "অ্যালার্ম আপডেট করুন") else tr("Save Alarm", "অ্যালার্ম সংরক্ষণ করুন"),
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun FlowRowDays(selected: Set<Int>, onToggle: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        weekDays.forEach { (day, labels) ->
            val (en, bn) = labels
            FilterChip(
                selected = selected.contains(day),
                onClick = { onToggle(day) },
                label = { Text(tr(en, bn), style = MaterialTheme.typography.labelSmall) }
            )
        }
    }
}
