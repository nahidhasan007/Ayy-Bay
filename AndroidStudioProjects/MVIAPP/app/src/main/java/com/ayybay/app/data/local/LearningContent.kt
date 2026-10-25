package com.ayybay.app.data.local

import com.ayybay.app.domain.model.LearningCategory
import com.ayybay.app.domain.model.LearningItem
import com.ayybay.app.domain.model.QuizQuestion

/**
 * Bundled Daily Learning content. Each category rotates by calendar day, so everyone sees the same
 * lesson on the same date and nothing needs a network. To add content, append to a list below: the
 * rotation adapts to the list size automatically (ids must stay unique and stable, since learned
 * progress is stored against them).
 */
object LearningContent {

    const val QUIZ_QUESTIONS_PER_DAY = 5

    fun itemsFor(category: LearningCategory): List<LearningItem> = when (category) {
        LearningCategory.ISLAMIC -> islamic
        LearningCategory.VOCABULARY -> vocabulary
        LearningCategory.GENERAL_KNOWLEDGE -> generalKnowledge
    }

    /** Today's lesson for [category]; [epochDay] is days since 1970-01-01 in the device's time zone. */
    fun itemFor(category: LearningCategory, epochDay: Long): LearningItem {
        val list = itemsFor(category)
        return list[Math.floorMod(epochDay, list.size.toLong()).toInt()]
    }

    /** Today's lessons, one per category, in display order. */
    fun todaysItems(epochDay: Long): List<LearningItem> =
        LearningCategory.entries.map { itemFor(it, epochDay) }

    /**
     * Today's quiz: a sliding window over the question pool, advancing by [count] each day so every
     * question comes around before any repeats.
     */
    fun quizFor(epochDay: Long, count: Int = QUIZ_QUESTIONS_PER_DAY): List<QuizQuestion> {
        val size = quiz.size
        val start = Math.floorMod(epochDay * count, size.toLong()).toInt()
        return List(minOf(count, size)) { quiz[(start + it) % size] }
    }

    private fun islamicItem(
        id: String, tagEn: String, tagBn: String, reference: String,
        arabic: String, transliteration: String, meaningEn: String, meaningBn: String
    ) = LearningItem(
        id = id, category = LearningCategory.ISLAMIC, tagEn = tagEn, tagBn = tagBn,
        titleEn = reference, titleBn = reference, meaningEn = meaningEn, meaningBn = meaningBn,
        arabic = arabic, subline = transliteration, reference = reference
    )

    private fun word(
        id: String, word: String, pronunciation: String, partOfSpeechEn: String, partOfSpeechBn: String,
        meaningEn: String, meaningBn: String, exampleEn: String, exampleBn: String
    ) = LearningItem(
        id = id, category = LearningCategory.VOCABULARY, tagEn = partOfSpeechEn, tagBn = partOfSpeechBn,
        titleEn = word, titleBn = word, meaningEn = meaningEn, meaningBn = meaningBn,
        subline = pronunciation, noteEn = exampleEn, noteBn = exampleBn
    )

    private fun gk(
        id: String, topicEn: String, topicBn: String,
        questionEn: String, questionBn: String,
        answerEn: String, answerBn: String, factEn: String, factBn: String
    ) = LearningItem(
        id = id, category = LearningCategory.GENERAL_KNOWLEDGE, tagEn = topicEn, tagBn = topicBn,
        titleEn = questionEn, titleBn = questionBn, meaningEn = answerEn, meaningBn = answerBn,
        noteEn = factEn, noteBn = factBn
    )

    private fun question(
        id: String, questionEn: String, questionBn: String,
        optionsEn: List<String>, optionsBn: List<String>, correctIndex: Int,
        explanationEn: String, explanationBn: String
    ) = QuizQuestion(id, questionEn, questionBn, optionsEn, optionsBn, correctIndex, explanationEn, explanationBn)

