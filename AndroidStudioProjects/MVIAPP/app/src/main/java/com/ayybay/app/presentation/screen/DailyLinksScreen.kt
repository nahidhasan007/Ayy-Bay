package com.ayybay.app.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ayybay.app.domain.model.LinkCategory
import com.ayybay.app.presentation.mvi.LinkCategoryItem
import com.ayybay.app.presentation.mvi.LinkUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyLinksScreen(
    uiState: LinkUiState,
    onCategoryClick: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Daily Links 🇧🇩",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Bangladesh Essential Links",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
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
                items(uiState.categories.filter { it.count > 0 }) { categoryItem ->
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
            .height(110.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = categoryItem.category.icon, fontSize = 28.sp)
            Column {
                Text(
                    text = categoryItem.category.displayName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "${categoryItem.count} links",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.85f)
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