package com.example.breaktime.data

import android.content.Context
import android.content.SharedPreferences
import com.example.breaktime.model.AppSettings
import com.example.breaktime.model.AppTheme
import com.example.breaktime.model.BreakDuration
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SettingsRepository(
    private val preferences: SharedPreferences
) {
    companion object {
        private const val PREFS_NAME = "breaktime_settings_prefs"
        private const val KEY_THEME = "key_theme"
        private const val KEY_DYNAMIC_COLOR = "key_dynamic_color"
        private const val KEY_HAPTIC = "key_haptic"
        private const val KEY_ANIMATIONS = "key_animations"
        private const val KEY_PRESERVE_LAST = "key_preserve_last"
        private const val KEY_CONFIRM_DELETE = "key_confirm_delete"

        private const val KEY_LAST_WORK_HOURS = "key_last_work_hours"
        private const val KEY_LAST_WORK_MINUTES = "key_last_work_minutes"
        private const val KEY_LAST_BREAKS = "key_last_breaks"

        fun create(context: Context): SettingsRepository {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return SettingsRepository(prefs)
        }
    }

    private val gson = Gson()
    private val breaksType = object : TypeToken<List<BreakDuration>>() {}.type

    fun getSettings(): AppSettings {
        val themeStr = preferences.getString(KEY_THEME, AppTheme.SYSTEM.name) ?: AppTheme.SYSTEM.name
        val theme = try {
            AppTheme.valueOf(themeStr)
        } catch (_: Exception) {
            AppTheme.SYSTEM
        }

        return AppSettings(
            theme = theme,
            dynamicColor = preferences.getBoolean(KEY_DYNAMIC_COLOR, true),
            hapticFeedback = preferences.getBoolean(KEY_HAPTIC, true),
            animationsEnabled = preferences.getBoolean(KEY_ANIMATIONS, true),
            preserveLastCalculation = preferences.getBoolean(KEY_PRESERVE_LAST, true),
            confirmHistoryDelete = preferences.getBoolean(KEY_CONFIRM_DELETE, true)
        )
    }

    fun setTheme(theme: AppTheme) {
        preferences.edit().putString(KEY_THEME, theme.name).apply()
    }

    fun setDynamicColor(enabled: Boolean) {
        preferences.edit().putBoolean(KEY_DYNAMIC_COLOR, enabled).apply()
    }

    fun setHapticFeedback(enabled: Boolean) {
        preferences.edit().putBoolean(KEY_HAPTIC, enabled).apply()
    }

    fun setAnimationsEnabled(enabled: Boolean) {
        preferences.edit().putBoolean(KEY_ANIMATIONS, enabled).apply()
    }

    fun setPreserveLastCalculation(enabled: Boolean) {
        preferences.edit().putBoolean(KEY_PRESERVE_LAST, enabled).apply()
    }

    fun setConfirmHistoryDelete(enabled: Boolean) {
        preferences.edit().putBoolean(KEY_CONFIRM_DELETE, enabled).apply()
    }

    fun resetSettings() {
        preferences.edit()
            .putString(KEY_THEME, AppTheme.SYSTEM.name)
            .putBoolean(KEY_DYNAMIC_COLOR, true)
            .putBoolean(KEY_HAPTIC, true)
            .putBoolean(KEY_ANIMATIONS, true)
            .putBoolean(KEY_PRESERVE_LAST, true)
            .putBoolean(KEY_CONFIRM_DELETE, true)
            .apply()
    }

    fun saveLastCalculation(hours: String, minutes: String, breaks: List<BreakDuration>) {
        val json = gson.toJson(breaks)
        preferences.edit()
            .putString(KEY_LAST_WORK_HOURS, hours)
            .putString(KEY_LAST_WORK_MINUTES, minutes)
            .putString(KEY_LAST_BREAKS, json)
            .apply()
    }

    fun getLastWorkHours(): String {
        return preferences.getString(KEY_LAST_WORK_HOURS, "8") ?: "8"
    }

    fun getLastWorkMinutes(): String {
        return preferences.getString(KEY_LAST_WORK_MINUTES, "0") ?: "0"
    }

    fun getLastBreaks(): List<BreakDuration> {
        val json = preferences.getString(KEY_LAST_BREAKS, null) ?: return emptyList()
        return try {
            gson.fromJson<List<BreakDuration>>(json, breaksType) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun clearLastCalculation() {
        preferences.edit()
            .remove(KEY_LAST_WORK_HOURS)
            .remove(KEY_LAST_WORK_MINUTES)
            .remove(KEY_LAST_BREAKS)
            .apply()
    }
}
