package com.ayybay.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
        QuranReadDayEntity::class
    ],
    version = 6,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun prayerTimeDao(): PrayerTimeDao
    abstract fun linkDao(): LinkDao
    abstract fun noteDao(): NoteDao
    abstract fun prayerLogDao(): PrayerLogDao
    abstract fun quranProgressDao(): QuranProgressDao

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

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .addMigrations(MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}