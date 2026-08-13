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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Bangla | EN pill toggle shown on every top-level screen in the mockup.
 * Visual only for now: it flips the highlighted side but does not translate any
 * screen text yet (full bilingual support is a separate, larger effort).
 */
@Composable
fun LanguageToggle(modifier: Modifier = Modifier) {
    var isBangla by remember { mutableStateOf(true) }

    Row(
        modifier = modifier
            .height(32.dp)
            .clip(RoundedCornerShape(50))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(50))
    ) {
        LanguageOption(
            label = "বাংলা",
            selected = isBangla,
            onClick = { isBangla = true }
        )
        Text(
            text = "|",
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            modifier = Modifier.padding(vertical = 6.dp)
        )
        LanguageOption(
            label = "EN",
            selected = !isBangla,
            onClick = { isBangla = false }
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
