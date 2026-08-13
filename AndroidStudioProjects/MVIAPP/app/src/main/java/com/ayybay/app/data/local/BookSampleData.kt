package com.ayybay.app.data.local

import com.ayybay.app.domain.model.Book
import com.ayybay.app.domain.model.ReligionCategory

object BookSampleData {

    fun getReligionCategories(): List<ReligionCategory> = listOf(
        ReligionCategory(
            id = "islam",
            name = "Islam",
            subtitle = "Quran, Hadith, Islamic Books",
            icon = "📖",
            colorHex = 0xFF1B6B3A,
            books = listOf(
                Book(1, "Al-Quran (Translation)"),
                Book(2, "Sahih Al-Bukhari"),
                Book(3, "Sahih Muslim"),
                Book(4, "Riyad as-Salihin")
            )
        ),
        ReligionCategory(
            id = "hinduism",
            name = "Hinduism",
            subtitle = "Bhagavad Gita, Hindu Scriptures",
            icon = "🪔",
            colorHex = 0xFFE65100,
            books = listOf(
                Book(5, "Bhagavad Gita"),
                Book(6, "Ramayana"),
                Book(7, "Mahabharata")
            )
        ),
        ReligionCategory(
            id = "christianity",
            name = "Christianity",
            subtitle = "Bible",
            icon = "✝️",
            colorHex = 0xFF1565C0,
            books = listOf(
                Book(8, "Holy Bible - Old Testament"),
                Book(9, "Holy Bible - New Testament")
            )
        ),
        ReligionCategory(
            id = "buddhism",
            name = "Buddhism",
            subtitle = "Buddhist Scriptures",
            icon = "☸️",
            colorHex = 0xFFC9A84C,
            books = listOf(
                Book(10, "Dhammapada"),
                Book(11, "Tripitaka (Selections)")
            )
        ),
        ReligionCategory(
            id = "other",
            name = "Other",
            subtitle = "Other Religious Books",
            icon = "📚",
            colorHex = 0xFF616161,
            books = listOf(
                Book(12, "Comparative Religion Essays"),
                Book(13, "World Scriptures Anthology")
            )
        )
    )
}
