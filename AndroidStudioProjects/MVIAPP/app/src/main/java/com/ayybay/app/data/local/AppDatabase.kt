package com.ayybay.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ayybay.app.data.local.entity.LinkEntity
import com.ayybay.app.data.local.entity.PrayerSettingsEntity
import com.ayybay.app.data.local.entity.PrayerTimeEntity

@Database(
    entities = [
        TransactionEntity::class,
        PrayerTimeEntity::class,
        PrayerSettingsEntity::class,
        LinkEntity::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun prayerTimeDao(): PrayerTimeDao
    abstract fun linkDao(): LinkDao

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

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .addMigrations(MIGRATION_2_3, MIGRATION_3_4)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}