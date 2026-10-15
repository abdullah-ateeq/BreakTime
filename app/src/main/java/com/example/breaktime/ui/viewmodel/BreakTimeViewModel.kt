package com.example.breaktime.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.breaktime.data.BreakTimeHistoryRepository
import com.example.breaktime.data.SettingsRepository
import com.example.breaktime.domain.BreakTimeCalculator
import com.example.breaktime.domain.BreakTimeValidator
import com.example.breaktime.model.AppSettings
import com.example.breaktime.model.AppTheme
import com.example.breaktime.model.BreakDuration
import com.example.breaktime.model.BreakTimeHistory
import com.example.breaktime.model.BreakTimeResult
import com.example.breaktime.model.BreakTimeUiState
import com.example.breaktime.model.CalculationState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class BreakTimeViewModel(
    private val historyRepository: BreakTimeHistoryRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BreakTimeUiState())
    val uiState: StateFlow<BreakTimeUiState> = _uiState.asStateFlow()

    private var lastBreakPresetTapTimestamp: Long = 0L

    init {
        loadInitialState()
    }

    private fun loadInitialState() {
        val settings = settingsRepository.getSettings()
        val history = historyRepository.getHistory()

        var initialHours = "8"
        var initialMinutes = "0"
        var initialBreaks = emptyList<BreakDuration>()

        if (settings.preserveLastCalculation) {
            initialHours = settingsRepository.getLastWorkHours()
            initialMinutes = settingsRepository.getLastWorkMinutes()
            initialBreaks = settingsRepository.getLastBreaks()
        }

        _uiState.update {
            it.copy(
                workHoursInput = initialHours,
                workMinutesInput = initialMinutes,
                breaks = initialBreaks,
                history = history,
                settings = settings,
                result = null,
                calculationState = CalculationState.Idle,
                errorMessage = null
            )
        }
    }

    fun onWorkHoursChange(hours: String) {
        // Strip non-digits
        val filtered = hours.filter { it.isDigit() }
        _uiState.update {
            it.copy(
                workHoursInput = filtered,
                result = null,
                calculationState = CalculationState.Idle,
                errorMessage = null
            )
        }
        persistCurrentInputsIfEnabled()
    }

    fun onWorkMinutesChange(minutes: String) {
        // Strip non-digits
        val filtered = minutes.filter { it.isDigit() }
        _uiState.update {
            it.copy(
                workMinutesInput = filtered,
                result = null,
                calculationState = CalculationState.Idle,
                errorMessage = null
            )
        }
        persistCurrentInputsIfEnabled()
    }

    fun applyWorkPreset(hours: Long, minutes: Long) {
        _uiState.update {
            it.copy(
                workHoursInput = hours.toString(),
                workMinutesInput = minutes.toString(),
                result = null,
                calculationState = CalculationState.Idle,
                errorMessage = null
            )
        }
        persistCurrentInputsIfEnabled()
    }

    fun addBreak(): Boolean {
        if (_uiState.value.breaks.size >= 50) return false

        val currentBreaks = _uiState.value.breaks.toMutableList()
        val nextIndex = currentBreaks.size + 1
        val newBreak = BreakDuration(
            id = UUID.randomUUID().toString(),
            label = "Break $nextIndex",
            hours = 0,
            minutes = 30
        )
        currentBreaks.add(newBreak)

        _uiState.update {
            it.copy(
                breaks = currentBreaks,
                result = null,
                calculationState = CalculationState.Idle,
                errorMessage = null
            )
        }
        persistCurrentInputsIfEnabled()
        return true
    }

    fun applyBreakPreset(hours: Long, minutes: Long, defaultLabel: String): Boolean {
        val now = System.currentTimeMillis()
        if (now - lastBreakPresetTapTimestamp < 300L) {
            // Protect against accidental double-tapping
            return false
        }
        lastBreakPresetTapTimestamp = now

        if (_uiState.value.breaks.size >= 50) return false

        val currentBreaks = _uiState.value.breaks.toMutableList()
        val nextIndex = currentBreaks.size + 1
        val label = defaultLabel.ifBlank { "Break $nextIndex" }
        val newBreak = BreakDuration(
            id = UUID.randomUUID().toString(),
            label = label,
            hours = hours,
            minutes = minutes
        )
        currentBreaks.add(newBreak)

        _uiState.update {
            it.copy(
                breaks = currentBreaks,
                result = null,
                calculationState = CalculationState.Idle,
                errorMessage = null
            )
        }
        persistCurrentInputsIfEnabled()
        return true
    }

    fun updateBreak(id: String, label: String, hours: Long, minutes: Long) {
        val sanitizedLabel = if (label.length > 40) label.take(40) else label
        val updatedBreaks = _uiState.value.breaks.map { b ->
            if (b.id == id) {
                b.copy(label = sanitizedLabel, hours = hours, minutes = minutes)
            } else {
                b
            }
        }
        _uiState.update {
            it.copy(
                breaks = updatedBreaks,
                result = null,
                calculationState = CalculationState.Idle,
                errorMessage = null
            )
        }
        persistCurrentInputsIfEnabled()
    }

    fun updateBreakLabel(id: String, rawLabel: String) {
        val sanitized = if (rawLabel.length > 40) rawLabel.take(40) else rawLabel
        val updatedBreaks = _uiState.value.breaks.map { b ->
            if (b.id == id) b.copy(label = sanitized) else b
        }
        _uiState.update {
            it.copy(
                breaks = updatedBreaks,
                result = null,
                calculationState = CalculationState.Idle,
                errorMessage = null
            )
        }
        persistCurrentInputsIfEnabled()
    }

    fun updateBreakHours(id: String, hoursStr: String) {
        val filtered = hoursStr.filter { it.isDigit() }
        val hoursLong = filtered.toLongOrNull() ?: 0L
        val updatedBreaks = _uiState.value.breaks.map { b ->
            if (b.id == id) b.copy(hours = hoursLong) else b
        }
        _uiState.update {
            it.copy(
                breaks = updatedBreaks,
                result = null,
                calculationState = CalculationState.Idle,
                errorMessage = null
            )
        }
        persistCurrentInputsIfEnabled()
    }

    fun updateBreakMinutes(id: String, minutesStr: String) {
        val filtered = minutesStr.filter { it.isDigit() }
        val minutesLong = filtered.toLongOrNull() ?: 0L
        val updatedBreaks = _uiState.value.breaks.map { b ->
            if (b.id == id) b.copy(minutes = minutesLong) else b
        }
        _uiState.update {
            it.copy(
                breaks = updatedBreaks,
                result = null,
                calculationState = CalculationState.Idle,
                errorMessage = null
            )
        }
        persistCurrentInputsIfEnabled()
    }

    fun deleteBreak(id: String) {
        val updatedBreaks = _uiState.value.breaks.filter { it.id != id }
        _uiState.update {
            it.copy(
                breaks = updatedBreaks,
                result = null,
                calculationState = CalculationState.Idle,
                errorMessage = null
            )
        }
        persistCurrentInputsIfEnabled()
    }

    fun clearBreaks() {
        _uiState.update {
            it.copy(
                breaks = emptyList(),
                result = null,
                calculationState = CalculationState.Idle,
                errorMessage = null
            )
        }
        persistCurrentInputsIfEnabled()
    }

    fun calculateNetTime(saveToHistory: Boolean = true): Boolean {
        val currentState = _uiState.value

        // Validate work duration
        val workValidation = BreakTimeValidator.validateWorkInput(
            currentState.workHoursInput,
            currentState.workMinutesInput
        )
        if (workValidation is BreakTimeValidator.ValidationResult.Invalid) {
            _uiState.update {
                it.copy(
                    errorMessage = workValidation.message,
                    calculationState = CalculationState.Error(workValidation.message),
                    result = null
                )
            }
            return false
        }
        val (workHours, workMinutes) = (workValidation as BreakTimeValidator.ValidationResult.Valid)

        // Validate all breaks
        for ((index, b) in currentState.breaks.withIndex()) {
            val breakValidation = BreakTimeValidator.validateBreakInput(
                b.hours.toString(),
                b.minutes.toString(),
                b.label
            )
            if (breakValidation is BreakTimeValidator.ValidationResult.Invalid) {
                val msg = "Break ${index + 1}: ${breakValidation.message}"
                _uiState.update {
                    it.copy(
                        errorMessage = msg,
                        calculationState = CalculationState.Error(msg),
                        result = null
                    )
                }
                return false
            }
        }

        return try {
            val result = BreakTimeCalculator.calculate(
                workHours = workHours,
                workMinutes = workMinutes,
                breaks = currentState.breaks
            )

            val updatedHistory = if (saveToHistory) {
                val historyEntry = BreakTimeHistory(
                    id = UUID.randomUUID().toString(),
                    workHours = result.normalizedWorkHours,
                    workMinutes = result.normalizedWorkMinutes,
                    breaks = currentState.breaks,
                    totalWorkMinutes = result.totalWorkMinutes,
                    totalBreakMinutes = result.totalBreakMinutes,
                    netMinutes = result.netMinutes,
                    calculatedAt = System.currentTimeMillis()
                )
                historyRepository.addCalculation(historyEntry)
            } else {
                _uiState.value.history
            }

            _uiState.update {
                it.copy(
                    result = result,
                    history = updatedHistory,
                    calculationState = CalculationState.Calculated,
                    errorMessage = null
                )
            }

            persistCurrentInputsIfEnabled()
            true
        } catch (e: IllegalArgumentException) {
            val msg = e.message ?: "Invalid duration calculation."
            _uiState.update {
                it.copy(
                    errorMessage = msg,
                    calculationState = CalculationState.Error(msg),
                    result = null
                )
            }
            false
        }
    }

    fun reset() {
        _uiState.update {
            it.copy(
                workHoursInput = "8",
                workMinutesInput = "0",
                breaks = emptyList(),
                result = null,
                calculationState = CalculationState.Idle,
                errorMessage = null
            )
        }
        if (_uiState.value.settings.preserveLastCalculation) {
            settingsRepository.saveLastCalculation("8", "0", emptyList())
        }
    }

    fun loadHistoryItem(item: BreakTimeHistory) {
        _uiState.update {
            it.copy(
                workHoursInput = item.workHours.toString(),
                workMinutesInput = item.workMinutes.toString(),
                breaks = item.breaks,
                errorMessage = null
            )
        }
        calculateNetTime(saveToHistory = false)
    }

    fun deleteHistoryItem(id: String) {
        val updated = historyRepository.deleteCalculation(id)
        _uiState.update { it.copy(history = updated) }
    }

    fun clearHistory() {
        historyRepository.clearHistory()
        _uiState.update { it.copy(history = emptyList()) }
    }

    fun setTheme(theme: AppTheme) {
        settingsRepository.setTheme(theme)
        _uiState.update {
            it.copy(settings = it.settings.copy(theme = theme))
        }
    }

    fun setDynamicColor(enabled: Boolean) {
        settingsRepository.setDynamicColor(enabled)
        _uiState.update {
            it.copy(settings = it.settings.copy(dynamicColor = enabled))
        }
    }

    fun setHapticFeedback(enabled: Boolean) {
        settingsRepository.setHapticFeedback(enabled)
        _uiState.update {
            it.copy(settings = it.settings.copy(hapticFeedback = enabled))
        }
    }

    fun setAnimationsEnabled(enabled: Boolean) {
        settingsRepository.setAnimationsEnabled(enabled)
        _uiState.update {
            it.copy(settings = it.settings.copy(animationsEnabled = enabled))
        }
    }

    fun setPreserveLastCalculation(enabled: Boolean) {
        settingsRepository.setPreserveLastCalculation(enabled)
        _uiState.update {
            it.copy(settings = it.settings.copy(preserveLastCalculation = enabled))
        }
        if (enabled) {
            persistCurrentInputsIfEnabled()
        } else {
            settingsRepository.clearLastCalculation()
        }
    }

    fun setConfirmHistoryDelete(enabled: Boolean) {
        settingsRepository.setConfirmHistoryDelete(enabled)
        _uiState.update {
            it.copy(settings = it.settings.copy(confirmHistoryDelete = enabled))
        }
    }

    fun resetSettings() {
        settingsRepository.resetSettings()
        val defaultSettings = settingsRepository.getSettings()
        _uiState.update {
            it.copy(settings = defaultSettings)
        }
    }

    private fun persistCurrentInputsIfEnabled() {
        if (_uiState.value.settings.preserveLastCalculation) {
            settingsRepository.saveLastCalculation(
                _uiState.value.workHoursInput,
                _uiState.value.workMinutesInput,
                _uiState.value.breaks
            )
        }
    }
}
