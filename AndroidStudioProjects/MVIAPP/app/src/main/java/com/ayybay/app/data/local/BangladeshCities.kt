package com.ayybay.app.data.local

data class CityLocation(
    val nameEn: String,
    val nameBn: String,
    val latitude: Double,
    val longitude: Double
)

/**
 * A representative set of Bangladesh districts plus major world cities, for the manual
 * location picker fallback when GPS is unavailable or denied. Not exhaustive -- add more
 * entries here as needed.
 */
object BangladeshCities {

    fun bangladeshCities(): List<CityLocation> = listOf(
        CityLocation("Dhaka", "ঢাকা", 23.8103, 90.4125),
        CityLocation("Chattogram", "চট্টগ্রাম", 22.3569, 91.7832),
        CityLocation("Khulna", "খুলনা", 22.8456, 89.5403),
        CityLocation("Rajshahi", "রাজশাহী", 24.3745, 88.6042),
        CityLocation("Sylhet", "সিলেট", 24.8949, 91.8687),
        CityLocation("Barishal", "বরিশাল", 22.7010, 90.3535),
        CityLocation("Rangpur", "রংপুর", 25.7439, 89.2752),
        CityLocation("Mymensingh", "ময়মনসিংহ", 24.7471, 90.4203),
        CityLocation("Cumilla", "কুমিল্লা", 23.4607, 91.1809),
        CityLocation("Narayanganj", "নারায়ণগঞ্জ", 23.6238, 90.5000),
        CityLocation("Gazipur", "গাজীপুর", 23.9999, 90.4203),
        CityLocation("Bogura", "বগুড়া", 24.8465, 89.3773),
        CityLocation("Jashore", "যশোর", 23.1667, 89.2167),
        CityLocation("Dinajpur", "দিনাজপুর", 25.6279, 88.6332),
        CityLocation("Cox's Bazar", "কক্সবাজার", 21.4272, 92.0058),
        CityLocation("Tangail", "টাঙ্গাইল", 24.2513, 89.9167),
        CityLocation("Faridpur", "ফরিদপুর", 23.6070, 89.8429),
        CityLocation("Pabna", "পাবনা", 24.0064, 89.2372),
        CityLocation("Noakhali", "নোয়াখালী", 22.8696, 91.0995),
        CityLocation("Kushtia", "কুষ্টিয়া", 23.9013, 89.1220)
    )

    fun worldCities(): List<CityLocation> = listOf(
        CityLocation("Mecca", "মক্কা", 21.3891, 39.8579),
        CityLocation("Medina", "মদিনা", 24.5247, 39.5692),
        CityLocation("Riyadh", "রিয়াদ", 24.7136, 46.6753),
        CityLocation("Dubai", "দুবাই", 25.2048, 55.2708),
        CityLocation("Kuala Lumpur", "কুয়ালালামপুর", 3.1390, 101.6869),
        CityLocation("Singapore", "সিঙ্গাপুর", 1.3521, 103.8198),
        CityLocation("London", "লন্ডন", 51.5074, -0.1278),
        CityLocation("New York", "নিউ ইয়র্ক", 40.7128, -74.0060)
    )

    fun all(): List<CityLocation> = bangladeshCities() + worldCities()
}
