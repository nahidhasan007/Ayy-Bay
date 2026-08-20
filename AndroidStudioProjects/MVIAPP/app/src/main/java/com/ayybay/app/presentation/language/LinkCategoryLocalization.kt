package com.ayybay.app.presentation.language

import androidx.compose.runtime.Composable
import com.ayybay.app.domain.model.LinkCategory

fun LinkCategory.bnLabel(): String = when (this) {
    LinkCategory.NEWS -> "সংবাদ"
    LinkCategory.GOVERNMENT -> "সরকারি"
    LinkCategory.JOBS -> "চাকরি"
    LinkCategory.EDUCATION -> "শিক্ষা"
    LinkCategory.FINANCE -> "অর্থনীতি"
    LinkCategory.SHOPPING -> "কেনাকাটা"
    LinkCategory.TRANSPORT -> "যোগাযোগ"
    LinkCategory.HEALTH -> "স্বাস্থ্য"
    LinkCategory.EMERGENCY -> "জরুরি"
    LinkCategory.ENTERTAINMENT -> "বিনোদন"
    LinkCategory.TELECOM -> "টেলিকম"
    LinkCategory.SPORTS -> "খেলাধুলা"
    LinkCategory.ISLAMIC -> "ইসলামিক"
    LinkCategory.SOCIAL -> "সামাজিক"
    LinkCategory.OTHER -> "অন্যান্য"
}

@Composable
fun LinkCategory.label(): String = tr(displayName, bnLabel())
