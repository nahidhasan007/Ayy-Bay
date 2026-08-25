package com.ayybay.app.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ayybay.app.data.local.QuranSurahData
import com.ayybay.app.data.local.Surah
import com.ayybay.app.presentation.language.tr

private val IslamicGreen = androidx.compose.ui.graphics.Color(0xFF1B6B3A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurahListScreen(
    completedNumbers: Set<Int> = emptySet(),
    onToggleComplete: (Int, Boolean) -> Unit = { _, _ -> },
    onSurahClick: (Surah) -> Unit,
    onBack: () -> Unit
) {
    val surahs = QuranSurahData.surahs()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "📖  ")
                        Text(text = tr("Al-Quran", "আল-কুরআন"), fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = tr("Back", "পেছনে"),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = IslamicGreen,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    text = tr(
                        "${surahs.size} Surahs · ${completedNumbers.size} completed",
                        "${surahs.size}টি সূরা · ${completedNumbers.size}টি সম্পন্ন"
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            items(surahs, key = { it.number }) { surah ->
                SurahRow(
                    surah = surah,
                    isCompleted = completedNumbers.contains(surah.number),
                    onClick = { onSurahClick(surah) },
                    onToggleComplete = { onToggleComplete(surah.number, !completedNumbers.contains(surah.number)) }
                )
            }
        }
    }
}

@Composable
private fun SurahRow(
    surah: Surah,
    isCompleted: Boolean,
    onClick: () -> Unit,
    onToggleComplete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(IslamicGreen.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${surah.number}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = IslamicGreen
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Text(text = tr(surah.name, surah.nameBn), modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
            IconButton(onClick = onToggleComplete) {
                Icon(
                    imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = tr("Mark as read", "পঠিত হিসেবে চিহ্নিত করুন"),
                    tint = if (isCompleted) IslamicGreen else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
