package com.ayybay.app.data.local

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.ui.graphics.vector.ImageVector
import com.ayybay.app.presentation.util.BmiCategory

enum class TipGroup { DIET, EXERCISE, SLEEP, HABIT }

data class FitnessTip(
    val titleEn: String,
    val titleBn: String,
    val bodyEn: String,
    val bodyBn: String,
    val icon: ImageVector,
    val group: TipGroup
)

private enum class AgeBand { TEEN, ADULT, MIDLIFE, SENIOR }

/** General bilingual wellness guidance, not medical advice. */
object FitnessTipData {

    fun tipsFor(category: BmiCategory, ageYears: Int?): List<FitnessTip> {
        val ageBand = when {
            ageYears == null -> AgeBand.ADULT
            ageYears < 18 -> AgeBand.TEEN
            ageYears < 40 -> AgeBand.ADULT
            ageYears < 60 -> AgeBand.MIDLIFE
            else -> AgeBand.SENIOR
        }
        return commonTips() + categoryTips(category) + ageBandTips(ageBand)
    }

    private fun commonTips(): List<FitnessTip> = listOf(
        FitnessTip(
            "Stay hydrated", "পর্যাপ্ত পানি পান করুন",
            "Drink at least 8 glasses of water a day, more in Dhaka's heat and humidity.",
            "দিনে অন্তত ৮ গ্লাস পানি পান করুন, ঢাকার গরম ও আর্দ্রতায় আরও বেশি প্রয়োজন।",
            Icons.Default.Restaurant, TipGroup.HABIT
        ),
        FitnessTip(
            "Sleep 7–8 hours", "৭-৮ ঘণ্টা ঘুমান",
            "Consistent sleep helps regulate appetite and recovery. Avoid heavy meals late at night.",
            "নিয়মিত ঘুম ক্ষুধা ও পুনরুদ্ধার নিয়ন্ত্রণে সাহায্য করে। রাতে দেরিতে ভারী খাবার এড়িয়ে চলুন।",
            Icons.Default.Bedtime, TipGroup.SLEEP
        )
    )

