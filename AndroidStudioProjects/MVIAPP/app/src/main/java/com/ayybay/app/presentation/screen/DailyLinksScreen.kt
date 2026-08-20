package com.ayybay.app.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ayybay.app.domain.model.LinkCategory
import com.ayybay.app.presentation.component.LanguageToggle
import com.ayybay.app.presentation.language.bnLabel
import com.ayybay.app.presentation.language.label
import com.ayybay.app.presentation.language.tr
import com.ayybay.app.presentation.mvi.LinkCategoryItem
import com.ayybay.app.presentation.mvi.LinkUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyLinksScreen(
    uiState: LinkUiState,
    onCategoryClick: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = tr("Useful Websites", "গুরুত্বপূর্ণ ওয়েবসাইট"), fontWeight = FontWeight.Bold)
                },
                actions = { LanguageToggle(modifier = Modifier.padding(end = 12.dp)) }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading && uiState.categories.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val visibleCategories = uiState.categories
                .filter { it.count > 0 }
                .filter {
                    searchQuery.isBlank() ||
                        it.category.displayName.contains(searchQuery, ignoreCase = true) ||
                        it.category.bnLabel().contains(searchQuery, ignoreCase = true)
                }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item(span = { GridItemSpan(2) }) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp),
                        placeholder = { Text(tr("Search websites…", "ওয়েবসাইট খুঁজুন…")) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        singleLine = true,
                        shape = RoundedCornerShape(50)
                    )
                }

                items(visibleCategories) { categoryItem ->
                    CategoryCard(
                        categoryItem = categoryItem,
                        onClick = { onCategoryClick(categoryItem.category.name) }
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryCard(
    categoryItem: LinkCategoryItem,
    onClick: () -> Unit
) {
    val color = categoryColor(categoryItem.category)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color.copy(alpha = 0.12f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = categoryItem.category.icon, fontSize = 20.sp)
            }
            Column {
                Text(
                    text = categoryItem.category.label(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = tr("${categoryItem.count} Websites", "${categoryItem.count}টি ওয়েবসাইট"),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

fun categoryColor(category: LinkCategory): Color = when (category) {
    LinkCategory.NEWS          -> Color(0xFF1565C0)
    LinkCategory.GOVERNMENT    -> Color(0xFF2E7D32)
    LinkCategory.JOBS          -> Color(0xFF6A1B9A)
    LinkCategory.EDUCATION     -> Color(0xFFE65100)
    LinkCategory.FINANCE       -> Color(0xFF00695C)
    LinkCategory.SHOPPING      -> Color(0xFFAD1457)
    LinkCategory.TRANSPORT     -> Color(0xFF283593)
    LinkCategory.HEALTH        -> Color(0xFFC62828)
    LinkCategory.EMERGENCY     -> Color(0xFFBF360C)
    LinkCategory.ENTERTAINMENT -> Color(0xFF4A148C)
    LinkCategory.TELECOM       -> Color(0xFF00838F)
    LinkCategory.SPORTS        -> Color(0xFF33691E)
    LinkCategory.ISLAMIC       -> Color(0xFF1B6B3A)
    LinkCategory.SOCIAL        -> Color(0xFF37474F)
    LinkCategory.OTHER         -> Color(0xFF455A64)
}
