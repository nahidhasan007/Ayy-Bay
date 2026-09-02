package com.ayybay.app.data.local

import com.ayybay.app.domain.model.GovtJob
import com.ayybay.app.domain.model.JobTag

object JobSampleData {

    fun getSampleJobs(): List<GovtJob> = listOf(
        GovtJob(
            id = 1,
            organization = "Bangladesh Bank",
            organizationBn = "বাংলাদেশ ব্যাংক",
            title = "Bangladesh Bank Job Circular 2026",
            titleBn = "বাংলাদেশ ব্যাংক নিয়োগ বিজ্ঞপ্তি ২০২৬",
            logoEmoji = "🏦",
            publishedDate = "10 Aug 2026",
            deadline = "31 Aug 2026",
            vacancies = 320,
            websiteUrl = "https://www.bb.org.bd",
            tags = setOf(JobTag.NEW, JobTag.BANK),
            isNew = true,
            isFeatured = true
        ),
        GovtJob(
            id = 2,
            organization = "Bangladesh Railway",
            organizationBn = "বাংলাদেশ রেলওয়ে",
            title = "Junior Assistant (TA)",
            titleBn = "জুনিয়র সহকারী (টিএ)",
            logoEmoji = "🚆",
            publishedDate = "09 Aug 2026",
            deadline = "30 Aug 2026",
            vacancies = 145,
            websiteUrl = "https://railway.gov.bd",
            tags = setOf(JobTag.DEADLINE_SOON)
        ),
        GovtJob(
            id = 3,
            organization = "Bangladesh Police",
            organizationBn = "বাংলাদেশ পুলিশ",
            title = "Sub-Inspector (SI)",
            titleBn = "উপ-পরিদর্শক (এসআই)",
            logoEmoji = "👮",
            publishedDate = "08 Aug 2026",
            deadline = "28 Aug 2026",
            vacancies = 210,
            websiteUrl = "https://police.gov.bd",
            tags = setOf(JobTag.DEFENSE, JobTag.DEADLINE_SOON)
        ),
        GovtJob(
            id = 4,
            organization = "Bangladesh Bank",
            organizationBn = "বাংলাদেশ ব্যাংক",
            title = "Officer (General)",
            titleBn = "অফিসার (জেনারেল)",
            logoEmoji = "🏦",
            publishedDate = "10 Aug 2026",
            deadline = "31 Aug 2026",
            vacancies = 180,
            websiteUrl = "https://www.bb.org.bd",
            tags = setOf(JobTag.BANK)
        ),
        GovtJob(
            id = 5,
            organization = "BPSC (Public Service Commission)",
            organizationBn = "বাংলাদেশ সরকারি কর্ম কমিশন",
            title = "BCS (44th)",
            titleBn = "বিসিএস (৪৪তম)",
            logoEmoji = "🏛️",
            publishedDate = "07 Aug 2026",
            deadline = "27 Aug 2026",
            vacancies = 2100,
            websiteUrl = "https://bpsc.gov.bd",
            tags = setOf(JobTag.BCS)
        ),
        GovtJob(
            id = 6,
            organization = "Ministry of Education",
            organizationBn = "শিক্ষা মন্ত্রণালয়",
            title = "Assistant Teacher",
            titleBn = "সহকারী শিক্ষক",
            logoEmoji = "🎓",
            publishedDate = "09 Aug 2026",
            deadline = "29 Aug 2026",
            vacancies = 560,
            websiteUrl = "https://moedu.gov.bd"
        ),
        GovtJob(
            id = 7,
            organization = "Ministry of Health & Family Welfare",
            organizationBn = "স্বাস্থ্য ও পরিবার কল্যাণ মন্ত্রণালয়",
            title = "Medical Officer",
            titleBn = "মেডিকেল অফিসার",
            logoEmoji = "🏥",
            publishedDate = "07 Aug 2026",
            deadline = "26 Aug 2026",
            vacancies = 95,
            websiteUrl = "https://mohfw.gov.bd",
            tags = setOf(JobTag.DEADLINE_SOON)
        ),
        GovtJob(
            id = 8,
            organization = "Bangladesh Army",
            organizationBn = "বাংলাদেশ সেনাবাহিনী",
            title = "Captain (General Duty)",
            titleBn = "ক্যাপ্টেন (জেনারেল ডিউটি)",
            logoEmoji = "🎖️",
            publishedDate = "06 Aug 2026",
            deadline = "25 Aug 2026",
            vacancies = 40,
            websiteUrl = "https://army.mil.bd",
            tags = setOf(JobTag.DEFENSE)
        )
    )
}
