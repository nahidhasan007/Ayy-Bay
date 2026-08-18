package com.ayybay.app.data.repository

import com.ayybay.app.data.local.LinkDao
import com.ayybay.app.data.local.entity.LinkEntity
import com.ayybay.app.domain.model.DailyLink
import com.ayybay.app.domain.model.LinkCategory
import com.ayybay.app.domain.repository.LinkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LinkRepositoryImpl(
    private val linkDao: LinkDao
) : LinkRepository {

    override fun getAllLinks(): Flow<List<DailyLink>> =
        linkDao.getAllLinks().map { it.map { e -> e.toDomain() } }

    override fun getLinksByCategory(category: String): Flow<List<DailyLink>> =
        linkDao.getLinksByCategory(category).map { it.map { e -> e.toDomain() } }

    override fun getLinkById(id: Long): Flow<DailyLink?> =
        linkDao.getLinkById(id).map { it?.toDomain() }

    override suspend fun insertLink(link: DailyLink): Long =
        linkDao.insertLink(link.toEntity())

    override suspend fun deleteLink(link: DailyLink) =
        linkDao.deleteLink(link.toEntity())

    override fun getLinkCountByCategory(category: String): Flow<Int> =
        linkDao.getLinkCountByCategory(category)

    override suspend fun seedIfEmpty() {
        if (linkDao.getTotalLinkCount() > 0) return
        val now = System.currentTimeMillis()
        var offset = 0L
        fun next() = now - (offset++ * 1000L)

        val seeds = listOf(
            // ── NEWS ──────────────────────────────────────────────
            entity("Prothom Alo", "Most widely read Bangla daily newspaper", "https://www.prothomalo.com", LinkCategory.NEWS, next()),
            entity("The Daily Star", "Leading English-language daily newspaper", "https://www.thedailystar.net", LinkCategory.NEWS, next()),
            entity("bdnews24", "First and largest Bangladeshi online news portal", "https://bdnews24.com", LinkCategory.NEWS, next()),
            entity("Kaler Kantho", "Popular Bangla daily newspaper", "https://www.kalerkantho.com", LinkCategory.NEWS, next()),
            entity("The Business Standard", "Business and national news portal", "https://www.tbsnews.net", LinkCategory.NEWS, next()),
            entity("Dhaka Post", "Major online Bangla news portal", "https://www.dhakapost.com", LinkCategory.NEWS, next()),
            entity("Jugantor", "Popular Bangla daily newspaper", "https://www.jugantor.com", LinkCategory.NEWS, next()),
            entity("The Financial Express", "Financial and economic news daily", "https://thefinancialexpress.com.bd", LinkCategory.NEWS, next()),

            // ── GOVERNMENT ────────────────────────────────────────
            entity("Bangladesh National Portal", "Gateway to 25,000+ government websites", "https://bangladesh.gov.bd", LinkCategory.GOVERNMENT, next()),
            entity("E-Passport Portal", "Online e-Passport application and status check", "https://epassport.gov.bd", LinkCategory.GOVERNMENT, next()),
            entity("NID / Voter Services", "National ID card application and smart card status", "https://services.nidw.gov.bd", LinkCategory.GOVERNMENT, next()),
            entity("BRTA", "Vehicle registration and driving licence services", "https://brta.gov.bd", LinkCategory.GOVERNMENT, next()),
            entity("National Board of Revenue", "NBR – tax, VAT and customs authority", "https://nbr.gov.bd", LinkCategory.GOVERNMENT, next()),
            entity("CPTU – Govt Tenders", "Central Procurement Technical Unit – govt tenders", "https://cptu.gov.bd", LinkCategory.GOVERNMENT, next()),
            entity("Ministry of Home Affairs", "Internal security, police, immigration portal", "https://moha.gov.bd", LinkCategory.GOVERNMENT, next()),

            // ── JOBS ──────────────────────────────────────────────
            entity("BDjobs", "Largest job portal – 30,000+ employers", "https://bdjobs.com", LinkCategory.JOBS, next()),
            entity("Chakri.com", "Prothom Alo Jobs – popular career portal", "https://www.chakri.com", LinkCategory.JOBS, next()),
            entity("BPSC", "Bangladesh Public Service Commission – BCS & govt jobs", "https://www.bpsc.gov.bd", LinkCategory.JOBS, next()),
            entity("Career Gov BD", "Official government job circular portal", "https://www.career.gov.bd", LinkCategory.JOBS, next()),
            entity("Shomvob", "Jobs for entry-level to experienced professionals", "https://shomvob.com", LinkCategory.JOBS, next()),
            entity("AllJobs BD", "Aggregator of top Bangladeshi job listings", "https://alljobs.com.bd", LinkCategory.JOBS, next()),

            // ── EDUCATION ─────────────────────────────────────────
            entity("Education Board Results", "Official SSC / HSC / JSC result check", "http://www.educationboardresults.gov.bd", LinkCategory.EDUCATION, next()),
            entity("eBoard Results", "Results with marksheets for all boards", "https://www.eboardresults.com/v2/home", LinkCategory.EDUCATION, next()),
            entity("National University BD", "Degree / Honours / Masters admission and results", "https://nu.ac.bd", LinkCategory.EDUCATION, next()),
            entity("University Grants Commission", "UGC Bangladesh – higher education oversight", "https://ugc.gov.bd", LinkCategory.EDUCATION, next()),
            entity("Dhaka University Admission", "DU admission test portal and result", "https://admission.eis.du.ac.bd", LinkCategory.EDUCATION, next()),
            entity("Bangladesh Open University", "Distance education and BOU results", "https://bou.ac.bd", LinkCategory.EDUCATION, next()),
            entity("BANBEIS", "Education information and statistics portal", "https://banbeis.gov.bd", LinkCategory.EDUCATION, next()),

            // ── FINANCE ───────────────────────────────────────────
            entity("Bangladesh Bank", "Central Bank of Bangladesh – official portal", "https://www.bb.org.bd", LinkCategory.FINANCE, next()),
            entity("bKash", "Largest mobile money platform in Bangladesh", "https://www.bkash.com", LinkCategory.FINANCE, next()),
            entity("Nagad", "Bangladesh Post Office MFS – second largest wallet", "https://nagad.com.bd", LinkCategory.FINANCE, next()),
            entity("Rocket (DBBL)", "Dutch-Bangla Bank mobile banking and Rocket MFS", "https://www.dutchbanglabank.com", LinkCategory.FINANCE, next()),
            entity("BRAC Bank", "Major private commercial bank", "https://www.bracbank.com", LinkCategory.FINANCE, next()),
            entity("Islami Bank Bangladesh", "Largest private bank by deposits", "https://www.islamibankbd.com", LinkCategory.FINANCE, next()),
            entity("EkPay", "Unified government fee and utility payment gateway", "https://ekpay.gov.bd", LinkCategory.FINANCE, next()),
            entity("Upay", "UCB-backed mobile financial service", "https://www.upaybd.com", LinkCategory.FINANCE, next()),

            // ── SHOPPING ──────────────────────────────────────────
            entity("Daraz Bangladesh", "Largest marketplace – Alibaba-owned platform", "https://www.daraz.com.bd", LinkCategory.SHOPPING, next()),
            entity("Chaldal", "Online grocery with 1-hour delivery in Dhaka", "https://www.chaldal.com", LinkCategory.SHOPPING, next()),
            entity("Rokomari", "Largest online bookstore in Bangladesh", "https://www.rokomari.com", LinkCategory.SHOPPING, next()),
            entity("Shajgoj", "Beauty and personal care marketplace", "https://www.shajgoj.com", LinkCategory.SHOPPING, next()),
            entity("Priyoshop", "General merchandise and electronics", "https://www.priyoshop.com", LinkCategory.SHOPPING, next()),
            entity("Ajkerdeal", "Daily deals and discount marketplace", "https://www.ajkerdeal.com", LinkCategory.SHOPPING, next()),
            entity("Pickaboo", "Electronics and gadgets online store", "https://www.pickaboo.com", LinkCategory.SHOPPING, next()),
            entity("Foodpanda Bangladesh", "Food delivery platform in Bangladesh", "https://www.foodpanda.com.bd", LinkCategory.SHOPPING, next()),

            // ── TRANSPORT ─────────────────────────────────────────
            entity("BD Railway E-Ticket", "Official Bangladesh Railway online ticket booking", "https://eticket.railway.gov.bd", LinkCategory.TRANSPORT, next()),
            entity("Shohoz", "Bus, train, flight and event ticket booking", "https://www.shohoz.com", LinkCategory.TRANSPORT, next()),
            entity("BDTickets", "Online bus ticket booking portal", "https://bdtickets.com", LinkCategory.TRANSPORT, next()),
            entity("ShareTrip", "Flight, hotel and tour package booking", "https://www.sharetrip.net", LinkCategory.TRANSPORT, next()),
            entity("Biman Bangladesh Airlines", "National flag carrier – booking and schedules", "https://www.biman-airlines.com", LinkCategory.TRANSPORT, next()),
            entity("US-Bangla Airlines", "Domestic and international flights", "https://www.usbanglaairlines.com", LinkCategory.TRANSPORT, next()),
            entity("Green Line Bus", "Green Line Paribahan bus e-ticketing", "https://greenlinebd.com", LinkCategory.TRANSPORT, next()),
            entity("BUSBD", "Inter-city bus ticket booking platform", "https://busbd.com.bd", LinkCategory.TRANSPORT, next()),

            // ── HEALTH ────────────────────────────────────────────
            entity("DGHS Bangladesh", "Directorate General of Health Services", "https://dghs.gov.bd", LinkCategory.HEALTH, next()),
            entity("DGHS Telemedicine", "Official government telemedicine portal (16263)", "https://tele-medicine.dghs.gov.bd", LinkCategory.HEALTH, next()),
            entity("Ministry of Health", "Ministry of Health and Family Welfare portal", "https://mohfw.gov.bd", LinkCategory.HEALTH, next()),
            entity("Hospital Directory", "DGHS hospital and clinic directory", "http://hospitaldghs.gov.bd", LinkCategory.HEALTH, next()),
            entity("Doctime", "Online doctor consultation and appointments", "https://www.doctime.com.bd", LinkCategory.HEALTH, next()),
            entity("Sewa BD", "Telemedicine and home health service", "https://www.sewabd.com", LinkCategory.HEALTH, next()),
            entity("Praava Health", "Private primary healthcare network", "https://www.praavahealth.com", LinkCategory.HEALTH, next()),

            // ── EMERGENCY ─────────────────────────────────────────
            entity("National Emergency 999", "Police, Fire, Ambulance – 24/7 hotline info", "https://www.999.gov.bd", LinkCategory.EMERGENCY, next()),
            entity("Bangladesh Police", "Bangladesh Police official portal", "https://www.police.gov.bd", LinkCategory.EMERGENCY, next()),
            entity("Fire Service & Civil Defence", "Bangladesh Fire Service official portal", "https://www.fireservice.gov.bd", LinkCategory.EMERGENCY, next()),
            entity("Disaster Management", "Ministry of Disaster Management and Relief", "https://www.modmr.gov.bd", LinkCategory.EMERGENCY, next()),
            entity("Women's Helpline 109", "Women and children safety hotline – MWCA", "https://www.mwca.gov.bd", LinkCategory.EMERGENCY, next()),
            entity("24 Ambulance BD", "Private ambulance service directory", "https://24ambulance.com", LinkCategory.EMERGENCY, next()),

            // ── ENTERTAINMENT ─────────────────────────────────────
            entity("Chorki", "Premium Bangladeshi OTT platform by Prothom Alo", "https://www.chorki.com", LinkCategory.ENTERTAINMENT, next()),
            entity("Bongo", "Bangla OTT – movies, dramas, music", "https://www.bongo.com.bd", LinkCategory.ENTERTAINMENT, next()),
            entity("Toffee", "Banglalink OTT – 100+ live TV channels", "https://toffee.com.bd", LinkCategory.ENTERTAINMENT, next()),
            entity("Hoichoi", "Bengali OTT – Bangla and regional content", "https://www.hoichoi.tv", LinkCategory.ENTERTAINMENT, next()),
            entity("BTV Online", "Bangladesh Television – national broadcaster", "https://www.btv.gov.bd", LinkCategory.ENTERTAINMENT, next()),
            entity("Channel i", "Popular private TV channel in Bangladesh", "https://www.channelionline.com", LinkCategory.ENTERTAINMENT, next()),
            entity("NTV Bangladesh", "Leading private television channel", "https://www.ntvbd.com", LinkCategory.ENTERTAINMENT, next()),
            entity("Bioscope – GP", "Grameenphone streaming and live TV aggregator", "https://bioscope.grameenphone.com", LinkCategory.ENTERTAINMENT, next()),

            // ── TELECOM ───────────────────────────────────────────
            entity("Grameenphone", "Largest mobile operator – 84M+ subscribers", "https://www.grameenphone.com", LinkCategory.TELECOM, next()),
            entity("Robi", "Second largest operator – Axiata Group", "https://www.robi.com.bd", LinkCategory.TELECOM, next()),
            entity("Banglalink", "Third largest operator – Veon Group", "https://www.banglalink.net", LinkCategory.TELECOM, next()),
            entity("Teletalk", "State-owned operator – key for govt job registration", "https://www.teletalk.com.bd", LinkCategory.TELECOM, next()),
            entity("BTRC", "Bangladesh Telecommunications Regulatory Commission", "https://btrc.gov.bd", LinkCategory.TELECOM, next()),
            entity("BTCL", "State-owned landline and internet service provider", "https://www.btcl.gov.bd", LinkCategory.TELECOM, next()),

            // ── SPORTS ────────────────────────────────────────────
            entity("BCB – Tiger Cricket", "Bangladesh Cricket Board official website", "https://www.tigercricket.com.bd", LinkCategory.SPORTS, next()),
            entity("BCB Ticket Booking", "Cricket match ticket booking portal", "https://www.gobcbticket.com.bd", LinkCategory.SPORTS, next()),
            entity("Bangladesh Football Federation", "BFF – national football governing body", "https://www.bff.com.bd", LinkCategory.SPORTS, next()),
            entity("Bangladesh Olympic Assoc.", "Bangladesh Olympic Association official portal", "https://www.bod.org.bd", LinkCategory.SPORTS, next()),
            entity("ESPN Cricinfo – Bangladesh", "Cricket stats, schedules and player profiles", "https://www.espncricinfo.com/bangladesh", LinkCategory.SPORTS, next()),

            // ── ISLAMIC ───────────────────────────────────────────
            entity("Islamic Foundation BD", "Official Islamic Foundation Bangladesh portal", "https://www.islamicfoundation.gov.bd", LinkCategory.ISLAMIC, next()),
            entity("Quran.com", "Read, listen and search the Holy Quran online", "https://quran.com", LinkCategory.ISLAMIC, next()),
            entity("Sunnah.com", "Authentic Hadith collections in English and Arabic", "https://sunnah.com", LinkCategory.ISLAMIC, next()),
            entity("Islam QA", "Islamic rulings and Q&A from scholars", "https://islamqa.info/en", LinkCategory.ISLAMIC, next()),

            // ── QURAN SURAHS (1–114) ────────────────────────────────
            entity("1. Al-Fatihah", "Surah 1 – Al-Fatihah", "https://www.quraanshareef.org/Surah-Al-Fatihah", LinkCategory.ISLAMIC, next()),
            entity("2. Al-Baqara", "Surah 2 – Al-Baqara", "https://www.quraanshareef.org/Surah-Al-Baqara", LinkCategory.ISLAMIC, next()),
            entity("3. Al-Imran", "Surah 3 – Al-Imran", "https://www.quraanshareef.org/Surah-Al-Imran", LinkCategory.ISLAMIC, next()),
            entity("4. An-Nisaa", "Surah 4 – An-Nisaa", "https://www.quraanshareef.org/Surah-An-Nisaa", LinkCategory.ISLAMIC, next()),
            entity("5. Al-Maidah", "Surah 5 – Al-Maidah", "https://www.quraanshareef.org/Surah-Al-Maidah", LinkCategory.ISLAMIC, next()),
            entity("6. Al-An'am", "Surah 6 – Al-An'am", "https://www.quraanshareef.org/Surah-Al-Anam", LinkCategory.ISLAMIC, next()),
            entity("7. Al-A'raf", "Surah 7 – Al-A'raf", "https://www.quraanshareef.org/Surah-Al-Araf", LinkCategory.ISLAMIC, next()),
            entity("8. Al-Anfal", "Surah 8 – Al-Anfal", "https://www.quraanshareef.org/Surah-Al-Anfal", LinkCategory.ISLAMIC, next()),
            entity("9. At-Taubah", "Surah 9 – At-Taubah", "https://www.quraanshareef.org/Surah-At-Taubah", LinkCategory.ISLAMIC, next()),
            entity("10. Yunus", "Surah 10 – Yunus", "https://www.quraanshareef.org/Surah-Yunus", LinkCategory.ISLAMIC, next()),
            entity("11. Hud", "Surah 11 – Hud", "https://www.quraanshareef.org/Surah-Hud", LinkCategory.ISLAMIC, next()),
            entity("12. Yusuf", "Surah 12 – Yusuf", "https://www.quraanshareef.org/Surah-Yusuf", LinkCategory.ISLAMIC, next()),
            entity("13. Ar-Ra'd", "Surah 13 – Ar-Ra'd", "https://www.quraanshareef.org/Surah-Ar-Rad", LinkCategory.ISLAMIC, next()),
            entity("14. Ibrahim", "Surah 14 – Ibrahim", "https://www.quraanshareef.org/Surah-Ibrahim", LinkCategory.ISLAMIC, next()),
            entity("15. Al-Hijr", "Surah 15 – Al-Hijr", "https://www.quraanshareef.org/Surah-Al-Hijr", LinkCategory.ISLAMIC, next()),
            entity("16. An-Nahl", "Surah 16 – An-Nahl", "https://www.quraanshareef.org/Surah-An-Nahl", LinkCategory.ISLAMIC, next()),
            entity("17. Al-Isra", "Surah 17 – Al-Isra (Bani Israel)", "https://www.quraanshareef.org/Surah-Israel", LinkCategory.ISLAMIC, next()),
            entity("18. Al-Kahf", "Surah 18 – Al-Kahf", "https://www.quraanshareef.org/Surah-Al-Kahf", LinkCategory.ISLAMIC, next()),
            entity("19. Maryam", "Surah 19 – Maryam", "https://www.quraanshareef.org/Surah-Maryam", LinkCategory.ISLAMIC, next()),
            entity("20. Ta-ha", "Surah 20 – Ta-ha", "https://www.quraanshareef.org/Surah-Ta-ha", LinkCategory.ISLAMIC, next()),
            entity("21. Al-Anbiyaa", "Surah 21 – Al-Anbiyaa", "https://www.quraanshareef.org/Surah-Al-Anbiyaa", LinkCategory.ISLAMIC, next()),
            entity("22. Al-Hajj", "Surah 22 – Al-Hajj", "https://www.quraanshareef.org/Surah-Al-Hajj", LinkCategory.ISLAMIC, next()),
            entity("23. Al-Muminun", "Surah 23 – Al-Muminun", "https://www.quraanshareef.org/Surah-Al-Muminun", LinkCategory.ISLAMIC, next()),
            entity("24. An-Nur", "Surah 24 – An-Nur", "https://www.quraanshareef.org/Surah-An-Nur", LinkCategory.ISLAMIC, next()),
            entity("25. Al-Furqan", "Surah 25 – Al-Furqan", "https://www.quraanshareef.org/Surah-Al-Furqan", LinkCategory.ISLAMIC, next()),
            entity("26. Ash-Shu'araa", "Surah 26 – Ash-Shu'araa", "https://www.quraanshareef.org/Surah-Ash-Shuaraa", LinkCategory.ISLAMIC, next()),
            entity("27. An-Naml", "Surah 27 – An-Naml", "https://www.quraanshareef.org/Surah-An-Naml", LinkCategory.ISLAMIC, next()),
            entity("28. Al-Qasas", "Surah 28 – Al-Qasas", "https://www.quraanshareef.org/Surah-Al-Qasas", LinkCategory.ISLAMIC, next()),
            entity("29. Al-Ankabut", "Surah 29 – Al-Ankabut", "https://www.quraanshareef.org/Surah-Al-Ankabut", LinkCategory.ISLAMIC, next()),
            entity("30. Ar-Rum", "Surah 30 – Ar-Rum", "https://www.quraanshareef.org/Surah-Ar-Rum", LinkCategory.ISLAMIC, next()),
            entity("31. Luqman", "Surah 31 – Luqman", "https://www.quraanshareef.org/Surah-Luqman", LinkCategory.ISLAMIC, next()),
            entity("32. As-Sajda", "Surah 32 – As-Sajda", "https://www.quraanshareef.org/Surah-As-Sajda", LinkCategory.ISLAMIC, next()),
            entity("33. Al-Ahzab", "Surah 33 – Al-Ahzab", "https://www.quraanshareef.org/Surah-Al-Ahzab", LinkCategory.ISLAMIC, next()),
            entity("34. Saba", "Surah 34 – Saba", "https://www.quraanshareef.org/Surah-Saba", LinkCategory.ISLAMIC, next()),
            entity("35. Fatir", "Surah 35 – Fatir", "https://www.quraanshareef.org/Surah-Fatir", LinkCategory.ISLAMIC, next()),
            entity("36. Ya-Sin", "Surah 36 – Ya-Sin", "https://www.quraanshareef.org/Surah-Ya-Sin", LinkCategory.ISLAMIC, next()),
            entity("37. As-Saffat", "Surah 37 – As-Saffat", "https://www.quraanshareef.org/Surah-As-Saffat", LinkCategory.ISLAMIC, next()),
            entity("38. Sad", "Surah 38 – Sad", "https://www.quraanshareef.org/Surah-Sad", LinkCategory.ISLAMIC, next()),
            entity("39. Az-Zumar", "Surah 39 – Az-Zumar", "https://www.quraanshareef.org/Surah-Az-Zumar", LinkCategory.ISLAMIC, next()),
            entity("40. Al-Mu'min", "Surah 40 – Al-Mu'min (Ghafir)", "https://www.quraanshareef.org/Surah-Al-Mumin", LinkCategory.ISLAMIC, next()),
            entity("41. Ha-Mim", "Surah 41 – Ha-Mim (Fussilat)", "https://www.quraanshareef.org/Surah-Ha-Mim", LinkCategory.ISLAMIC, next()),
            entity("42. Ash-Shura", "Surah 42 – Ash-Shura", "https://www.quraanshareef.org/Surah-Ash-Shura", LinkCategory.ISLAMIC, next()),
            entity("43. Az-Zukhruf", "Surah 43 – Az-Zukhruf", "https://www.quraanshareef.org/Surah-Az-Zukhruf", LinkCategory.ISLAMIC, next()),
            entity("44. Ad-Dukhan", "Surah 44 – Ad-Dukhan", "https://www.quraanshareef.org/Surah-Ad-Dukhan", LinkCategory.ISLAMIC, next()),
            entity("45. Al-Jathiya", "Surah 45 – Al-Jathiya", "https://www.quraanshareef.org/Surah-Al-Jathiya", LinkCategory.ISLAMIC, next()),
            entity("46. Al-Ahqaf", "Surah 46 – Al-Ahqaf", "https://www.quraanshareef.org/Surah-Al-Ahqaf", LinkCategory.ISLAMIC, next()),
            entity("47. Muhammad", "Surah 47 – Muhammad", "https://www.quraanshareef.org/Surah-Muhammad", LinkCategory.ISLAMIC, next()),
            entity("48. Al-Fat-h", "Surah 48 – Al-Fat-h", "https://www.quraanshareef.org/Surah-Al-Fat-h", LinkCategory.ISLAMIC, next()),
            entity("49. Al-Hujurat", "Surah 49 – Al-Hujurat", "https://www.quraanshareef.org/Surah-Al-Hujurat", LinkCategory.ISLAMIC, next()),
            entity("50. Qaf", "Surah 50 – Qaf", "https://www.quraanshareef.org/Surah-Qaf", LinkCategory.ISLAMIC, next()),
            entity("51. Az-Zariyat", "Surah 51 – Az-Zariyat", "https://www.quraanshareef.org/Surah-Az-Zariyat", LinkCategory.ISLAMIC, next()),
            entity("52. At-Tur", "Surah 52 – At-Tur", "https://www.quraanshareef.org/Surah-At-Tur", LinkCategory.ISLAMIC, next()),
            entity("53. An-Najm", "Surah 53 – An-Najm", "https://www.quraanshareef.org/Surah-An-Najm", LinkCategory.ISLAMIC, next()),
            entity("54. Al-Qamar", "Surah 54 – Al-Qamar", "https://www.quraanshareef.org/Surah-Al-Qamar", LinkCategory.ISLAMIC, next()),
            entity("55. Ar-Rahman", "Surah 55 – Ar-Rahman", "https://www.quraanshareef.org/Surah-Ar-Rahman", LinkCategory.ISLAMIC, next()),
            entity("56. Al-Waqi'a", "Surah 56 – Al-Waqi'a", "https://www.quraanshareef.org/Surah-Al-Waqia", LinkCategory.ISLAMIC, next()),
            entity("57. Al-Hadid", "Surah 57 – Al-Hadid", "https://www.quraanshareef.org/Surah-Al-Hadid", LinkCategory.ISLAMIC, next()),
            entity("58. Al-Mujadila", "Surah 58 – Al-Mujadila", "https://www.quraanshareef.org/Surah-Al-Mujadila", LinkCategory.ISLAMIC, next()),
            entity("59. Al-Hashr", "Surah 59 – Al-Hashr", "https://www.quraanshareef.org/Surah-Al-Hashr", LinkCategory.ISLAMIC, next()),
            entity("60. Al-Mumtahana", "Surah 60 – Al-Mumtahana", "https://www.quraanshareef.org/Surah-Al-Mumtahana", LinkCategory.ISLAMIC, next()),
            entity("61. As-Saff", "Surah 61 – As-Saff", "https://www.quraanshareef.org/Surah-As-Saff", LinkCategory.ISLAMIC, next()),
            entity("62. Al-Jumu'a", "Surah 62 – Al-Jumu'a", "https://www.quraanshareef.org/Surah-Al-Jumua", LinkCategory.ISLAMIC, next()),
            entity("63. Al-Munafiqun", "Surah 63 – Al-Munafiqun", "https://www.quraanshareef.org/Surah-Al-Munafiqun", LinkCategory.ISLAMIC, next()),
            entity("64. At-Tagabun", "Surah 64 – At-Tagabun", "https://www.quraanshareef.org/Surah-At-Tagabun", LinkCategory.ISLAMIC, next()),
            entity("65. At-Talaq", "Surah 65 – At-Talaq", "https://www.quraanshareef.org/Surah-At-Talaq", LinkCategory.ISLAMIC, next()),
            entity("66. At-Tahrim", "Surah 66 – At-Tahrim", "https://www.quraanshareef.org/Surah-At-Tahrim", LinkCategory.ISLAMIC, next()),
            entity("67. Al-Mulk", "Surah 67 – Al-Mulk", "https://www.quraanshareef.org/Surah-Al-Mulk", LinkCategory.ISLAMIC, next()),
            entity("68. Al-Qalam", "Surah 68 – Al-Qalam", "https://www.quraanshareef.org/Surah-Al-Qalam", LinkCategory.ISLAMIC, next()),
            entity("69. Al-Haqqa", "Surah 69 – Al-Haqqa", "https://www.quraanshareef.org/Surah-Al-Haqqa", LinkCategory.ISLAMIC, next()),
            entity("70. Al-Ma'arij", "Surah 70 – Al-Ma'arij", "https://www.quraanshareef.org/Surah-Al-Maarij", LinkCategory.ISLAMIC, next()),
            entity("71. Nuh", "Surah 71 – Nuh", "https://www.quraanshareef.org/Surah-Nuh", LinkCategory.ISLAMIC, next()),
            entity("72. Al-Jinn", "Surah 72 – Al-Jinn", "https://www.quraanshareef.org/Surah-Al-Jinn", LinkCategory.ISLAMIC, next()),
            entity("73. Al-Muzzammil", "Surah 73 – Al-Muzzammil", "https://www.quraanshareef.org/Surah-Al-Muzzammil", LinkCategory.ISLAMIC, next()),
            entity("74. Al-Muddathth", "Surah 74 – Al-Muddathth", "https://www.quraanshareef.org/Surah-Al-Muddathth", LinkCategory.ISLAMIC, next()),
            entity("75. Al-Qiyamat", "Surah 75 – Al-Qiyamat", "https://www.quraanshareef.org/Surah-Al-Qiyamat", LinkCategory.ISLAMIC, next()),
            entity("76. Ad-Dahr", "Surah 76 – Ad-Dahr (Al-Insan)", "https://www.quraanshareef.org/Surah-Ad-Dahr", LinkCategory.ISLAMIC, next()),
            entity("77. Al-Mursalat", "Surah 77 – Al-Mursalat", "https://www.quraanshareef.org/Surah-Al-Mursalat", LinkCategory.ISLAMIC, next()),
            entity("78. An-Nabaa", "Surah 78 – An-Nabaa", "https://www.quraanshareef.org/Surah-An-Nabaa", LinkCategory.ISLAMIC, next()),
            entity("79. An-Nazi'at", "Surah 79 – An-Nazi'at", "https://www.quraanshareef.org/Surah-An-Naziat", LinkCategory.ISLAMIC, next()),
            entity("80. Abasa", "Surah 80 – Abasa", "https://www.quraanshareef.org/Surah-Abasa", LinkCategory.ISLAMIC, next()),
            entity("81. At-Takwir", "Surah 81 – At-Takwir", "https://www.quraanshareef.org/Surah-At-Takwir", LinkCategory.ISLAMIC, next()),
            entity("82. Al-Infitar", "Surah 82 – Al-Infitar", "https://www.quraanshareef.org/Surah-Al-Infitar", LinkCategory.ISLAMIC, next()),
            entity("83. Al-Mutaffife", "Surah 83 – Al-Mutaffife", "https://www.quraanshareef.org/Surah-Al-Mutaffife", LinkCategory.ISLAMIC, next()),
            entity("84. Al-Inshiqaq", "Surah 84 – Al-Inshiqaq", "https://www.quraanshareef.org/Surah-Al-Inshiqaq", LinkCategory.ISLAMIC, next()),
            entity("85. Al-Buruj", "Surah 85 – Al-Buruj", "https://www.quraanshareef.org/Surah-Al-Buruj", LinkCategory.ISLAMIC, next()),
            entity("86. At-Tariq", "Surah 86 – At-Tariq", "https://www.quraanshareef.org/Surah-At-Tariq", LinkCategory.ISLAMIC, next()),
            entity("87. Al-A'la", "Surah 87 – Al-A'la", "https://www.quraanshareef.org/Surah-Al-Ala", LinkCategory.ISLAMIC, next()),
            entity("88. Al-Gashiya", "Surah 88 – Al-Gashiya", "https://www.quraanshareef.org/Surah-Al-Gashiya", LinkCategory.ISLAMIC, next()),
            entity("89. Al-Fajr", "Surah 89 – Al-Fajr", "https://www.quraanshareef.org/Surah-Al-Fajr", LinkCategory.ISLAMIC, next()),
            entity("90. Al-Balad", "Surah 90 – Al-Balad", "https://www.quraanshareef.org/Surah-Al-Balad", LinkCategory.ISLAMIC, next()),
            entity("91. Ash-Shams", "Surah 91 – Ash-Shams", "https://www.quraanshareef.org/Surah-Ash-Shams", LinkCategory.ISLAMIC, next()),
            entity("92. Al-Lail", "Surah 92 – Al-Lail", "https://www.quraanshareef.org/Surah-Al-Lail", LinkCategory.ISLAMIC, next()),
            entity("93. Adh-Dhuha", "Surah 93 – Adh-Dhuha", "https://www.quraanshareef.org/Surah-Adh-Dhuha", LinkCategory.ISLAMIC, next()),
            entity("94. Al-Sharh", "Surah 94 – Al-Sharh", "https://www.quraanshareef.org/Surah-Al-Sharh", LinkCategory.ISLAMIC, next()),
            entity("95. At-Tin", "Surah 95 – At-Tin", "https://www.quraanshareef.org/Surah-At-Tin", LinkCategory.ISLAMIC, next()),
            entity("96. Al-Alaq", "Surah 96 – Al-Alaq", "https://www.quraanshareef.org/Surah-Al-Alaq", LinkCategory.ISLAMIC, next()),
            entity("97. Al-Qadr", "Surah 97 – Al-Qadr", "https://www.quraanshareef.org/Surah-Al-Qadr", LinkCategory.ISLAMIC, next()),
            entity("98. Al-Baiyina", "Surah 98 – Al-Baiyina", "https://www.quraanshareef.org/Surah-Al-Baiyina", LinkCategory.ISLAMIC, next()),
            entity("99. Al-Zalzalah", "Surah 99 – Al-Zalzalah", "https://www.quraanshareef.org/Surah-Al-Zalzalah", LinkCategory.ISLAMIC, next()),
            entity("100. Al-Adiyat", "Surah 100 – Al-Adiyat", "https://www.quraanshareef.org/Surah-Al-Adiyat", LinkCategory.ISLAMIC, next()),
            entity("101. Al-Qari'a", "Surah 101 – Al-Qari'a", "https://www.quraanshareef.org/Surah-Al-Qaria", LinkCategory.ISLAMIC, next()),
            entity("102. At-Takathur", "Surah 102 – At-Takathur", "https://www.quraanshareef.org/Surah-At-Takathur", LinkCategory.ISLAMIC, next()),
            entity("103. Al-Asr", "Surah 103 – Al-Asr", "https://www.quraanshareef.org/Surah-Al-Asr", LinkCategory.ISLAMIC, next()),
            entity("104. Al-Humaza", "Surah 104 – Al-Humaza", "https://www.quraanshareef.org/Surah-Al-Humaza", LinkCategory.ISLAMIC, next()),
            entity("105. Al-Fil", "Surah 105 – Al-Fil", "https://www.quraanshareef.org/Surah-Al-Fil", LinkCategory.ISLAMIC, next()),
            entity("106. Quraish", "Surah 106 – Quraish", "https://www.quraanshareef.org/Surah-Quraish", LinkCategory.ISLAMIC, next()),
            entity("107. Al-Ma'un", "Surah 107 – Al-Ma'un", "https://www.quraanshareef.org/Surah-Al-Maun", LinkCategory.ISLAMIC, next()),
            entity("108. Al-Kauthar", "Surah 108 – Al-Kauthar", "https://www.quraanshareef.org/Surah-Al-Kauthar", LinkCategory.ISLAMIC, next()),
            entity("109. Al-Kafirun", "Surah 109 – Al-Kafirun", "https://www.quraanshareef.org/Surah-Al-Kafirun", LinkCategory.ISLAMIC, next()),
            entity("110. An-Nasr", "Surah 110 – An-Nasr", "https://www.quraanshareef.org/Surah-An-Nasr", LinkCategory.ISLAMIC, next()),
            entity("111. Al-Lahab", "Surah 111 – Al-Lahab", "https://www.quraanshareef.org/Surah-Al-Lahab", LinkCategory.ISLAMIC, next()),
            entity("112. Al-Ikhlas", "Surah 112 – Al-Ikhlas", "https://www.quraanshareef.org/Surah-Al-Ikhlas", LinkCategory.ISLAMIC, next()),
            entity("113. Al-Falaq", "Surah 113 – Al-Falaq", "https://www.quraanshareef.org/Surah-Al-Falaq", LinkCategory.ISLAMIC, next()),
            entity("114. An-Nas", "Surah 114 – An-Nas", "https://www.quraanshareef.org/Surah-Al-Nas", LinkCategory.ISLAMIC, next()),

            // ── SOCIAL ────────────────────────────────────────────
            entity("BRAC", "World's largest NGO – poverty and development", "https://www.brac.net", LinkCategory.SOCIAL, next()),
            entity("Dept. of Social Services", "Govt allowances for elderly, disabled, widows", "https://dss.gov.bd", LinkCategory.SOCIAL, next()),
            entity("Grameen Bank", "Nobel-prize winning microfinance institution", "https://www.grameen.com", LinkCategory.SOCIAL, next()),
            entity("PKSF Bangladesh", "Microfinance apex body of Bangladesh", "https://www.pksf-bd.org", LinkCategory.SOCIAL, next()),
            entity("ASA Bangladesh", "Microfinance and social development NGO", "https://asa.org.bd", LinkCategory.SOCIAL, next()),

            // ── OTHER ─────────────────────────────────────────────
            entity("BMD – Weather", "Bangladesh Meteorological Dept – forecasts and alerts", "https://bmd.gov.bd", LinkCategory.OTHER, next()),
            entity("BPDB – Electricity Bill", "Power Development Board – online bill payment", "https://miscbill.bpdb.gov.bd/startup/consumer-bills", LinkCategory.OTHER, next()),
            entity("DPDC – Dhaka Power", "Dhaka Power Distribution Company portal", "https://dpdc.gov.bd", LinkCategory.OTHER, next()),
            entity("Titas Gas Bill", "Titas Gas transmission and distribution portal", "https://www.titasgas.org.bd", LinkCategory.OTHER, next()),
            entity("DWASA", "Dhaka Water Supply and Sewerage Authority", "https://www.dwasa.org.bd", LinkCategory.OTHER, next())
        )

        seeds.forEach { linkDao.insertLink(it) }
    }

    private fun entity(
        title: String,
        description: String,
        url: String,
        category: LinkCategory,
        addedDate: Long
    ) = LinkEntity(
        title = title,
        description = description,
        url = url,
        category = category.name,
        addedDate = addedDate
    )

    private fun LinkEntity.toDomain() = DailyLink(
        id = id,
        title = title,
        description = description,
        url = url,
        category = LinkCategory.valueOf(category),
        addedDate = addedDate
    )

    private fun DailyLink.toEntity() = LinkEntity(
        id = id,
        title = title,
        description = description,
        url = url,
        category = category.name,
        addedDate = addedDate
    )
}