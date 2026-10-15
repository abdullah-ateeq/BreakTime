package com.example.breaktime.data

import android.content.Context
import android.content.SharedPreferences
import com.example.breaktime.model.BreakTimeHistory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class BreakTimeHistoryRepository(
    private val preferences: SharedPreferences
) {
    companion object {
        private const val PREFS_NAME = "breaktime_history_prefs"
        private const val KEY_HISTORY = "recent_calculations"
        const val MAX_HISTORY_ITEMS = 10

        fun create(context: Context): BreakTimeHistoryRepository {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return BreakTimeHistoryRepository(prefs)
        }
    }

    private val gson = Gson()
    private val historyType = object : TypeToken<List<BreakTimeHistory>>() {}.type

    fun getHistory(): List<BreakTimeHistory> {
        val json = preferences.getString(KEY_HISTORY, null) ?: return emptyList()
        return try {
            gson.fromJson<List<BreakTimeHistory>>(json, historyType) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun addCalculation(entry: BreakTimeHistory): List<BreakTimeHistory> {
        val current = getHistory().toMutableList()
        // Prevent immediate duplicate if identical to the latest entry
        if (current.isNotEmpty()) {
            val first = current[0]
            if (first.workHours == entry.workHours &&
                first.workMinutes == entry.workMinutes &&
                first.totalBreakMinutes == entry.totalBreakMinutes &&
                first.breaks == entry.breaks
            ) {
                return current
            }
        }

        current.add(0, entry)
        while (current.size > MAX_HISTORY_ITEMS) {
            current.removeAt(current.size - 1)
        }

        val json = gson.toJson(current)
        preferences.edit().putString(KEY_HISTORY, json).apply()
        return current
    }

    fun deleteCalculation(id: String): List<BreakTimeHistory> {
        val current = getHistory().toMutableList()
        current.removeAll { it.id == id }
        val json = gson.toJson(current)
        preferences.edit().putString(KEY_HISTORY, json).apply()
        return current
    }

    fun clearHistory() {
        preferences.edit().remove(KEY_HISTORY).apply()
    }
}
