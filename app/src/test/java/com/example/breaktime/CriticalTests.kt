package com.example.breaktime

import com.example.breaktime.data.BreakTimeHistoryRepository
import com.example.breaktime.data.SettingsRepository
import com.example.breaktime.domain.BreakTimeCalculator
import com.example.breaktime.domain.BreakTimeFormatter
import com.example.breaktime.model.AppTheme
import com.example.breaktime.model.BreakDuration
import com.example.breaktime.ui.viewmodel.BreakTimeViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test
import java.util.UUID

class CriticalTests {

    @Test
    fun criticalTest_basic() {
        // Work: 8 hours, Breaks: 30 minutes -> Expected: 7 hours 30 minutes
        val breaks = listOf(BreakDuration(UUID.randomUUID().toString(), "Break 1", 0, 30))
        val result = BreakTimeCalculator.calculate(8, 0, breaks)
        assertEquals("7 hours 30 minutes", BreakTimeFormatter.formatDuration(result.netMinutes))
    }

    @Test
    fun criticalTest_multipleBreaks() {
        // Work: 8 hours, Break 1: 30m, Break 2: 15m -> Expected: 7 hours 15 minutes
        val breaks = listOf(
            BreakDuration(UUID.randomUUID().toString(), "Break 1", 0, 30),
            BreakDuration(UUID.randomUUID().toString(), "Break 2", 0, 15)
        )
        val result = BreakTimeCalculator.calculate(8, 0, breaks)
        assertEquals("7 hours 15 minutes", BreakTimeFormatter.formatDuration(result.netMinutes))
    }

    @Test
    fun criticalTest_threeBreaks() {
        // Work: 8 hours, Break 1: 30m, Break 2: 15m, Break 3: 15m -> Expected: 7 hours
        val breaks = listOf(
            BreakDuration(UUID.randomUUID().toString(), "Break 1", 0, 30),
            BreakDuration(UUID.randomUUID().toString(), "Break 2", 0, 15),
            BreakDuration(UUID.randomUUID().toString(), "Break 3", 0, 15)
        )
        val result = BreakTimeCalculator.calculate(8, 0, breaks)
        assertEquals("7 hours", BreakTimeFormatter.formatDuration(result.netMinutes))
    }

    @Test
    fun criticalTest_minuteNormalization() {
        // Work: 7 hours 90 minutes -> Expected normalized work: 8h 30m
        val result = BreakTimeCalculator.calculate(7, 90, emptyList())
        assertEquals("8h 30m", BreakTimeFormatter.formatCompactDuration(result.totalWorkMinutes))
        assertEquals(8L, result.normalizedWorkHours)
        assertEquals(30L, result.normalizedWorkMinutes)
    }

    @Test
    fun criticalTest_breakNormalization() {
        // Work: 8 hours, Break: 1 hour 75 minutes -> Expected break: 2h 15m, Expected net: 5h 45m
        // Note: 8h (480m) - 2h 15m (135m) = 345m (5h 45m). (If work is 8h 30m: 8h 30m - 2h 15m = 6h 15m)
        val breaks = listOf(BreakDuration(UUID.randomUUID().toString(), "Break", 1, 75))
        val result = BreakTimeCalculator.calculate(8, 0, breaks)
        assertEquals("2h 15m", BreakTimeFormatter.formatCompactDuration(result.totalBreakMinutes))
        assertEquals(2L, result.breaks[0].normalizedHours)
        assertEquals(15L, result.breaks[0].normalizedMinutes)
        assertEquals(345L, result.netMinutes)
    }

    @Test
    fun criticalTest_sixtyMinutes() {
        // Work: 8 hours, Break: 60 minutes -> Expected: 7 hours
        val breaks = listOf(BreakDuration(UUID.randomUUID().toString(), "Break", 0, 60))
        val result = BreakTimeCalculator.calculate(8, 0, breaks)
        assertEquals("7 hours", BreakTimeFormatter.formatDuration(result.netMinutes))
    }

