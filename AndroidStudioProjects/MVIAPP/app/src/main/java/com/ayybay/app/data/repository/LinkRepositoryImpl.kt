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