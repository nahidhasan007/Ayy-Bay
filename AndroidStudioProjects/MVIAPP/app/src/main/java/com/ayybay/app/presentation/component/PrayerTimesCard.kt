package com.ayybay.app.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ayybay.app.domain.model.PrayerName
import com.ayybay.app.domain.model.PrayerTime
import com.ayybay.app.service.AdhanForegroundService
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

private fun prayerIcon(prayerName: PrayerName): String = when (prayerName) {
    PrayerName.FAJR -> "🌙"
    PrayerName.DHUHR -> "🌤️"
    PrayerName.ASR -> "☀️"
    PrayerName.MAGHRIB -> "🌅"
    PrayerName.ISHA -> "🌠"
}

private fun formatTimeRemaining(diffMs: Long): String {
    val hours = TimeUnit.MILLISECONDS.toHours(diffMs)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(diffMs) % 60
    return when {
        hours > 0 -> "in ${hours}h ${minutes}m"
        minutes > 0 -> "in ${minutes}m"
        else -> "now"
    }
}

@Composable
fun PrayerTimesCard(
    prayerTimes: List<PrayerTime>,
    onToggleNotification: (PrayerName, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val now = remember { Date() }
    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val dateFormat = remember { SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault()) }

    val sortedPrayers = prayerTimes.sortedBy { it.prayerName.ordinal }
    val nextPrayer = sortedPrayers.filter { it.time.after(now) }.minByOrNull { it.time }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "🕌 Prayer Times",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = dateFormat.format(now),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.65f)
                    )
                }

                // Next prayer badge
                nextPrayer?.let { prayer ->
                    val diff = prayer.time.time - now.time
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = prayer.prayerName.displayName,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = formatTimeRemaining(diff),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Prayer rows
            sortedPrayers.forEachIndexed { index, prayerTime ->
                val isNext = prayerTime.prayerName == nextPrayer?.prayerName
                val isPassed = prayerTime.time.before(now)
                PrayerTimeRow(
                    prayerTime = prayerTime,
                    isNext = isNext,
                    isPassed = isPassed,
                    timeFormat = timeFormat,
                    onToggleNotification = onToggleNotification
                )
                if (index < sortedPrayers.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 6.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Play Adhan button
            Button(
                onClick = {
                    val prayerLabel = nextPrayer?.prayerName?.displayName ?: "Prayer"
                    AdhanForegroundService.startAdhan(context, prayerLabel, 90)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "▶  Play Adhan",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
private fun PrayerTimeRow(
    prayerTime: PrayerTime,
    isNext: Boolean,
    isPassed: Boolean,
    timeFormat: SimpleDateFormat,
    onToggleNotification: (PrayerName, Boolean) -> Unit
) {
    val rowBg = if (isNext) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(rowBg)
            .padding(horizontal = if (isNext) 8.dp else 0.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = prayerIcon(prayerTime.prayerName),
            fontSize = 20.sp,
            modifier = Modifier.width(32.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = prayerTime.prayerName.displayName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isNext) FontWeight.Bold else FontWeight.Medium,
                color = when {
                    isNext -> MaterialTheme.colorScheme.primary
                    isPassed -> MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.4f)
                    else -> MaterialTheme.colorScheme.onPrimaryContainer
                }
            )
            if (isNext) {
                Text(
                    text = "Next prayer",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                )
            }
        }

        Text(
            text = timeFormat.format(prayerTime.time),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = if (isNext) FontWeight.Bold else FontWeight.Normal,
            color = when {
                isNext -> MaterialTheme.colorScheme.primary
                isPassed -> MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.35f)
                else -> MaterialTheme.colorScheme.onPrimaryContainer
            },
            modifier = Modifier.padding(end = 4.dp)
        )

        IconButton(
            onClick = { onToggleNotification(prayerTime.prayerName, !prayerTime.isEnabled) },
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Notifications,
                contentDescription = if (prayerTime.isEnabled) "Disable notification" else "Enable notification",
                tint = if (prayerTime.isEnabled) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.25f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}