package com.ayybay.app.presentation.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ayybay.app.data.local.JobSampleData
import com.ayybay.app.domain.model.GovtJob
import com.ayybay.app.domain.model.JobTag
import com.ayybay.app.presentation.component.AppTopBar
import com.ayybay.app.presentation.component.LanguageToggle
import com.ayybay.app.presentation.language.tr
import com.ayybay.app.ui.theme.ExpenseRed
import com.ayybay.app.ui.theme.IncomeGreenTint

private data class JobFilter(val labelEn: String, val labelBn: String, val tag: JobTag?)

private val jobFilters = listOf(
    JobFilter("All", "সকল", null),
    JobFilter("New", "নতুন", JobTag.NEW),
    JobFilter("Deadline Soon", "শেষ তারিখ কাছাকাছি", JobTag.DEADLINE_SOON),
    JobFilter("BCS", "বিসিএস", JobTag.BCS),
    JobFilter("Bank", "ব্যাংক", JobTag.BANK),
    JobFilter("Defense", "প্রতিরক্ষা", JobTag.DEFENSE)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobsScreen() {
    val context = LocalContext.current
    val jobs = remember { JobSampleData.getSampleJobs() }
    var selectedFilter by remember { mutableStateOf(jobFilters.first()) }
    var searchQuery by remember { mutableStateOf("") }
    var searchActive by remember { mutableStateOf(false) }
    var bookmarkedOnly by remember { mutableStateOf(false) }
    var bookmarked by remember { mutableStateOf(setOf<Long>()) }

    val featured = jobs.firstOrNull { it.isFeatured }
    val filteredJobs = jobs.filter { job ->
        (selectedFilter.tag == null || job.tags.contains(selectedFilter.tag)) &&
            (!bookmarkedOnly || bookmarked.contains(job.id)) &&
            (searchQuery.isBlank() || job.title.contains(searchQuery, ignoreCase = true) || job.organization.contains(searchQuery, ignoreCase = true))
    }

    fun openUrl(url: String) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = {
                    Text(text = tr("Govt Jobs", "সরকারি চাকরি"), fontWeight = FontWeight.Bold)
                },
                actions = {
                    IconButton(onClick = { searchActive = !searchActive }) {
                        Icon(Icons.Default.Search, contentDescription = tr("Search", "খুঁজুন"))
                    }
                    IconButton(onClick = { bookmarkedOnly = !bookmarkedOnly }) {
                        Icon(
                            imageVector = if (bookmarkedOnly) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = tr("Bookmarked jobs", "বুকমার্ক করা চাকরি"),
                            tint = if (bookmarkedOnly) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onPrimary
                        )
                    }
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (searchActive) {
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(tr("Search jobs or organizations…", "চাকরি বা প্রতিষ্ঠান খুঁজুন…")) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        singleLine = true,
                        shape = RoundedCornerShape(50)
                    )
                }
            }

            if (featured != null && selectedFilter.tag == null && !bookmarkedOnly && searchQuery.isBlank()) {
                item {
                    FeaturedJobCard(job = featured, onViewCircular = { openUrl(featured.websiteUrl) })
                }
            }

            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(jobFilters) { filter ->
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            label = { Text(tr(filter.labelEn, filter.labelBn)) }
                        )
                    }
                }
            }

            if (filteredJobs.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text(text = tr("No jobs match your filters", "আপনার ফিল্টারের সাথে মিলে এমন কোনো চাকরি নেই"), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                items(filteredJobs, key = { it.id }) { job ->
                    JobCard(
                        job = job,
                        isBookmarked = bookmarked.contains(job.id),
                        onToggleBookmark = {
                            bookmarked = if (bookmarked.contains(job.id)) bookmarked - job.id else bookmarked + job.id
                        },
                        onOpenWebsite = { openUrl(job.websiteUrl) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FeaturedJobCard(job: GovtJob, onViewCircular: () -> Unit) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = IncomeGreenTint),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    JobLogo(job.logoEmoji, size = 48.dp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = job.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Work, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = job.organization, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                if (job.isNew) {
                    Surface(shape = RoundedCornerShape(50), color = MaterialTheme.colorScheme.primary) {
                        Text(text = tr("NEW", "নতুন"), color = androidx.compose.ui.graphics.Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                JobMetaColumn(icon = Icons.Default.CalendarToday, label = tr("Published", "প্রকাশিত"), value = job.publishedDate)
                JobMetaColumn(icon = Icons.Default.Groups, label = tr("Vacancies", "শূন্যপদ"), value = job.vacancies.toString())
                JobMetaColumn(icon = Icons.Default.Event, label = tr("Deadline", "শেষ তারিখ"), value = job.deadline, valueColor = ExpenseRed)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onViewCircular,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(tr("View Circular", "সার্কুলার দেখুন"), fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(6.dp))
                Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun JobMetaColumn(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(text = value, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = valueColor)
    }
}

@Composable
private fun JobCard(
    job: GovtJob,
    isBookmarked: Boolean,
    onToggleBookmark: () -> Unit,
    onOpenWebsite: () -> Unit
) {
    Card(shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                JobLogo(job.logoEmoji, size = 44.dp)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = job.organization, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text(text = job.title, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row {
                        Text(text = "${tr("Published", "প্রকাশিত")}: ${job.publishedDate}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Row {
                        Text(text = "${tr("Deadline", "শেষ তারিখ")}: ", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = job.deadline, style = MaterialTheme.typography.labelSmall, color = ExpenseRed, fontWeight = FontWeight.Bold)
                    }
                }
                IconButton(onClick = onToggleBookmark, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = tr("Bookmark", "বুকমার্ক"),
                        tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedButton(
                onClick = onOpenWebsite,
                modifier = Modifier.align(Alignment.End),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(tr("Open Website", "ওয়েবসাইট খুলুন"))
                Spacer(modifier = Modifier.width(6.dp))
                Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
            }
        }
    }
}

@Composable
private fun JobLogo(emoji: String, size: androidx.compose.ui.unit.Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center
    ) {
        Text(text = emoji, fontSize = (size.value / 2).sp)
    }
}
