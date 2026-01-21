package com.ayybay.app.presentation.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ayybay.app.domain.model.PrayerName
import com.ayybay.app.domain.model.PrayerTime
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerTimesCard(
    prayerTimes: List<PrayerTime>,
    onToggleNotification: (PrayerName, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "🕌 Prayer Times",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Current time: $currentTime",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Prayer Times List
            prayerTimes.sortedBy { it.prayerName.ordinal }.forEach { prayerTime ->
                PrayerTimeRow(
                    prayerTime = prayerTime,
                    onToggleNotification = onToggleNotification
                )
                if (prayerTime.prayerName != prayerTimes.lastOrNull()?.prayerName) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun PrayerTimeRow(
    prayerTime: PrayerTime,
    onToggleNotification: (PrayerName, Boolean) -> Unit
) {
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val formattedTime = timeFormat.format(prayerTime.time)

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Time display
        Text(
            text = formattedTime,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.primary
        )

        // Prayer name
        Text(
            text = prayerTime.prayerName.displayName,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )

        // Notification toggle
        IconButton(onClick = {
            onToggleNotification(
                prayerTime.prayerName,
                !prayerTime.isEnabled
            )
        }) {
            Icon(
                imageVector = Icons.Filled.Notifications,
                contentDescription = if (prayerTime.isEnabled) {
                    "Disable ${prayerTime.prayerName.displayName} notification"
                } else {
                    "Enable ${prayerTime.prayerName.displayName} notification"
                },
                tint = if (prayerTime.isEnabled) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f)
                }
            )
        }
    }
}

@Composable
fun SetupPrayerSettingsDialog(
    latitude: Double?,
    longitude: Double?,
    onDismiss: () -> Unit,
    onSave: (Double, Double) -> Unit
) {
    var lat by remember { mutableStateOf(latitude?.toString() ?: "") }
    var lon by remember { mutableStateOf(longitude?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Location") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Enter your location coordinates for accurate prayer times",
                    style = MaterialTheme.typography.bodyMedium
                )

                OutlinedTextField(
                    value = lat,
                    onValueChange = { lat = it },
                    label = { Text("Latitude") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                    )
                )

                OutlinedTextField(
                    value = lon,
                    onValueChange = { lon = it },
                    label = { Text("Longitude") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val latValue = lat.toDoubleOrNull() ?: 0.0
                    val lonValue = lon.toDoubleOrNull() ?: 0.0
                    onSave(latValue, lonValue)
                },
                enabled = lat.isNotBlank() && lon.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}