    private val islamic: List<LearningItem> = listOf(
        islamicItem(
            "isl_94_5", "Ayah", "আয়াত", "Quran 94:5-6",
            "فَإِنَّ مَعَ الْعُسْرِ يُسْرًا ۝ إِنَّ مَعَ الْعُسْرِ يُسْرًا",
            "Fa inna ma'al-'usri yusra. Inna ma'al-'usri yusra.",
            "So, surely with hardship comes ease. Surely with hardship comes ease.",
            "নিশ্চয়ই কষ্টের সাথেই স্বস্তি আছে। নিশ্চয়ই কষ্টের সাথেই স্বস্তি আছে।"
        ),
        islamicItem(
            "isl_13_28", "Ayah", "আয়াত", "Quran 13:28",
            "أَلَا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ",
            "Ala bi-dhikrillahi tatma'innul-qulub.",
            "Verily, in the remembrance of Allah do hearts find rest.",
            "জেনে রাখো, আল্লাহর স্মরণেই অন্তর প্রশান্ত হয়।"
        ),
        islamicItem(
            "isl_2_286", "Ayah", "আয়াত", "Quran 2:286",
            "لَا يُكَلِّفُ اللَّهُ نَفْسًا إِلَّا وُسْعَهَا",
            "La yukallifullahu nafsan illa wus'aha.",
            "Allah does not burden a soul beyond what it can bear.",
            "আল্লাহ কারও ওপর তার সাধ্যের অতিরিক্ত দায়িত্ব চাপান না।"
        ),
        islamicItem(
            "isl_2_153", "Ayah", "আয়াত", "Quran 2:153",
            "إِنَّ اللَّهَ مَعَ الصَّابِرِينَ",
            "Innallaha ma'as-sabirin.",
            "Indeed, Allah is with the patient.",
            "নিশ্চয়ই আল্লাহ ধৈর্যশীলদের সাথে আছেন।"
        ),
        islamicItem(
            "isl_14_7", "Ayah", "আয়াত", "Quran 14:7",
            "لَئِن شَكَرْتُمْ لَأَزِيدَنَّكُمْ",
            "La'in shakartum la-azidannakum.",
            "If you are grateful, I will surely increase you [in favor].",
            "যদি তোমরা কৃতজ্ঞ হও, তবে অবশ্যই আমি তোমাদের (নিয়ামত) বাড়িয়ে দেব।"
        ),
        islamicItem(
            "isl_65_3", "Ayah", "আয়াত", "Quran 65:3",
            "وَمَن يَتَوَكَّلْ عَلَى اللَّهِ فَهُوَ حَسْبُهُ",
            "Wa man yatawakkal 'alallahi fa-huwa hasbuh.",
            "Whoever relies upon Allah, He is sufficient for him.",
            "যে আল্লাহর ওপর ভরসা করে, তার জন্য তিনিই যথেষ্ট।"
        ),
        islamicItem(
            "isl_40_60", "Ayah", "আয়াত", "Quran 40:60",
            "ادْعُونِي أَسْتَجِبْ لَكُمْ",
            "Ud'uni astajib lakum.",
            "Call upon Me; I will respond to you.",
            "তোমরা আমাকে ডাকো, আমি তোমাদের ডাকে সাড়া দেব।"
        ),
        islamicItem(
            "isl_112", "Surah", "সূরা", "Quran 112:1-4 (Al-Ikhlas)",
            "قُلْ هُوَ اللَّهُ أَحَدٌ ۝ اللَّهُ الصَّمَدُ ۝ لَمْ يَلِدْ وَلَمْ يُولَدْ ۝ وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ",
            "Qul huwallahu ahad. Allahus-samad. Lam yalid wa lam yulad. Wa lam yakun lahu kufuwan ahad.",
            "Say: He is Allah, the One. Allah, the Eternal Refuge. He neither begets nor is born, nor is there anyone equal to Him.",
            "বলুন, তিনি আল্লাহ, এক। আল্লাহ অমুখাপেক্ষী। তিনি কাউকে জন্ম দেননি এবং কেউ তাঁকে জন্ম দেয়নি। আর তাঁর সমকক্ষ কেউ নেই।"
        ),
        islamicItem(
            "isl_dua_parents", "Dua", "দোয়া", "Quran 17:24",
            "رَبِّ ارْحَمْهُمَا كَمَا رَبَّيَانِي صَغِيرًا",
            "Rabbir-hamhuma kama rabbayani saghira.",
            "My Lord, have mercy on them as they raised me when I was small.",
            "হে আমার রব, তাঁদের প্রতি দয়া করুন, যেমন তাঁরা শৈশবে আমাকে লালনপালন করেছেন।"
        ),
        islamicItem(
            "isl_dua_knowledge", "Dua", "দোয়া", "Quran 20:114",
            "رَبِّ زِدْنِي عِلْمًا",
            "Rabbi zidni 'ilma.",
            "My Lord, increase me in knowledge.",
            "হে আমার রব, আমার জ্ঞান বৃদ্ধি করে দিন।"
        ),
        islamicItem(
            "isl_dua_hasanah", "Dua", "দোয়া", "Quran 2:201",
            "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الْآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ",
            "Rabbana atina fid-dunya hasanatan wa fil-akhirati hasanatan wa qina 'adhaban-nar.",
            "Our Lord, give us good in this world and good in the Hereafter, and protect us from the punishment of the Fire.",
            "হে আমাদের রব, আমাদের দুনিয়াতে কল্যাণ দিন, আখিরাতেও কল্যাণ দিন এবং আমাদের জাহান্নামের আযাব থেকে রক্ষা করুন।"
        ),
        islamicItem(
            "isl_dua_yunus", "Dua", "দোয়া", "Quran 21:87",
            "لَا إِلَهَ إِلَّا أَنتَ سُبْحَانَكَ إِنِّي كُنتُ مِنَ الظَّالِمِينَ",
            "La ilaha illa anta subhanaka inni kuntu minaz-zalimin.",
            "There is no god but You; glory be to You! Indeed, I have been among the wrongdoers.",
            "আপনি ছাড়া কোনো ইলাহ নেই, আপনি পবিত্র; নিশ্চয়ই আমি জালিমদের অন্তর্ভুক্ত ছিলাম।"
        ),
        islamicItem(
            "isl_h_intentions", "Hadith", "হাদিস", "Sahih al-Bukhari 1 · Sahih Muslim 1907",
            "إِنَّمَا الْأَعْمَالُ بِالنِّيَّاتِ",
            "Innamal-a'malu bin-niyyat.",
            "Actions are judged by intentions.",
            "কাজের ফলাফল নিয়তের ওপর নির্ভরশীল।"
        ),
        islamicItem(
            "isl_h_learn_quran", "Hadith", "হাদিস", "Sahih al-Bukhari 5027",
            "خَيْرُكُمْ مَنْ تَعَلَّمَ الْقُرْآنَ وَعَلَّمَهُ",
            "Khairukum man ta'allamal-Qur'ana wa 'allamah.",
            "The best of you are those who learn the Quran and teach it.",
            "তোমাদের মধ্যে সর্বোত্তম সেই ব্যক্তি, যে কুরআন শেখে এবং শেখায়।"
        ),
        islamicItem(
            "isl_h_smile", "Hadith", "হাদিস", "Jami at-Tirmidhi 1956",
            "تَبَسُّمُكَ فِي وَجْهِ أَخِيكَ لَكَ صَدَقَةٌ",
            "Tabassumuka fi wajhi akhika laka sadaqah.",
            "Your smile in the face of your brother is charity.",
            "তোমার ভাইয়ের সামনে তোমার হাসি তোমার জন্য সদকা।"
        ),
        islamicItem(
            "isl_h_love_brother", "Hadith", "হাদিস", "Sahih al-Bukhari 13 · Sahih Muslim 45",
            "لَا يُؤْمِنُ أَحَدُكُمْ حَتَّى يُحِبَّ لِأَخِيهِ مَا يُحِبُّ لِنَفْسِهِ",
            "La yu'minu ahadukum hatta yuhibba li-akhihi ma yuhibbu li-nafsih.",
            "None of you truly believes until he loves for his brother what he loves for himself.",
            "তোমাদের কেউ প্রকৃত মুমিন হতে পারবে না, যতক্ষণ না সে তার ভাইয়ের জন্য তা-ই ভালোবাসে যা নিজের জন্য ভালোবাসে।"
        ),
        islamicItem(
            "isl_h_cleanliness", "Hadith", "হাদিস", "Sahih Muslim 223",
            "الطُّهُورُ شَطْرُ الْإِيمَانِ",
            "At-tuhuru shatrul-iman.",
            "Cleanliness is half of faith.",
            "পবিত্রতা ঈমানের অর্ধেক।"
        ),
        islamicItem(
            "isl_h_good_speech", "Hadith", "হাদিস", "Sahih al-Bukhari 6018 · Sahih Muslim 47",
            "مَنْ كَانَ يُؤْمِنُ بِاللَّهِ وَالْيَوْمِ الْآخِرِ فَلْيَقُلْ خَيْرًا أَوْ لِيَصْمُتْ",
            "Man kana yu'minu billahi wal-yawmil-akhiri falyaqul khairan aw liyasmut.",
            "Whoever believes in Allah and the Last Day, let him speak good or remain silent.",
            "যে আল্লাহ ও শেষ দিবসে বিশ্বাস রাখে, সে যেন ভালো কথা বলে অথবা চুপ থাকে।"
        ),
        islamicItem(
            "isl_h_anger", "Hadith", "হাদিস", "Sahih al-Bukhari 6114 · Sahih Muslim 2609",
            "لَيْسَ الشَّدِيدُ بِالصُّرَعَةِ، إِنَّمَا الشَّدِيدُ الَّذِي يَمْلِكُ نَفْسَهُ عِنْدَ الْغَضَبِ",
            "Laysash-shadidu bis-sura'ah, innamash-shadidul-ladhi yamliku nafsahu 'indal-ghadab.",
            "The strong person is not the one who overcomes others by strength, but the one who controls himself in anger.",
            "প্রকৃত শক্তিশালী সে নয় যে কুস্তিতে অন্যকে হারায়; বরং সে-ই শক্তিশালী যে রাগের সময় নিজেকে নিয়ন্ত্রণ করে।"
        ),
        islamicItem(
            "isl_dua_rain", "Dua", "দোয়া", "Sahih al-Bukhari 1032",
            "اللَّهُمَّ صَيِّبًا نَافِعًا",
            "Allahumma sayyiban nafi'a.",
            "O Allah, make it a beneficial rain. (Said when it rains.)",
            "হে আল্লাহ, একে কল্যাণকর বৃষ্টি বানিয়ে দিন। (বৃষ্টির সময় পড়ার দোয়া।)"
        ),
        islamicItem(
            "isl_dua_waking", "Dua", "দোয়া", "Sahih al-Bukhari 6312",
            "الْحَمْدُ لِلَّهِ الَّذِي أَحْيَانَا بَعْدَ مَا أَمَاتَنَا وَإِلَيْهِ النُّشُورُ",
            "Alhamdu lillahil-ladhi ahyana ba'da ma amatana wa ilayhin-nushur.",
            "All praise is for Allah who gave us life after causing us to die, and to Him is the resurrection. (Said on waking.)",
            "সমস্ত প্রশংসা আল্লাহর, যিনি আমাদের মৃত্যুর (ঘুমের) পর জীবন দান করেছেন এবং তাঁর কাছেই ফিরে যেতে হবে। (ঘুম থেকে উঠে পড়ার দোয়া।)"
        )
    )

