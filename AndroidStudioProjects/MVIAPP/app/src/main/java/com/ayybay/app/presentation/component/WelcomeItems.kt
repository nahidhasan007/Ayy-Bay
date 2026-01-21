package com.ayybay.app.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WelcomeMessage() {
    Text(
        text = "🕌 Welcome to Adhan App\n\nPrayer Times:\n• Fajr: 5:00 AM\n• Johr: 1:00 PM\n• Asar: 5:00 PM\n• Magrib: 6:10 PM\n• Esha: 8:00 PM",
        fontSize = 16.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color(0xFFE8F5E9))
            .padding(16.dp)
    )
}