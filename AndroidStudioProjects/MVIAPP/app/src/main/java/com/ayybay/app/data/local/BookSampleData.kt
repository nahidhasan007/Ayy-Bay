package com.ayybay.app.data.local

import com.ayybay.app.domain.model.Book
import com.ayybay.app.domain.model.ReligionCategory

object BookSampleData {

    fun getReligionCategories(): List<ReligionCategory> = listOf(
        ReligionCategory(
            id = "islam",
            name = "Islam",
            nameBn = "ইসলাম",
            subtitle = "Quran, Hadith, Islamic Books",
            subtitleBn = "কুরআন, হাদীস ও ইসলামী বই",
            icon = "📖",
            colorHex = 0xFF1B6B3A,
            books = listOf(
                Book(1, "Al-Quran (Translation)", "আল-কুরআন (অনুবাদ)"),
                Book(2, "Sahih Al-Bukhari", "সহীহ আল-বুখারী", url = "https://sunnah.com/bukhari"),
                Book(3, "Sahih Muslim", "সহীহ মুসলিম", url = "https://sunnah.com/muslim"),
                Book(4, "Riyad as-Salihin", "রিয়াদুস সালেহীন", url = "https://sunnah.com/riyadhussalihin")
            )
        ),
        ReligionCategory(
            id = "hinduism",
            name = "Hinduism",
            nameBn = "হিন্দুধর্ম",
            subtitle = "Bhagavad Gita, Hindu Scriptures",
            subtitleBn = "ভগবদ গীতা ও হিন্দু ধর্মগ্রন্থ",
            icon = "🪔",
            colorHex = 0xFFE65100,
            books = listOf(
                Book(5, "Bhagavad Gita", "ভগবদ গীতা", url = "https://www.holy-bhagavad-gita.org/"),
                Book(6, "Ramayana", "রামায়ণ", url = "https://www.sacred-texts.com/hin/rama/index.htm"),
                Book(7, "Mahabharata", "মহাভারত", url = "https://www.sacred-texts.com/hin/maha/index.htm")
            )
        ),
        ReligionCategory(
            id = "christianity",
            name = "Christianity",
            nameBn = "খ্রিস্টধর্ম",
            subtitle = "Bible",
            subtitleBn = "বাইবেল",
            icon = "✝️",
            colorHex = 0xFF1565C0,
            books = listOf(
                Book(8, "Holy Bible - Old Testament", "পবিত্র বাইবেল - পুরাতন নিয়ম", url = "https://www.biblegateway.com/passage/?search=Genesis%201&version=KJV"),
                Book(9, "Holy Bible - New Testament", "পবিত্র বাইবেল - নতুন নিয়ম", url = "https://www.biblegateway.com/passage/?search=Matthew%201&version=KJV")
            )
        ),
        ReligionCategory(
            id = "buddhism",
            name = "Buddhism",
            nameBn = "বৌদ্ধধর্ম",
            subtitle = "Buddhist Scriptures",
            subtitleBn = "বৌদ্ধ ধর্মগ্রন্থ",
            icon = "☸️",
            colorHex = 0xFFC9A84C,
            books = listOf(
                Book(10, "Dhammapada", "ধম্মপদ", url = "https://www.accesstoinsight.org/tipitaka/kn/dhp/"),
                Book(11, "Tripitaka (Selections)", "ত্রিপিটক (নির্বাচিত অংশ)", url = "https://www.accesstoinsight.org/tipitaka/")
            )
        ),
        ReligionCategory(
            id = "other",
            name = "Other",
            nameBn = "অন্যান্য",
            subtitle = "Other Religious Books",
            subtitleBn = "অন্যান্য ধর্মীয় বই",
            icon = "📚",
            colorHex = 0xFF616161,
            books = listOf(
                Book(12, "Comparative Religion Essays", "তুলনামূলক ধর্ম প্রবন্ধ", url = "https://www.sacred-texts.com/index.htm"),
                Book(13, "World Scriptures Anthology", "বিশ্ব ধর্মগ্রন্থ সংকলন", url = "https://www.worldscripture.org/")
            )
        )
    )
}