    @Test
    fun criticalTest_ninetyMinutes() {
        // Work: 8 hours, Break: 90 minutes -> Expected: 6h 30m
        val breaks = listOf(BreakDuration(UUID.randomUUID().toString(), "Break", 0, 90))
        val result = BreakTimeCalculator.calculate(8, 0, breaks)
        assertEquals("6h 30m", BreakTimeFormatter.formatCompactDuration(result.netMinutes))
    }

    @Test
    fun criticalTest_breaksEqualWork() {
        // Work: 8 hours, Break: 8 hours -> Expected: 0 minutes, Status: NO WORK TIME REMAINING
        val breaks = listOf(BreakDuration(UUID.randomUUID().toString(), "Break", 8, 0))
        val result = BreakTimeCalculator.calculate(8, 0, breaks)
        assertEquals(0L, result.netMinutes)
        assertEquals("0 minutes", BreakTimeFormatter.formatDuration(result.netMinutes))
    }

    @Test
    fun criticalTest_breaksGreaterThanWork() {
        // Work: 8 hours, Break: 8h 30m -> Expected: validation error. Message: Break time cannot exceed the work duration.
        val breaks = listOf(BreakDuration(UUID.randomUUID().toString(), "Break", 8, 30))
        val ex = assertThrows(IllegalArgumentException::class.java) {
            BreakTimeCalculator.calculate(8, 0, breaks)
        }
        assertEquals("Break time cannot exceed the work duration.", ex.message)
    }

    @Test
    fun criticalTest_noBreaks() {
        // Work: 8 hours, No breaks -> Expected: 8 hours
        val result = BreakTimeCalculator.calculate(8, 0, emptyList())
        assertEquals("8 hours", BreakTimeFormatter.formatDuration(result.netMinutes))
    }

    @Test
    fun criticalTest_zeroWork() {
        // Work: 0, Breaks: none -> Expected: 0 minutes
        val result = BreakTimeCalculator.calculate(0, 0, emptyList())
        assertEquals("0 minutes", BreakTimeFormatter.formatDuration(result.netMinutes))
    }

    @Test
    fun criticalTest_zeroWorkWithBreak() {
        // Work: 0, Break: 15 minutes -> Expected: validation error.
        val breaks = listOf(BreakDuration(UUID.randomUUID().toString(), "Break", 0, 15))
        val ex = assertThrows(IllegalArgumentException::class.java) {
            BreakTimeCalculator.calculate(0, 0, breaks)
        }
        assertEquals("Break time cannot exceed the work duration.", ex.message)
    }

    @Test
    fun criticalTest_digitalDuration() {
        // Work: 8h, Breaks: 45m -> Expected: 07:15:00
        val breaks = listOf(BreakDuration(UUID.randomUUID().toString(), "Break", 0, 45))
        val result = BreakTimeCalculator.calculate(8, 0, breaks)
        assertEquals("07:15:00", BreakTimeFormatter.formatDigitalDuration(result.netMinutes))
    }

    @Test
    fun criticalTest_decimalHours() {
        // Work: 8h, Break: 45m -> Expected: 7.25 hours
        val breaks = listOf(BreakDuration(UUID.randomUUID().toString(), "Break", 0, 45))
        val result = BreakTimeCalculator.calculate(8, 0, breaks)
        assertEquals("7.25 hours", BreakTimeFormatter.formatDecimalHours(result.netMinutes))
    }

    @Test
    fun criticalTest_breakPercentage() {
        // Work: 8h, Break: 45m -> Expected: 9.375%, Display: 9.4%
        val raw = BreakTimeFormatter.calculateBreakPercentageRaw(45L, 480L)
        assertEquals(9.375, raw, 0.001)
        val display = BreakTimeFormatter.formatBreakPercentage(45L, 480L)
        assertEquals("9.4%", display)
    }

    @Test
    fun criticalTest_largeDuration() {
        // Work: 100 hours, Break: 5 hours -> Expected: 95 hours, Digital: 95:00:00
        val breaks = listOf(BreakDuration(UUID.randomUUID().toString(), "Break", 5, 0))
        val result = BreakTimeCalculator.calculate(100, 0, breaks)
        assertEquals("95 hours", BreakTimeFormatter.formatDuration(result.netMinutes))
        assertEquals("95:00:00", BreakTimeFormatter.formatDigitalDuration(result.netMinutes))
    }