    private val vocabulary: List<LearningItem> = listOf(
        word(
            "voc_diligent", "Diligent", "DIL-uh-juhnt", "Adjective", "বিশেষণ",
            "Showing steady, careful effort in one's work.", "পরিশ্রমী; অধ্যবসায়ী",
            "She is a diligent student who never skips her revision.",
            "সে একজন পরিশ্রমী ছাত্রী, যে কখনো পড়া ঝালাই বাদ দেয় না।"
        ),
        word(
            "voc_resilient", "Resilient", "ri-ZIL-yuhnt", "Adjective", "বিশেষণ",
            "Able to recover quickly from difficulties.", "দ্রুত ঘুরে দাঁড়াতে সক্ষম; স্থিতিস্থাপক",
            "Farmers here are resilient and replant quickly after every flood.",
            "এখানকার কৃষকেরা স্থিতিস্থাপক; প্রতিটি বন্যার পর দ্রুত আবার চাষ শুরু করেন।"
        ),
        word(
            "voc_candid", "Candid", "KAN-did", "Adjective", "বিশেষণ",
            "Honest and straightforward, even when it is uncomfortable.", "অকপট; স্পষ্টবাদী",
            "He gave a candid answer to the interview panel.",
            "সে ইন্টারভিউ বোর্ডকে একটি অকপট উত্তর দিয়েছে।"
        ),
        word(
            "voc_abundant", "Abundant", "uh-BUHN-duhnt", "Adjective", "বিশেষণ",
            "Existing in large quantities; plentiful.", "প্রচুর; পর্যাপ্ত",
            "Fish are abundant in the rivers of the delta.",
            "ব-দ্বীপের নদীগুলোতে মাছ প্রচুর।"
        ),
        word(
            "voc_benevolent", "Benevolent", "buh-NEV-uh-luhnt", "Adjective", "বিশেষণ",
            "Kind and generous toward others.", "হিতৈষী; দয়ালু",
            "The benevolent donor paid for the school's new library.",
            "দয়ালু দাতা স্কুলের নতুন গ্রন্থাগারের খরচ বহন করেছেন।"
        ),
        word(
            "voc_meticulous", "Meticulous", "muh-TIK-yuh-luhs", "Adjective", "বিশেষণ",
            "Showing great attention to detail; very careful.", "অত্যন্ত যত্নশীল; নিখুঁত",
            "The accountant was meticulous about every entry in the ledger.",
            "হিসাবরক্ষক খাতার প্রতিটি এন্ট্রি নিয়ে অত্যন্ত যত্নশীল ছিলেন।"
        ),
        word(
            "voc_pragmatic", "Pragmatic", "prag-MAT-ik", "Adjective", "বিশেষণ",
            "Dealing with things sensibly and realistically.", "বাস্তববাদী",
            "A pragmatic approach to budgeting keeps spending under control.",
            "বাজেটের বাস্তববাদী পদ্ধতি খরচ নিয়ন্ত্রণে রাখে।"
        ),
        word(
            "voc_eloquent", "Eloquent", "EL-uh-kwuhnt", "Adjective", "বিশেষণ",
            "Fluent and persuasive in speaking or writing.", "বাকপটু; সুবক্তা",
            "Her eloquent speech moved the whole audience.",
            "তাঁর বাকপটু বক্তৃতা পুরো শ্রোতাকে মুগ্ধ করেছে।"
        ),
        word(
            "voc_integrity", "Integrity", "in-TEG-ruh-tee", "Noun", "বিশেষ্য",
            "The quality of being honest and having strong moral principles.", "সততা; নৈতিক দৃঢ়তা",
            "A public servant must act with integrity.",
            "একজন সরকারি কর্মচারীকে অবশ্যই সততার সাথে কাজ করতে হয়।"
        ),
        word(
            "voc_ambiguous", "Ambiguous", "am-BIG-yoo-uhs", "Adjective", "বিশেষণ",
            "Open to more than one meaning; unclear.", "দ্ব্যর্থক; অস্পষ্ট",
            "The instructions were ambiguous, so everyone understood them differently.",
            "নির্দেশনাটি অস্পষ্ট ছিল, তাই সবাই আলাদাভাবে বুঝেছে।"
        ),
        word(
            "voc_persevere", "Persevere", "pur-suh-VEER", "Verb", "ক্রিয়া",
            "To keep trying despite difficulty or delay.", "অধ্যবসায়ের সাথে লেগে থাকা",
            "If you persevere with your studies, success will follow.",
            "পড়াশোনায় লেগে থাকলে সাফল্য আসবেই।"
        ),
        word(
            "voc_inevitable", "Inevitable", "in-EV-i-tuh-buhl", "Adjective", "বিশেষণ",
            "Certain to happen; unavoidable.", "অনিবার্য",
            "With so much rain, flooding was inevitable.",
            "এত বৃষ্টিতে বন্যা ছিল অনিবার্য।"
        ),
        word(
            "voc_humble", "Humble", "HUHM-buhl", "Adjective", "বিশেষণ",
            "Not proud; modest about one's own importance.", "বিনয়ী; নম্র",
            "Despite his success, he remained humble.",
            "সাফল্য সত্ত্বেও সে বিনয়ীই থেকেছে।"
        ),
        word(
            "voc_frugal", "Frugal", "FROO-guhl", "Adjective", "বিশেষণ",
            "Careful about spending; thrifty.", "মিতব্যয়ী",
            "They lead a frugal life and save a part of every salary.",
            "তারা মিতব্যয়ী জীবনযাপন করে এবং প্রতিটি বেতনের একটি অংশ সঞ্চয় করে।"
        ),
        word(
            "voc_vital", "Vital", "VY-tl", "Adjective", "বিশেষণ",
            "Absolutely necessary or important.", "অত্যাবশ্যক; অপরিহার্য",
            "Clean water is vital for good health.",
            "ভালো স্বাস্থ্যের জন্য বিশুদ্ধ পানি অত্যাবশ্যক।"
        ),
        word(
            "voc_contemplate", "Contemplate", "KON-tuhm-playt", "Verb", "ক্রিয়া",
            "To think deeply and carefully about something.", "গভীরভাবে চিন্তা করা",
            "He sat quietly and contemplated his next step.",
            "সে চুপচাপ বসে তার পরবর্তী পদক্ষেপ নিয়ে গভীরভাবে ভাবল।"
        ),
        word(
            "voc_sustain", "Sustain", "suh-STAYN", "Verb", "ক্রিয়া",
            "To keep something going over time; to support.", "টিকিয়ে রাখা; ধারণ করা",
            "Regular exercise helps sustain good health.",
            "নিয়মিত ব্যায়াম ভালো স্বাস্থ্য টিকিয়ে রাখতে সাহায্য করে।"
        ),
        word(
            "voc_obsolete", "Obsolete", "ob-suh-LEET", "Adjective", "বিশেষণ",
            "No longer used because something newer has replaced it.", "অচল; সেকেলে",
            "Film cameras have become almost obsolete.",
            "ফিল্ম ক্যামেরা প্রায় অচল হয়ে গেছে।"
        ),
        word(
            "voc_tranquil", "Tranquil", "TRANG-kwil", "Adjective", "বিশেষণ",
            "Calm, peaceful and free from disturbance.", "শান্ত; নিস্তরঙ্গ",
            "The village is tranquil in the early morning.",
            "ভোরবেলায় গ্রামটি শান্ত থাকে।"
        ),
        word(
            "voc_endeavour", "Endeavour", "en-DEV-er", "Noun", "বিশেষ্য",
            "A serious, determined attempt to achieve something.", "প্রচেষ্টা; উদ্যোগ",
            "Learning a new language is a worthwhile endeavour.",
            "নতুন ভাষা শেখা একটি মূল্যবান প্রচেষ্টা।"
        )
    )

