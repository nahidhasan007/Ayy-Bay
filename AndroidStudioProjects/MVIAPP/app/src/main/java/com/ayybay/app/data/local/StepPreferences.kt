package com.ayybay.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.stepDataStore by preferencesDataStore(name = "step_prefs")

const val DEFAULT_STEP_GOAL = 10_000

class StepPreferences(private val context: Context) {

    private object Keys {
        val DAILY_GOAL = intPreferencesKey("daily_goal")
        val LAST_COUNTER = longPreferencesKey("last_counter")
    }

    val dailyGoal: Flow<Int> = context.stepDataStore.data.map { it[Keys.DAILY_GOAL] ?: DEFAULT_STEP_GOAL }

    suspend fun setDailyGoal(goal: Int) {
        context.stepDataStore.edit { it[Keys.DAILY_GOAL] = goal }
    }

    /** Last raw TYPE_STEP_COUNTER reading we accounted for (steps since boot), or null if none yet. */
    suspend fun lastCounter(): Long? = context.stepDataStore.data.first()[Keys.LAST_COUNTER]

    suspend fun setLastCounter(value: Long) {
        context.stepDataStore.edit { it[Keys.LAST_COUNTER] = value }
    }
}