    @Test
    fun criticalTest_24Hours() {
        // Work: 24 hours, Break: 30 minutes -> Expected: 23h 30m, Digital: 23:30:00
        val breaks = listOf(BreakDuration(UUID.randomUUID().toString(), "Break", 0, 30))
        val result = BreakTimeCalculator.calculate(24, 0, breaks)
        assertEquals("23h 30m", BreakTimeFormatter.formatCompactDuration(result.netMinutes))
        assertEquals("23:30:00", BreakTimeFormatter.formatDigitalDuration(result.netMinutes))
    }

    @Test
    fun criticalTest_25Hours() {
        // Work: 25 hours, Break: 1 hour -> Expected: 24 hours, Digital: 24:00:00
        val breaks = listOf(BreakDuration(UUID.randomUUID().toString(), "Break", 1, 0))
        val result = BreakTimeCalculator.calculate(25, 0, breaks)
        assertEquals("24 hours", BreakTimeFormatter.formatDuration(result.netMinutes))
        assertEquals("24:00:00", BreakTimeFormatter.formatDigitalDuration(result.netMinutes))
    }

    @Test
    fun criticalTest_historyRestoreAndReset() {
        val historyPrefs = FakeSharedPreferences()
        val settingsPrefs = FakeSharedPreferences()
        val historyRepo = BreakTimeHistoryRepository(historyPrefs)
        val settingsRepo = SettingsRepository(settingsPrefs)
        val vm = BreakTimeViewModel(historyRepo, settingsRepo)

        // Perform calculation
        vm.onWorkHoursChange("8")
        vm.onWorkMinutesChange("0")
        vm.applyBreakPreset(0, 45, "Lunch")
        val success = vm.calculateNetTime()
        assertEquals(true, success)
        assertEquals(1, vm.uiState.value.history.size)
        assertEquals(435L, vm.uiState.value.result?.netMinutes)

        // Perform another calculation
        vm.onWorkHoursChange("6")
        vm.onWorkMinutesChange("0")
        vm.clearBreaks()
        vm.calculateNetTime()
        assertEquals(2, vm.uiState.value.history.size)

        // Restore first history item
        val firstItem = vm.uiState.value.history[1] // The earlier one
        vm.loadHistoryItem(firstItem)
        assertEquals("8", vm.uiState.value.workHoursInput)
        assertEquals("0", vm.uiState.value.workMinutesInput)
        assertEquals(1, vm.uiState.value.breaks.size)
        assertEquals(435L, vm.uiState.value.result?.netMinutes)

        // Reset
        vm.reset()
        assertEquals("8", vm.uiState.value.workHoursInput)
        assertEquals("0", vm.uiState.value.workMinutesInput)
        assertEquals(0, vm.uiState.value.breaks.size)
        assertNull(vm.uiState.value.result)
        // History remains
        assertEquals(2, vm.uiState.value.history.size)
    }

    @Test
    fun criticalTest_settingsDarkModeAndPreserve() {
        val historyPrefs = FakeSharedPreferences()
        val settingsPrefs = FakeSharedPreferences()
        val historyRepo = BreakTimeHistoryRepository(historyPrefs)
        val settingsRepo = SettingsRepository(settingsPrefs)
        val vm = BreakTimeViewModel(historyRepo, settingsRepo)

        // Change: Dark mode
        vm.setTheme(AppTheme.DARK)
        // Enable: Preserve Last Calculation
        vm.setPreserveLastCalculation(true)
        vm.onWorkHoursChange("7")
        vm.onWorkMinutesChange("45")
        vm.applyBreakPreset(0, 20, "Break 1")

        // Simulate closing and reopening app
        val newVm = BreakTimeViewModel(historyRepo, settingsRepo)
        assertEquals(AppTheme.DARK, newVm.uiState.value.settings.theme)
        assertEquals("7", newVm.uiState.value.workHoursInput)
        assertEquals("45", newVm.uiState.value.workMinutesInput)
        assertEquals(1, newVm.uiState.value.breaks.size)
        assertEquals(20L, newVm.uiState.value.breaks[0].minutes)
    }
}