    private fun categoryTips(category: BmiCategory): List<FitnessTip> = when (category) {
        BmiCategory.UNDERWEIGHT -> listOf(
            FitnessTip(
                "Eat more frequently", "ঘনঘন খাবার খান",
                "Add 2–3 nutrient-dense snacks between meals — nuts, dates, milk, banana.",
                "দুই বেলা খাবারের মাঝে ২-৩ বার পুষ্টিকর নাশতা খান — বাদাম, খেজুর, দুধ, কলা।",
                Icons.Default.Restaurant, TipGroup.DIET
            ),
            FitnessTip(
                "Add protein & healthy fats", "প্রোটিন ও স্বাস্থ্যকর চর্বি যোগ করুন",
                "Include dal, eggs, fish, and ghee to build healthy weight.",
                "স্বাস্থ্যকর ওজন বাড়াতে ডাল, ডিম, মাছ ও ঘি যোগ করুন।",
                Icons.Default.Restaurant, TipGroup.DIET
            ),
            FitnessTip(
                "Strength training", "শক্তি বৃদ্ধির ব্যায়াম",
                "Light resistance exercises 2–3 times a week help build muscle, not just fat.",
                "সপ্তাহে ২-৩ বার হালকা রেজিস্ট্যান্স ব্যায়াম মাংসপেশি গঠনে সাহায্য করে।",
                Icons.Default.FitnessCenter, TipGroup.EXERCISE
            )
        )
        BmiCategory.NORMAL -> listOf(
            FitnessTip(
                "Maintain with balance", "ভারসাম্য বজায় রাখুন",
                "Keep a balanced plate: half vegetables, quarter protein, quarter whole grains.",
                "সুষম প্লেট রাখুন: অর্ধেক সবজি, চতুর্থাংশ প্রোটিন, চতুর্থাংশ পূর্ণ শস্য।",
                Icons.Default.Restaurant, TipGroup.DIET
            ),
            FitnessTip(
                "Stay active", "সক্রিয় থাকুন",
                "Aim for 150 minutes of moderate activity a week — walking, cycling, or swimming.",
                "সপ্তাহে অন্তত ১৫০ মিনিট মাঝারি সক্রিয়তা — হাঁটা, সাইক্লিং, বা সাঁতার।",
                Icons.AutoMirrored.Filled.DirectionsWalk, TipGroup.EXERCISE
            )
        )
        BmiCategory.OVERWEIGHT -> listOf(
            FitnessTip(
                "Cut sugary drinks", "চিনিযুক্ত পানীয় কমান",
                "Replace soft drinks with lebur pani (lemon water) or plain water.",
                "কোমল পানীয়ের বদলে লেবুর পানি বা পানি পান করুন।",
                Icons.Default.Restaurant, TipGroup.DIET
            ),
            FitnessTip(
                "Brisk walking", "দ্রুত হাঁটা",
                "Aim for 150+ minutes of brisk walking per week, split into 20–30 minute sessions.",
                "সপ্তাহে ১৫০+ মিনিট দ্রুত হাঁটার লক্ষ্য রাখুন, ২০-৩০ মিনিটের ভাগে ভাগ করে।",
                Icons.AutoMirrored.Filled.DirectionsWalk, TipGroup.EXERCISE
            ),
            FitnessTip(
                "Portion control", "খাবারের পরিমাণ নিয়ন্ত্রণ",
                "Use a smaller plate and stop eating at 80% full.",
                "ছোট প্লেট ব্যবহার করুন এবং ৮০% ভরা অনুভব করলে খাওয়া বন্ধ করুন।",
                Icons.Default.Restaurant, TipGroup.HABIT
            )
        )
        BmiCategory.OBESE -> listOf(
            FitnessTip(
                "Start with walking", "হাঁটা দিয়ে শুরু করুন",
                "Begin with 10–15 minute walks after meals and build up gradually.",
                "খাবারের পর ১০-১৫ মিনিট হাঁটা দিয়ে শুরু করুন এবং ধীরে ধীরে বাড়ান।",
                Icons.AutoMirrored.Filled.DirectionsWalk, TipGroup.EXERCISE
            ),
            FitnessTip(
                "Reduce refined carbs", "পরিশোধিত শর্করা কমান",
                "Cut down on white rice/maida-based snacks; favor red rice, vegetables, and dal.",
                "সাদা ভাত/ময়দার নাশতা কমান; লাল চাল, সবজি ও ডাল বেশি খান।",
                Icons.Default.Restaurant, TipGroup.DIET
            ),
            FitnessTip(
                "Consult a doctor", "ডাক্তারের পরামর্শ নিন",
                "For a BMI this high, a doctor or dietitian can build a plan suited to you.",
                "এত বেশি বিএমআইয়ের জন্য একজন ডাক্তার বা পুষ্টিবিদ আপনার উপযোগী পরিকল্পনা করতে পারবেন।",
                Icons.Default.FitnessCenter, TipGroup.HABIT
            )
        )
    }

    private fun ageBandTips(ageBand: AgeBand): List<FitnessTip> = when (ageBand) {
        AgeBand.SENIOR -> listOf(
            FitnessTip(
                "Gentle, joint-friendly exercise", "কোমল, জয়েন্ট-বান্ধব ব্যায়াম",
                "Favor walking, stretching, and swimming over high-impact workouts.",
                "উচ্চ-প্রভাবযুক্ত ব্যায়ামের বদলে হাঁটা, স্ট্রেচিং ও সাঁতার বেছে নিন।",
                Icons.Default.FitnessCenter, TipGroup.EXERCISE
            )
        )
        AgeBand.TEEN -> listOf(
            FitnessTip(
                "Focus on growth, not dieting", "ডায়েটের বদলে বৃদ্ধিতে মনোযোগ দিন",
                "Growing bodies need balanced nutrition — avoid strict calorie-cutting at this age.",
                "বাড়ন্ত শরীরের সুষম পুষ্টি দরকার — এই বয়সে কড়া ক্যালরি কমানো এড়িয়ে চলুন।",
                Icons.Default.Restaurant, TipGroup.DIET
            )
        )
        else -> emptyList()
    }
}