    private val generalKnowledge: List<LearningItem> = listOf(
        gk(
            "gk_flower", "Bangladesh", "বাংলাদেশ",
            "What is the national flower of Bangladesh?", "বাংলাদেশের জাতীয় ফুল কোনটি?",
            "Shapla (water lily)", "শাপলা",
            "It grows in ponds and wetlands across the country.", "এটি দেশজুড়ে পুকুর ও জলাশয়ে জন্মায়।"
        ),
        gk(
            "gk_fruit", "Bangladesh", "বাংলাদেশ",
            "What is the national fruit of Bangladesh?", "বাংলাদেশের জাতীয় ফল কোনটি?",
            "Kathal (jackfruit)", "কাঁঠাল",
            "Jackfruit is among the largest tree-borne fruits in the world.", "কাঁঠাল পৃথিবীর বৃহত্তম গাছে-ফলা ফলগুলোর একটি।"
        ),
        gk(
            "gk_fish", "Bangladesh", "বাংলাদেশ",
            "What is the national fish of Bangladesh?", "বাংলাদেশের জাতীয় মাছ কোনটি?",
            "Ilish (hilsa)", "ইলিশ",
            "Bangladesh produces most of the world's hilsa.", "বিশ্বের অধিকাংশ ইলিশ বাংলাদেশে উৎপাদিত হয়।"
        ),
        gk(
            "gk_bird", "Bangladesh", "বাংলাদেশ",
            "What is the national bird of Bangladesh?", "বাংলাদেশের জাতীয় পাখি কোনটি?",
            "Doel (Oriental magpie-robin)", "দোয়েল",
            "It is known for its sweet song.", "মিষ্টি সুরের জন্য এটি পরিচিত।"
        ),
        gk(
            "gk_animal", "Bangladesh", "বাংলাদেশ",
            "What is the national animal of Bangladesh?", "বাংলাদেশের জাতীয় পশু কোনটি?",
            "Royal Bengal tiger", "রয়েল বেঙ্গল টাইগার",
            "The Sundarbans is home to the largest wild population of these tigers in Bangladesh.", "বাংলাদেশে এই বাঘের সবচেয়ে বড় বন্য আবাস সুন্দরবন।"
        ),
        gk(
            "gk_mother_language", "History", "ইতিহাস",
            "Which day is International Mother Language Day?", "আন্তর্জাতিক মাতৃভাষা দিবস কবে?",
            "21 February", "২১ ফেব্রুয়ারি",
            "UNESCO proclaimed it in 1999 in honour of the 1952 Language Movement in Bangladesh.", "১৯৫২ সালের ভাষা আন্দোলনের স্মরণে ১৯৯৯ সালে ইউনেস্কো দিনটি ঘোষণা করে।"
        ),
        gk(
            "gk_days", "History", "ইতিহাস",
            "When are Independence Day and Victory Day of Bangladesh?", "বাংলাদেশের স্বাধীনতা দিবস ও বিজয় দিবস কবে?",
            "26 March and 16 December", "২৬ মার্চ ও ১৬ ডিসেম্বর",
            "Both commemorate events of 1971.", "দুটি দিনই ১৯৭১ সালের ঘটনার স্মারক।"
        ),
        gk(
            "gk_constitution", "History", "ইতিহাস",
            "When was the Constitution of Bangladesh adopted?", "বাংলাদেশের সংবিধান কবে গৃহীত হয়?",
            "4 November 1972", "৪ নভেম্বর ১৯৭২",
            "It came into effect on 16 December 1972.", "এটি কার্যকর হয় ১৯৭২ সালের ১৬ ডিসেম্বর।"
        ),
        gk(
            "gk_un", "History", "ইতিহাস",
            "When did Bangladesh become a member of the United Nations?", "বাংলাদেশ কবে জাতিসংঘের সদস্য হয়?",
            "17 September 1974", "১৭ সেপ্টেম্বর ১৯৭৪",
            "The UN itself was founded in 1945.", "জাতিসংঘ নিজে প্রতিষ্ঠিত হয় ১৯৪৫ সালে।"
        ),
        gk(
            "gk_beach", "Geography", "ভূগোল",
            "Which is Bangladesh's famous long sea beach?", "বাংলাদেশের বিখ্যাত দীর্ঘ সমুদ্রসৈকত কোনটি?",
            "Cox's Bazar", "কক্সবাজার",
            "It stretches for about 120 km along the Bay of Bengal.", "বঙ্গোপসাগর বরাবর এটি প্রায় ১২০ কিলোমিটার বিস্তৃত।"
        ),
        gk(
            "gk_sundarbans", "Geography", "ভূগোল",
            "Which is the largest mangrove forest in the world?", "বিশ্বের বৃহত্তম ম্যানগ্রোভ বন কোনটি?",
            "The Sundarbans", "সুন্দরবন",
            "It was listed as a UNESCO World Heritage Site in 1997.", "১৯৯৭ সালে এটি ইউনেস্কোর বিশ্ব ঐতিহ্যবাহী স্থান হিসেবে তালিকাভুক্ত হয়।"
        ),
        gk(
            "gk_unesco", "Heritage", "ঐতিহ্য",
            "Name the three UNESCO World Heritage Sites of Bangladesh.", "বাংলাদেশের তিনটি ইউনেস্কো বিশ্ব ঐতিহ্যবাহী স্থানের নাম বলো।",
            "Sundarbans, Mosque City of Bagerhat, Paharpur Buddhist Vihara", "সুন্দরবন, বাগেরহাটের মসজিদ শহর, পাহাড়পুর বৌদ্ধ বিহার",
            "Bagerhat and Paharpur were listed in 1985.", "বাগেরহাট ও পাহাড়পুর ১৯৮৫ সালে তালিকাভুক্ত হয়।"
        ),
        gk(
            "gk_padma", "Bangladesh", "বাংলাদেশ",
            "When was the Padma Bridge opened to traffic?", "পদ্মা সেতু কবে যান চলাচলের জন্য খুলে দেওয়া হয়?",
            "25 June 2022", "২৫ জুন ২০২২",
            "It links the south-west of the country with Dhaka and the north-east.", "এটি দেশের দক্ষিণ-পশ্চিমাঞ্চলকে ঢাকা ও উত্তর-পূর্বাঞ্চলের সাথে যুক্ত করেছে।"
        ),
        gk(
            "gk_taka", "Economy", "অর্থনীতি",
            "What is the currency of Bangladesh and its symbol?", "বাংলাদেশের মুদ্রা ও তার প্রতীক কী?",
            "Taka (৳)", "টাকা (৳)",
            "Banknotes are issued by Bangladesh Bank, the central bank.", "ব্যাংকনোট ইস্যু করে কেন্দ্রীয় ব্যাংক, বাংলাদেশ ব্যাংক।"
        ),
        gk(
            "gk_satellite", "Science & Tech", "বিজ্ঞান ও প্রযুক্তি",
            "What was Bangladesh's first communications satellite called?", "বাংলাদেশের প্রথম যোগাযোগ উপগ্রহের নাম কী?",
            "Bangabandhu Satellite-1", "বঙ্গবন্ধু স্যাটেলাইট-১",
            "It was launched on 12 May 2018.", "এটি উৎক্ষেপণ করা হয় ২০১৮ সালের ১২ মে।"
        ),
        gk(
            "gk_everest", "World", "বিশ্ব",
            "What is the height of Mount Everest, the highest mountain above sea level?", "সমুদ্রপৃষ্ঠ থেকে সর্বোচ্চ পর্বত মাউন্ট এভারেস্টের উচ্চতা কত?",
            "8,849 metres", "৮,৮৪৯ মিটার",
            "Nepal and China jointly announced this figure in 2020.", "নেপাল ও চীন যৌথভাবে ২০২০ সালে এই পরিমাপ ঘোষণা করে।"
        ),
        gk(
            "gk_jupiter", "Science", "বিজ্ঞান",
            "Which is the largest planet in our solar system?", "আমাদের সৌরজগতের বৃহত্তম গ্রহ কোনটি?",
            "Jupiter", "বৃহস্পতি",
            "More than 1,300 Earths could fit inside it.", "এর ভেতরে ১,৩০০-এর বেশি পৃথিবী এঁটে যাবে।"
        ),
        gk(
            "gk_bones", "Science", "বিজ্ঞান",
            "How many bones does an adult human body have?", "প্রাপ্তবয়স্ক মানুষের দেহে কতটি হাড় থাকে?",
            "206", "২০৬",
            "Babies are born with around 300 softer bones that gradually fuse.", "শিশুরা প্রায় ৩০০টি নরম হাড় নিয়ে জন্মায়, যা ধীরে ধীরে জোড়া লাগে।"
        ),
        gk(
            "gk_pacific", "World", "বিশ্ব",
            "Which is the largest ocean on Earth?", "পৃথিবীর বৃহত্তম মহাসাগর কোনটি?",
            "The Pacific Ocean", "প্রশান্ত মহাসাগর",
            "It covers about one-third of the Earth's surface.", "এটি পৃথিবীর প্রায় এক-তৃতীয়াংশ জুড়ে বিস্তৃত।"
        ),
        gk(
            "gk_light", "Science", "বিজ্ঞান",
            "What is the approximate speed of light?", "আলোর গতিবেগ প্রায় কত?",
            "About 300,000 km per second", "প্রায় ৩,০০,০০০ কিলোমিটার প্রতি সেকেন্ড",
            "Sunlight takes about 8 minutes to reach the Earth.", "সূর্যের আলো পৃথিবীতে পৌঁছাতে প্রায় ৮ মিনিট সময় নেয়।"
        )
    )

