package com.ayybay.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.healthDataStore by preferencesDataStore(name = "health_prefs")

data class HealthProfile(
    val dateOfBirth: Long? = null,
    val heightCm: Double? = null,
    val weightKg: Double? = null
)

class HealthPreferences(private val context: Context) {

    private object Keys {
        val DATE_OF_BIRTH = longPreferencesKey("date_of_birth")
        val HEIGHT_CM = doublePreferencesKey("height_cm")
        val WEIGHT_KG = doublePreferencesKey("weight_kg")
    }

    val profile: Flow<HealthProfile> = context.healthDataStore.data.map { prefs ->
        HealthProfile(
            dateOfBirth = prefs[Keys.DATE_OF_BIRTH],
            heightCm = prefs[Keys.HEIGHT_CM],
            weightKg = prefs[Keys.WEIGHT_KG]
        )
    }

    suspend fun setDateOfBirth(millis: Long) {
        context.healthDataStore.edit { prefs -> prefs[Keys.DATE_OF_BIRTH] = millis }
    }

    suspend fun setHeightCm(heightCm: Double) {
        context.healthDataStore.edit { prefs -> prefs[Keys.HEIGHT_CM] = heightCm }
    }

    suspend fun setWeightKg(weightKg: Double) {
        context.healthDataStore.edit { prefs -> prefs[Keys.WEIGHT_KG] = weightKg }
    }
}
