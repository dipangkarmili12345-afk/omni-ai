package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "omni_user_prefs")

data class UserPreferences(
    val messagesUsedToday: Int = 0,
    val bonusMessages: Int = 0,
    val isPremium: Boolean = false,
    val activePlan: String = "Free",
    val themeMode: String = "dark",
    val language: String = "en",
    val defaultModel: String = "gemini",
    val backendUrl: String = "",
    val lastResetDate: String = ""
) {
    val premiumPlan: String
        get() = activePlan

    val dailyLimit: Int
        get() = if (isPremium) Int.MAX_VALUE else (30 + bonusMessages)

    val remainingMessages: Int
        get() = if (isPremium) 9999 else (dailyLimit - messagesUsedToday).coerceAtLeast(0)

    val canSendMessage: Boolean
        get() = isPremium || (messagesUsedToday < dailyLimit)
}

class UserPreferencesManager(private val context: Context) {

    companion object {
        private val KEY_MESSAGES_USED_TODAY = intPreferencesKey("messages_used_today")
        private val KEY_BONUS_MESSAGES = intPreferencesKey("bonus_messages")
        private val KEY_IS_PREMIUM = booleanPreferencesKey("is_premium")
        private val KEY_ACTIVE_PLAN = stringPreferencesKey("active_plan")
        private val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
        private val KEY_LANGUAGE = stringPreferencesKey("language")
        private val KEY_DEFAULT_MODEL = stringPreferencesKey("default_model")
        private val KEY_BACKEND_URL = stringPreferencesKey("backend_url")
        private val KEY_LAST_RESET_DATE = stringPreferencesKey("last_reset_date")

        private fun getTodayDateString(): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            return sdf.format(Date())
        }
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data.map { prefs ->
        val today = getTodayDateString()
        val lastDate = prefs[KEY_LAST_RESET_DATE] ?: ""
        val messagesUsed = if (lastDate == today) {
            prefs[KEY_MESSAGES_USED_TODAY] ?: 0
        } else {
            0
        }
        val bonus = if (lastDate == today) {
            prefs[KEY_BONUS_MESSAGES] ?: 0
        } else {
            0
        }

        UserPreferences(
            messagesUsedToday = messagesUsed,
            bonusMessages = bonus,
            isPremium = prefs[KEY_IS_PREMIUM] ?: false,
            activePlan = prefs[KEY_ACTIVE_PLAN] ?: "Free",
            themeMode = prefs[KEY_THEME_MODE] ?: "dark",
            language = prefs[KEY_LANGUAGE] ?: "en",
            defaultModel = prefs[KEY_DEFAULT_MODEL] ?: "gemini",
            backendUrl = prefs[KEY_BACKEND_URL] ?: "",
            lastResetDate = lastDate
        )
    }

    suspend fun checkAndResetDailyLimit() {
        val today = getTodayDateString()
        context.dataStore.edit { prefs ->
            val lastDate = prefs[KEY_LAST_RESET_DATE] ?: ""
            if (lastDate != today) {
                prefs[KEY_LAST_RESET_DATE] = today
                prefs[KEY_MESSAGES_USED_TODAY] = 0
                prefs[KEY_BONUS_MESSAGES] = 0
            }
        }
    }

    suspend fun incrementMessageUsage(): Boolean {
        val today = getTodayDateString()
        var allowed = false
        context.dataStore.edit { prefs ->
            val lastDate = prefs[KEY_LAST_RESET_DATE] ?: ""
            if (lastDate != today) {
                prefs[KEY_LAST_RESET_DATE] = today
                prefs[KEY_MESSAGES_USED_TODAY] = 0
                prefs[KEY_BONUS_MESSAGES] = 0
            }

            val isPremium = prefs[KEY_IS_PREMIUM] ?: false
            val used = prefs[KEY_MESSAGES_USED_TODAY] ?: 0
            val bonus = prefs[KEY_BONUS_MESSAGES] ?: 0
            val limit = if (isPremium) Int.MAX_VALUE else (30 + bonus)

            if (isPremium || used < limit) {
                prefs[KEY_MESSAGES_USED_TODAY] = used + 1
                allowed = true
            } else {
                allowed = false
            }
        }
        return allowed
    }

    suspend fun addBonusMessages(count: Int = 10) {
        val today = getTodayDateString()
        context.dataStore.edit { prefs ->
            val lastDate = prefs[KEY_LAST_RESET_DATE] ?: ""
            if (lastDate != today) {
                prefs[KEY_LAST_RESET_DATE] = today
                prefs[KEY_MESSAGES_USED_TODAY] = 0
                prefs[KEY_BONUS_MESSAGES] = 0
            }
            val currentBonus = prefs[KEY_BONUS_MESSAGES] ?: 0
            prefs[KEY_BONUS_MESSAGES] = currentBonus + count
        }
    }

    suspend fun setPremium(isPremium: Boolean, plan: String = "Free") {
        context.dataStore.edit { prefs ->
            prefs[KEY_IS_PREMIUM] = isPremium
            prefs[KEY_ACTIVE_PLAN] = plan
        }
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_THEME_MODE] = mode
        }
    }

    suspend fun setLanguage(lang: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_LANGUAGE] = lang
        }
    }

    suspend fun setDefaultModel(modelId: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_DEFAULT_MODEL] = modelId
        }
    }

    suspend fun setBackendUrl(url: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_BACKEND_URL] = url
        }
    }
}
