package com.ayybay.app.presentation.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import com.ayybay.app.presentation.language.AppLanguage
import com.ayybay.app.presentation.viewmodel.LanguageViewModel
import org.koin.androidx.compose.koinViewModel

/**
 * Bangla | EN pill toggle shown on every top-level screen. Switches the app-wide
 * [AppLanguage] via [LanguageViewModel]; every screen reading [com.ayybay.app.presentation.language.LocalAppLanguage]
 * (through [com.ayybay.app.presentation.language.tr]) updates immediately.
 */
@Composable
fun LanguageToggle(
    modifier: Modifier = Modifier,
    languageViewModel: LanguageViewModel = koinViewModel()
) {
    val language by languageViewModel.language.collectAsState()

    Row(
        modifier = modifier
            .height(32.dp)
            .clip(RoundedCornerShape(50))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(50))
    ) {
        LanguageOption(
            label = "বাংলা",
            selected = language == AppLanguage.BN,
            onClick = { languageViewModel.setLanguage(AppLanguage.BN) }
        )
        Text(
            text = "|",
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            modifier = Modifier.padding(vertical = 6.dp)
        )
        LanguageOption(
            label = "EN",
            selected = language == AppLanguage.EN,
            onClick = { languageViewModel.setLanguage(AppLanguage.EN) }
        )
    }
}

@Composable
private fun LanguageOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Text(
        text = label,
        fontSize = 13.sp,
        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    )
}
