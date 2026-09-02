package com.ayybay.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ayybay.app.data.local.entity.AlarmEntity
import com.ayybay.app.data.local.entity.AppNotificationEntity
import com.ayybay.app.data.local.entity.JobBookmarkEntity
import com.ayybay.app.data.local.entity.LinkEntity
import com.ayybay.app.data.local.entity.NoteEntity
import com.ayybay.app.data.local.entity.PrayerLogEntity
import com.ayybay.app.data.local.entity.PrayerSettingsEntity
import com.ayybay.app.data.local.entity.PrayerTimeEntity
import com.ayybay.app.data.local.entity.QuranReadDayEntity
import com.ayybay.app.data.local.entity.SurahProgressEntity

@Database(
    entities = [
        TransactionEntity::class,
        PrayerTimeEntity::class,
        PrayerSettingsEntity::class,
        LinkEntity::class,
        NoteEntity::class,
        PrayerLogEntity::class,
        SurahProgressEntity::class,
        QuranReadDayEntity::class,
        AlarmEntity::class,
        JobBookmarkEntity::class,
        AppNotificationEntity::class
    ],
    version = 8,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun prayerTimeDao(): PrayerTimeDao
    abstract fun linkDao(): LinkDao
    abstract fun noteDao(): NoteDao
    abstract fun prayerLogDao(): PrayerLogDao
    abstract fun quranProgressDao(): QuranProgressDao
    abstract fun alarmDao(): AlarmDao
    abstract fun jobBookmarkDao(): JobBookmarkDao
    abstract fun appNotificationDao(): AppNotificationDao

    companion object {
        private const val DATABASE_NAME = "ayybay_database"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `links` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `title` TEXT NOT NULL,
                        `description` TEXT NOT NULL,
                        `url` TEXT NOT NULL,
                        `category` TEXT NOT NULL,
                        `addedDate` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE `transactions` ADD COLUMN `paymentMethod` TEXT NOT NULL DEFAULT 'Cash'")
                database.execSQL("ALTER TABLE `transactions` ADD COLUMN `note` TEXT NOT NULL DEFAULT ''")
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `notes` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `title` TEXT NOT NULL,
                        `body` TEXT NOT NULL,
                        `isPinned` INTEGER NOT NULL DEFAULT 0,
                        `createdAt` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `prayer_logs` (
                        `dateKey` INTEGER NOT NULL,
                        `prayerName` TEXT NOT NULL,
                        `isPrayed` INTEGER NOT NULL,
                        PRIMARY KEY(`dateKey`, `prayerName`)
                    )
                    """.trimIndent()
                )
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `surah_progress` (
                        `surahNumber` INTEGER PRIMARY KEY NOT NULL,
                        `isCompleted` INTEGER NOT NULL,
                        `completedAt` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `quran_read_days` (
                        `dateKey` INTEGER PRIMARY KEY NOT NULL,
                        `surahsOpened` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // insertPrayerTimes() used OnConflictStrategy.REPLACE against an
                // autoGenerate id, which never collides, so every schedule run (each
                // app start) appended a fresh set of rows for the same day instead of
                // replacing them. Drop the accumulated duplicates before adding the
                // unique index, keeping the newest row per (date, prayerName).
                database.execSQL(
                    """
                    DELETE FROM prayer_times WHERE id NOT IN (
                        SELECT MAX(id) FROM prayer_times GROUP BY date, prayerName
                    )
                    """.trimIndent()
                )
                database.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS `index_prayer_times_date_prayerName` ON `prayer_times` (`date`, `prayerName`)"
                )
            }
        }

        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `alarms` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `hour` INTEGER NOT NULL,
                        `minute` INTEGER NOT NULL,
                        `label` TEXT NOT NULL,
                        `isEnabled` INTEGER NOT NULL,
                        `repeatDays` TEXT NOT NULL,
                        `vibrate` INTEGER NOT NULL,
                        `createdAt` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Per-prayer notification enabled flags move from the daily prayer_times
                // row (recomputed -- and its isEnabled reset to true -- every time
                // SchedulePrayerNotificationsUseCase runs) onto the singleton
                // prayer_settings row, which is the only place they actually persist.
                database.execSQL("ALTER TABLE `prayer_settings` ADD COLUMN `fajrEnabled` INTEGER NOT NULL DEFAULT 1")
                database.execSQL("ALTER TABLE `prayer_settings` ADD COLUMN `dhuhrEnabled` INTEGER NOT NULL DEFAULT 1")
                database.execSQL("ALTER TABLE `prayer_settings` ADD COLUMN `asrEnabled` INTEGER NOT NULL DEFAULT 1")
                database.execSQL("ALTER TABLE `prayer_settings` ADD COLUMN `maghribEnabled` INTEGER NOT NULL DEFAULT 1")
                database.execSQL("ALTER TABLE `prayer_settings` ADD COLUMN `ishaEnabled` INTEGER NOT NULL DEFAULT 1")
                database.execSQL("ALTER TABLE `prayer_settings` ADD COLUMN `placeName` TEXT")
                database.execSQL("ALTER TABLE `prayer_settings` ADD COLUMN `autoLocationEnabled` INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE `prayer_settings` ADD COLUMN `hijriOffset` INTEGER NOT NULL DEFAULT 0")

                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `job_bookmarks` (
                        `jobId` INTEGER PRIMARY KEY NOT NULL,
                        `bookmarkedAt` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `app_notifications` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `type` TEXT NOT NULL,
                        `titleEn` TEXT NOT NULL,
                        `titleBn` TEXT NOT NULL,
                        `bodyEn` TEXT NOT NULL,
                        `bodyBn` TEXT NOT NULL,
                        `timestamp` INTEGER NOT NULL,
                        `isRead` INTEGER NOT NULL DEFAULT 0,
                        `deepLinkRoute` TEXT
                    )
                    """.trimIndent()
                )
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .addMigrations(MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}