    private val quiz: List<QuizQuestion> = listOf(
        question(
            "q_flower", "What is the national flower of Bangladesh?", "বাংলাদেশের জাতীয় ফুল কোনটি?",
            listOf("Rose", "Shapla", "Marigold", "Jasmine"), listOf("গোলাপ", "শাপলা", "গাঁদা", "জুঁই"), 1,
            "The Shapla (water lily) is the national flower.", "শাপলা হলো জাতীয় ফুল।"
        ),
        question(
            "q_mother_language", "On which date is International Mother Language Day observed?", "আন্তর্জাতিক মাতৃভাষা দিবস কোন তারিখে পালিত হয়?",
            listOf("26 March", "21 February", "16 December", "14 April"), listOf("২৬ মার্চ", "২১ ফেব্রুয়ারি", "১৬ ডিসেম্বর", "১৪ এপ্রিল"), 1,
            "It honours the 1952 Language Movement.", "এটি ১৯৫২ সালের ভাষা আন্দোলনকে স্মরণ করে।"
        ),
        question(
            "q_currency", "What is the currency of Bangladesh?", "বাংলাদেশের মুদ্রার নাম কী?",
            listOf("Rupee", "Taka", "Rial", "Ringgit"), listOf("রুপি", "টাকা", "রিয়াল", "রিঙ্গিত"), 1,
            "The Taka (৳) is the official currency of Bangladesh.", "টাকা (৳) বাংলাদেশের সরকারি মুদ্রা।"
        ),
        question(
            "q_mangrove", "Which is the largest mangrove forest in the world?", "বিশ্বের বৃহত্তম ম্যানগ্রোভ বন কোনটি?",
            listOf("Amazon", "Sundarbans", "Daintree", "Bhitarkanika"), listOf("আমাজন", "সুন্দরবন", "ডেইনট্রি", "ভিতরকণিকা"), 1,
            "The Sundarbans spans Bangladesh and India.", "সুন্দরবন বাংলাদেশ ও ভারত জুড়ে বিস্তৃত।"
        ),
        question(
            "q_padma", "In which year did the Padma Bridge open to traffic?", "পদ্মা সেতু কোন বছর যান চলাচলের জন্য খুলে দেওয়া হয়?",
            listOf("2020", "2021", "2022", "2023"), listOf("২০২০", "২০২১", "২০২২", "২০২৩"), 2,
            "It opened on 25 June 2022.", "এটি খুলে দেওয়া হয় ২০২২ সালের ২৫ জুন।"
        ),
        question(
            "q_surahs", "How many surahs are there in the Quran?", "কুরআনে কতটি সূরা আছে?",
            listOf("110", "114", "120", "124"), listOf("১১০", "১১৪", "১২০", "১২৪"), 1,
            "The Quran has 114 surahs.", "কুরআনে ১১৪টি সূরা আছে।"
        ),
        question(
            "q_longest_surah", "Which is the longest surah of the Quran?", "কুরআনের দীর্ঘতম সূরা কোনটি?",
            listOf("Al-Baqarah", "Al-Imran", "An-Nisa", "Al-Kahf"), listOf("আল-বাকারা", "আলে ইমরান", "আন-নিসা", "আল-কাহফ"), 0,
            "Al-Baqarah has 286 verses.", "সূরা আল-বাকারায় ২৮৬টি আয়াত আছে।"
        ),
        question(
            "q_prayers", "How many obligatory prayers are there each day?", "প্রতিদিন কয়টি ফরজ নামাজ আছে?",
            listOf("3", "4", "5", "6"), listOf("৩", "৪", "৫", "৬"), 2,
            "Fajr, Dhuhr, Asr, Maghrib and Isha.", "ফজর, জোহর, আসর, মাগরিব ও ইশা।"
        ),
        question(
            "q_first_month", "Which is the first month of the Islamic (Hijri) calendar?", "হিজরি সনের প্রথম মাস কোনটি?",
            listOf("Ramadan", "Muharram", "Rajab", "Shawwal"), listOf("রমজান", "মুহাররম", "রজব", "শাওয়াল"), 1,
            "The Hijri year begins with Muharram.", "হিজরি বছর মুহাররম দিয়ে শুরু হয়।"
        ),
        question(
            "q_hira", "In which cave did the first revelation come to the Prophet ﷺ?", "নবী ﷺ-এর কাছে প্রথম ওহি কোন গুহায় এসেছিল?",
            listOf("Cave Thawr", "Cave Hira", "Cave Uhud", "Cave Nur-e-Madina"), listOf("সাওর গুহা", "হেরা গুহা", "উহুদ গুহা", "নূরে মদিনা গুহা"), 1,
            "It was in the Cave of Hira on Jabal al-Nour.", "এটি জাবালে নূরের হেরা গুহায় ঘটেছিল।"
        ),
        question(
            "q_diligent", "What does \"diligent\" mean?", "\"Diligent\" শব্দের অর্থ কী?",
            listOf("Lazy", "Hardworking", "Angry", "Famous"), listOf("অলস", "পরিশ্রমী", "রাগী", "বিখ্যাত"), 1,
            "Diligent means showing steady, careful effort.", "Diligent মানে নিয়মিত ও যত্নের সাথে প্রচেষ্টা চালানো।"
        ),
        question(
            "q_frugal", "What does \"frugal\" mean?", "\"Frugal\" শব্দের অর্থ কী?",
            listOf("Wasteful", "Thrifty", "Generous", "Careless"), listOf("অপচয়ী", "মিতব্যয়ী", "উদার", "অসতর্ক"), 1,
            "Frugal means careful about spending.", "Frugal মানে খরচে সতর্ক।"
        ),
        question(
            "q_tranquil", "Which word is closest in meaning to \"tranquil\"?", "\"Tranquil\" শব্দের সবচেয়ে কাছের অর্থ কোনটি?",
            listOf("Noisy", "Calm", "Busy", "Rough"), listOf("কোলাহলপূর্ণ", "শান্ত", "ব্যস্ত", "এবড়োখেবড়ো"), 1,
            "Tranquil means calm and peaceful.", "Tranquil মানে শান্ত ও নিরিবিলি।"
        ),
        question(
            "q_abundant_antonym", "Which word is the opposite of \"abundant\"?", "\"Abundant\" শব্দের বিপরীত শব্দ কোনটি?",
            listOf("Plentiful", "Scarce", "Rich", "Large"), listOf("প্রচুর", "দুষ্প্রাপ্য", "ধনী", "বড়"), 1,
            "Scarce means in short supply.", "Scarce মানে সরবরাহ কম।"
        ),
        question(
            "q_jupiter", "Which is the largest planet in the solar system?", "সৌরজগতের বৃহত্তম গ্রহ কোনটি?",
            listOf("Saturn", "Jupiter", "Neptune", "Earth"), listOf("শনি", "বৃহস্পতি", "নেপচুন", "পৃথিবী"), 1,
            "Jupiter is more than 11 times wider than Earth.", "বৃহস্পতি পৃথিবীর চেয়ে ১১ গুণেরও বেশি প্রশস্ত।"
        ),
        question(
            "q_bones", "How many bones does an adult human have?", "প্রাপ্তবয়স্ক মানুষের কতটি হাড় থাকে?",
            listOf("106", "206", "306", "406"), listOf("১০৬", "২০৬", "৩০৬", "৪০৬"), 1,
            "An adult skeleton has 206 bones.", "প্রাপ্তবয়স্কের কঙ্কালে ২০৬টি হাড় থাকে।"
        ),
        question(
            "q_boiling", "At sea level, at what temperature does water boil?", "সমুদ্রপৃষ্ঠে পানি কত ডিগ্রি সেলসিয়াসে ফোটে?",
            listOf("90 °C", "100 °C", "110 °C", "120 °C"), listOf("৯০ °C", "১০০ °C", "১১০ °C", "১২০ °C"), 1,
            "Water boils at 100 °C at standard pressure.", "স্বাভাবিক চাপে পানি ১০০ °C-এ ফোটে।"
        ),
        question(
            "q_photosynthesis", "Which gas do plants absorb for photosynthesis?", "সালোকসংশ্লেষণের জন্য উদ্ভিদ কোন গ্যাস গ্রহণ করে?",
            listOf("Oxygen", "Carbon dioxide", "Nitrogen", "Hydrogen"), listOf("অক্সিজেন", "কার্বন ডাই-অক্সাইড", "নাইট্রোজেন", "হাইড্রোজেন"), 1,
            "Plants take in carbon dioxide and release oxygen.", "উদ্ভিদ কার্বন ডাই-অক্সাইড নেয় এবং অক্সিজেন ছাড়ে।"
        ),
        question(
            "q_ocean", "Which is the largest ocean?", "বৃহত্তম মহাসাগর কোনটি?",
            listOf("Atlantic", "Indian", "Pacific", "Arctic"), listOf("আটলান্টিক", "ভারত", "প্রশান্ত", "উত্তর মেরু"), 2,
            "The Pacific covers about a third of the Earth.", "প্রশান্ত মহাসাগর পৃথিবীর প্রায় এক-তৃতীয়াংশ জুড়ে।"
        ),
        question(
            "q_canberra", "What is the capital of Australia?", "অস্ট্রেলিয়ার রাজধানী কোনটি?",
            listOf("Sydney", "Melbourne", "Canberra", "Perth"), listOf("সিডনি", "মেলবোর্ন", "ক্যানবেরা", "পার্থ"), 2,
            "Canberra, not Sydney, is the capital.", "রাজধানী ক্যানবেরা, সিডনি নয়।"
        ),
        question(
            "q_victory", "On which date is Victory Day of Bangladesh?", "বাংলাদেশের বিজয় দিবস কোন তারিখে?",
            listOf("26 March", "21 February", "16 December", "17 April"), listOf("২৬ মার্চ", "২১ ফেব্রুয়ারি", "১৬ ডিসেম্বর", "১৭ এপ্রিল"), 2,
            "Victory Day is 16 December.", "বিজয় দিবস ১৬ ডিসেম্বর।"
        ),
        question(
            "q_anthem", "Who wrote the national anthem \"Amar Shonar Bangla\"?", "জাতীয় সংগীত \"আমার সোনার বাংলা\" কে রচনা করেছেন?",
            listOf("Kazi Nazrul Islam", "Rabindranath Tagore", "Jibanananda Das", "Jasimuddin"), listOf("কাজী নজরুল ইসলাম", "রবীন্দ্রনাথ ঠাকুর", "জীবনানন্দ দাশ", "জসীমউদ্দীন"), 1,
            "Rabindranath Tagore wrote it.", "রবীন্দ্রনাথ ঠাকুর এটি রচনা করেছেন।"
        ),
        question(
            "q_national_poet", "Who is the national poet of Bangladesh?", "বাংলাদেশের জাতীয় কবি কে?",
            listOf("Kazi Nazrul Islam", "Rabindranath Tagore", "Michael Madhusudan Dutt", "Sukanta Bhattacharya"), listOf("কাজী নজরুল ইসলাম", "রবীন্দ্রনাথ ঠাকুর", "মাইকেল মধুসূদন দত্ত", "সুকান্ত ভট্টাচার্য"), 0,
            "Kazi Nazrul Islam is the national poet.", "কাজী নজরুল ইসলাম জাতীয় কবি।"
        ),
        question(
            "q_sixty_dome", "In which district is the Sixty Dome Mosque located?", "ষাট গম্বুজ মসজিদ কোন জেলায় অবস্থিত?",
            listOf("Bagerhat", "Bogura", "Sylhet", "Cumilla"), listOf("বাগেরহাট", "বগুড়া", "সিলেট", "কুমিল্লা"), 0,
            "It is part of the Mosque City of Bagerhat.", "এটি বাগেরহাটের মসজিদ শহরের অংশ।"
        ),
        question(
            "q_fish", "Which is the national fish of Bangladesh?", "বাংলাদেশের জাতীয় মাছ কোনটি?",
            listOf("Rui", "Ilish", "Katla", "Koi"), listOf("রুই", "ইলিশ", "কাতলা", "কই"), 1,
            "The Ilish (hilsa) is the national fish.", "ইলিশ হলো জাতীয় মাছ।"
        ),
        question(
            "q_un_founded", "In which year was the United Nations founded?", "জাতিসংঘ কোন বছর প্রতিষ্ঠিত হয়?",
            listOf("1919", "1945", "1971", "1991"), listOf("১৯১৯", "১৯৪৫", "১৯৭১", "১৯৯১"), 1,
            "The UN was founded in 1945.", "জাতিসংঘ প্রতিষ্ঠিত হয় ১৯৪৫ সালে।"
        ),
        question(
            "q_satellite", "In which year was Bangabandhu Satellite-1 launched?", "বঙ্গবন্ধু স্যাটেলাইট-১ কোন বছর উৎক্ষেপণ করা হয়?",
            listOf("2016", "2018", "2020", "2022"), listOf("২০১৬", "২০১৮", "২০২০", "২০২২"), 1,
            "It was launched on 12 May 2018.", "এটি উৎক্ষেপণ করা হয় ২০১৮ সালের ১২ মে।"
        ),
        question(
            "q_light", "Approximately how fast does light travel?", "আলো প্রায় কত দ্রুত চলে?",
            listOf("3,000 km/s", "30,000 km/s", "300,000 km/s", "3,000,000 km/s"), listOf("৩,০০০ কিমি/সে.", "৩০,০০০ কিমি/সে.", "৩,০০,০০০ কিমি/সে.", "৩০,০০,০০০ কিমি/সে."), 2,
            "Light travels at about 300,000 km per second.", "আলো প্রতি সেকেন্ডে প্রায় ৩,০০,০০০ কিলোমিটার চলে।"
        )
    )
}
