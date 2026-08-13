package com.ayybay.app.presentation.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.vector.ImageVector
import com.ayybay.app.ui.theme.CategoryBills
import com.ayybay.app.ui.theme.CategoryEducation
import com.ayybay.app.ui.theme.CategoryFood
import com.ayybay.app.ui.theme.CategoryHealth
import com.ayybay.app.ui.theme.CategoryOther
import com.ayybay.app.ui.theme.CategoryShopping
import com.ayybay.app.ui.theme.CategoryTransport
import com.ayybay.app.ui.theme.IncomeGreen
import androidx.compose.ui.graphics.Color

enum class ExpenseCategory(val label: String, val labelBn: String, val icon: ImageVector, val color: Color) {
    FOOD("Food", "খাবার", Icons.Default.Restaurant, CategoryFood),
    TRANSPORT("Transport", "যাতায়াত", Icons.Default.DirectionsCar, CategoryTransport),
    SHOPPING("Shopping", "কেনাকাটা", Icons.Default.ShoppingBag, CategoryShopping),
    BILLS("Bills", "বিল", Icons.Default.Bolt, CategoryBills),
    HEALTH("Health", "স্বাস্থ্য", Icons.Default.Favorite, CategoryHealth),
    EDUCATION("Education", "শিক্ষা", Icons.Default.School, CategoryEducation),
    OTHER("Other", "অন্যান্য", Icons.Default.MoreHoriz, CategoryOther)
}

enum class IncomeCategory(val label: String, val labelBn: String, val icon: ImageVector, val color: Color) {
    SALARY("Salary", "বেতন", Icons.Default.AttachMoney, IncomeGreen),
    FREELANCE("Freelance", "ফ্রিল্যান্স", Icons.Default.Work, IncomeGreen),
    INVESTMENT("Investment", "বিনিয়োগ", Icons.AutoMirrored.Filled.TrendingUp, IncomeGreen),
    GIFT("Gift", "উপহার", Icons.Default.CardGiftcard, IncomeGreen),
    OTHER("Other", "অন্যান্য", Icons.Default.MoreHoriz, IncomeGreen)
}

/** Buckets a free-text transaction category into one of the fixed expense categories used for charts/icons. */
fun expenseBucketOf(category: String): ExpenseCategory {
    val c = category.lowercase()
    return when {
        c.contains("food") || c.contains("grocer") || c.contains("restaurant") || c.contains("dining") -> ExpenseCategory.FOOD
        c.contains("transport") || c.contains("uber") || c.contains("fuel") || c.contains("gas") || c.contains("car") -> ExpenseCategory.TRANSPORT
        c.contains("shop") -> ExpenseCategory.SHOPPING
        c.contains("bill") || c.contains("electric") || c.contains("rent") || c.contains("internet") || c.contains("utilit") -> ExpenseCategory.BILLS
        c.contains("health") || c.contains("medical") || c.contains("doctor") -> ExpenseCategory.HEALTH
        c.contains("educat") || c.contains("school") || c.contains("tuition") -> ExpenseCategory.EDUCATION
        c.contains("entertain") -> ExpenseCategory.OTHER
        else -> ExpenseCategory.OTHER
    }
}

fun incomeBucketOf(category: String): IncomeCategory {
    val c = category.lowercase()
    return when {
        c.contains("salary") -> IncomeCategory.SALARY
        c.contains("freelance") -> IncomeCategory.FREELANCE
        c.contains("invest") || c.contains("dividend") || c.contains("stock") -> IncomeCategory.INVESTMENT
        c.contains("gift") -> IncomeCategory.GIFT
        else -> IncomeCategory.OTHER
    }
}
