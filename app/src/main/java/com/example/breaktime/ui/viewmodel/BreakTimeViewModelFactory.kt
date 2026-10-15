package com.example.breaktime.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.breaktime.data.BreakTimeHistoryRepository
import com.example.breaktime.data.SettingsRepository

class BreakTimeViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BreakTimeViewModel::class.java)) {
            val historyRepo = BreakTimeHistoryRepository.create(context.applicationContext)
            val settingsRepo = SettingsRepository.create(context.applicationContext)
            return BreakTimeViewModel(historyRepo, settingsRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
