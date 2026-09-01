package com.ayybay.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Islamic green palette
val IslamicGreen = Color(0xFF1B6B3A)
val IslamicGreenDark = Color(0xFF0D4A26)
val IslamicGreenLight = Color(0xFF4CAF50)
val IslamicGold = Color(0xFFC9A84C)
val IslamicGoldLight = Color(0xFFFFD54F)

val NightBlue = Color(0xFF0D1B2A)
val NightBlueSurface = Color(0xFF1A2E3D)
val MoonSilver = Color(0xFFB0BEC5)

// Semantic finance/status colors
val IncomeGreen = Color(0xFF2E7D32)
val ExpenseRed = Color(0xFFE53935)
val BalanceOrange = Color(0xFFF57C00)
val InfoBlue = Color(0xFF1976D2)
val AlarmPurple = Color(0xFF6A4C93)

// Light tint backgrounds for stat cards
val IncomeGreenTint = Color(0xFFE8F5E9)
val ExpenseRedTint = Color(0xFFFFEBEE)
val BalanceOrangeTint = Color(0xFFFFF3E0)
val InfoBlueTint = Color(0xFFE3F2FD)

// Category accent colors (Add Transaction / Expense Categories)
val CategoryFood = Color(0xFF2E7D32)
val CategoryTransport = Color(0xFF1976D2)
val CategoryShopping = Color(0xFF6A1B9A)
val CategoryBills = Color(0xFFF9A825)
val CategoryHealth = Color(0xFFD32F2F)
val CategoryEducation = Color(0xFF1565C0)
val CategoryOther = Color(0xFF616161)

private val DarkColorScheme = darkColorScheme(
    primary = IslamicGreenLight,
    onPrimary = Color.Black,
    primaryContainer = NightBlueSurface,
    onPrimaryContainer = IslamicGreenLight,
    secondary = IslamicGold,
    onSecondary = Color.Black,
    background = NightBlue,
    onBackground = Color.White,
    surface = NightBlueSurface,
    onSurface = Color.White,
    tertiary = IslamicGoldLight
)

private val LightColorScheme = lightColorScheme(
    primary = IslamicGreen,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE8F5E9),
    onPrimaryContainer = IslamicGreenDark,
    secondary = IslamicGold,
    onSecondary = Color.White,
    background = Color(0xFFF5F5F5),
    onBackground = Color(0xFF1C1B1F),
    surface = Color.White,
    onSurface = Color(0xFF1C1B1F),
    tertiary = Color(0xFF2E7D32)
)

@Composable
fun MVIAPPTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
