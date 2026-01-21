package com.ayybay.app.data.mapper

import com.ayybay.app.data.local.entity.PrayerSettingsEntity
import com.ayybay.app.data.local.entity.PrayerTimeEntity
import com.ayybay.app.domain.model.CalculationMethod
import com.ayybay.app.domain.model.Madhab
import com.ayybay.app.domain.model.PrayerName
import com.ayybay.app.domain.model.PrayerSettings
import com.ayybay.app.domain.model.PrayerTime
import java.util.Date

object PrayerTimeMapper {

    fun toEntity(prayerTime: PrayerTime, date: Date): PrayerTimeEntity {
        return PrayerTimeEntity(
            prayerName = prayerTime.prayerName.name,
            time = prayerTime.time.time,
            date = date.time,
            isEnabled = prayerTime.isEnabled
        )
    }

    fun toDomain(entity: PrayerTimeEntity): PrayerTime {
        return PrayerTime(
            prayerName = PrayerName.valueOf(entity.prayerName),
            time = Date(entity.time),
            isEnabled = entity.isEnabled
        )
    }

    fun toEntity(settings: PrayerSettings): PrayerSettingsEntity {
        return PrayerSettingsEntity(
            locationLatitude = settings.locationLatitude,
            locationLongitude = settings.locationLongitude,
            calculationMethod = settings.calculationMethod.name,
            madhab = settings.madhab.name,
            notificationsEnabled = settings.notificationsEnabled
        )
    }

    fun toDomainSettings(entity: PrayerSettingsEntity?): PrayerSettings {
        return if (entity != null) {
            PrayerSettings(
                locationLatitude = entity.locationLatitude,
                locationLongitude = entity.locationLongitude,
                calculationMethod = try {
                    CalculationMethod.valueOf(entity.calculationMethod)
                } catch (e: IllegalArgumentException) {
                    CalculationMethod.MWL
                },
                madhab = try {
                    Madhab.valueOf(entity.madhab)
                } catch (e: IllegalArgumentException) {
                    Madhab.SHAFI
                },
                notificationsEnabled = entity.notificationsEnabled
            )
        } else {
            PrayerSettings()
        }
    }
}