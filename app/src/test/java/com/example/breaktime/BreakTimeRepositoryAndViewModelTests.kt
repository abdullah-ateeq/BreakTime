package com.example.breaktime

import com.example.breaktime.data.BreakTimeHistoryRepository
import com.example.breaktime.data.SettingsRepository
import com.example.breaktime.domain.BreakTimeCalculator
import com.example.breaktime.domain.BreakTimeFormatter
import com.example.breaktime.model.AppTheme
import com.example.breaktime.model.BreakDuration
import com.example.breaktime.model.BreakTimeHistory
import com.example.breaktime.ui.viewmodel.BreakTimeViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.UUID

class BreakTimeRepositoryAndViewModelTests {

    private lateinit var historyPrefs: FakeSharedPreferences
    private lateinit var settingsPrefs: FakeSharedPreferences
    private lateinit var historyRepo: BreakTimeHistoryRepository
    private lateinit var settingsRepo: SettingsRepository
    private lateinit var viewModel: BreakTimeViewModel

    @Before
    fun setUp() {
        historyPrefs = FakeSharedPreferences()
        settingsPrefs = FakeSharedPreferences()
        historyRepo = BreakTimeHistoryRepository(historyPrefs)
        settingsRepo = SettingsRepository(settingsPrefs)
        viewModel = BreakTimeViewModel(historyRepo, settingsRepo)
    }

    // Test 35: History save
    @Test
    fun test35_historySave() {
        val entry = BreakTimeHistory(
            id = UUID.randomUUID().toString(),
            workHours = 8,
            workMinutes = 0,
            breaks = emptyList(),
            totalWorkMinutes = 480,
            totalBreakMinutes = 0,
            netMinutes = 480,
            calculatedAt = System.currentTimeMillis()
        )
        val history = historyRepo.addCalculation(entry)
        assertEquals(1, history.size)
        assertEquals(480L, historyRepo.getHistory()[0].netMinutes)
    }

    // Test 36: History load
    @Test
    fun test36_historyLoad() {
        val breaks = listOf(BreakDuration(UUID.randomUUID().toString(), "Lunch", 0, 45))
        val entry = BreakTimeHistory(
            id = UUID.randomUUID().toString(),
            workHours = 8,
            workMinutes = 0,
            breaks = breaks,
            totalWorkMinutes = 480,
            totalBreakMinutes = 45,
            netMinutes = 435,
            calculatedAt = System.currentTimeMillis()
        )
        viewModel.loadHistoryItem(entry)

        val state = viewModel.uiState.value
        assertEquals("8", state.workHoursInput)
        assertEquals("0", state.workMinutesInput)
        assertEquals(1, state.breaks.size)
        assertEquals("Lunch", state.breaks[0].label)
        assertNotNull(state.result)
        assertEquals(435L, state.result?.netMinutes)
    }

    // Test 37: History limit (Max 10)
    @Test
    fun test37_historyLimit() {
        for (i in 1..11) {
            val entry = BreakTimeHistory(
                id = "id_$i",
                workHours = i.toLong(),
                workMinutes = 0,
                breaks = emptyList(),
                totalWorkMinutes = i * 60L,
                totalBreakMinutes = 0,
                netMinutes = i * 60L,
                calculatedAt = System.currentTimeMillis() + i
            )
            historyRepo.addCalculation(entry)
        }
        val history = historyRepo.getHistory()
        assertEquals(10, history.size)
        // 11th was added, oldest (id_1) should have been removed
        assertFalse(history.any { it.id == "id_1" })
        assertEquals("id_11", history.first().id)
    }

    // Test 38: History deletion
    @Test
    fun test38_historyDeletion() {
        val entry1 = BreakTimeHistory("id_1", 8, 0, emptyList(), 480, 0, 480, 100L)
        val entry2 = BreakTimeHistory("id_2", 9, 0, emptyList(), 540, 0, 540, 200L)
        historyRepo.addCalculation(entry1)
        historyRepo.addCalculation(entry2)

        viewModel.deleteHistoryItem("id_1")
        val history = historyRepo.getHistory()
        assertEquals(1, history.size)
        assertEquals("id_2", history[0].id)
    }

    // Test 39: History clear
    @Test
    fun test39_historyClear() {
        val entry = BreakTimeHistory("id_1", 8, 0, emptyList(), 480, 0, 480, 100L)
        historyRepo.addCalculation(entry)
        assertEquals(1, historyRepo.getHistory().size)

        viewModel.clearHistory()
        assertEquals(0, historyRepo.getHistory().size)
        assertEquals(0, viewModel.uiState.value.history.size)
    }

    // Test 40: Settings persistence
    @Test
    fun test40_settingsPersistence() {
        viewModel.setTheme(AppTheme.DARK)
        viewModel.setDynamicColor(false)
        viewModel.setHapticFeedback(false)
        viewModel.setAnimationsEnabled(false)

        val newSettingsRepo = SettingsRepository(settingsPrefs)
        val loaded = newSettingsRepo.getSettings()
        assertEquals(AppTheme.DARK, loaded.theme)
        assertFalse(loaded.dynamicColor)
        assertFalse(loaded.hapticFeedback)
        assertFalse(loaded.animationsEnabled)
    }

    // Test 41: Last-calculation persistence
    @Test
    fun test41_lastCalculationPersistence() {
        viewModel.setPreserveLastCalculation(true)
        viewModel.onWorkHoursChange("9")
        viewModel.onWorkMinutesChange("30")
        viewModel.addBreak()

        val newSettingsRepo = SettingsRepository(settingsPrefs)
        assertEquals("9", newSettingsRepo.getLastWorkHours())
        assertEquals("30", newSettingsRepo.getLastWorkMinutes())
        assertEquals(1, newSettingsRepo.getLastBreaks().size)
    }

    // Test 42: Reset
    @Test
    fun test42_reset() {
        viewModel.onWorkHoursChange("10")
        viewModel.onWorkMinutesChange("15")
        viewModel.addBreak()
        viewModel.calculateNetTime()
        assertNotNull(viewModel.uiState.value.result)

        viewModel.reset()
        val state = viewModel.uiState.value
        assertEquals("8", state.workHoursInput)
        assertEquals("0", state.workMinutesInput)
        assertEquals(0, state.breaks.size)
        assertNull(state.result)
    }

    // Test 43: Copy formatting
    @Test
    fun test43_copyFormatting() {
        val breaks = listOf(
            BreakDuration("1", "Lunch", 0, 30),
            BreakDuration("2", "Tea", 0, 15)
        )
        val result = BreakTimeCalculator.calculate(8, 0, breaks)
        val text = BreakTimeFormatter.formatCopyText(result)
        assertTrue(text.contains("BreakTime Result"))
        assertTrue(text.contains("Work duration: 8 hours"))
        assertTrue(text.contains("Total breaks: 45 minutes"))
        assertTrue(text.contains("Net work time: 7 hours 15 minutes"))
        assertTrue(text.contains("Digital duration: 07:15:00"))
        assertTrue(text.contains("Total net minutes: 435"))
        assertTrue(text.contains("Decimal net hours: 7.25"))
        assertTrue(text.contains("Lunch — 30 minutes"))
        assertTrue(text.contains("Tea — 15 minutes"))
    }

    // Test 44: Share formatting
    @Test
    fun test44_shareFormatting() {
        val breaks = listOf(BreakDuration("1", "Break", 0, 45))
        val result = BreakTimeCalculator.calculate(8, 0, breaks)
        val text = BreakTimeFormatter.formatShareText(result)
        assertTrue(text.contains("BreakTime"))
        assertTrue(text.contains("Work: 8h"))
        assertTrue(text.contains("Breaks: 45m"))
        assertTrue(text.contains("Net: 7h 15m"))
        assertTrue(text.contains("07:15:00"))
        assertTrue(text.contains("435 minutes"))
        assertTrue(text.contains("7.25 hours"))
    }

    // Test 45: Quick work presets
    @Test
    fun test45_quickWorkPresets() {
        viewModel.applyWorkPreset(8, 30)
        assertEquals("8", viewModel.uiState.value.workHoursInput)
        assertEquals("30", viewModel.uiState.value.workMinutesInput)

        viewModel.applyWorkPreset(10, 0)
        assertEquals("10", viewModel.uiState.value.workHoursInput)
        assertEquals("0", viewModel.uiState.value.workMinutesInput)
    }

    // Test 46: Quick break presets
    @Test
    fun test46_quickBreakPresets() {
        viewModel.applyBreakPreset(0, 15, "15 MIN")
        assertEquals(1, viewModel.uiState.value.breaks.size)
        assertEquals(0L, viewModel.uiState.value.breaks[0].hours)
        assertEquals(15L, viewModel.uiState.value.breaks[0].minutes)
        assertEquals("15 MIN", viewModel.uiState.value.breaks[0].label)
    }

    // Test 47: Break add
    @Test
    fun test47_breakAdd() {
        viewModel.addBreak()
        assertEquals(1, viewModel.uiState.value.breaks.size)
        assertEquals(0L, viewModel.uiState.value.breaks[0].hours)
        assertEquals(30L, viewModel.uiState.value.breaks[0].minutes)
        assertEquals("Break 1", viewModel.uiState.value.breaks[0].label)
    }

    // Test 48: Break edit
    @Test
    fun test48_breakEdit() {
        viewModel.addBreak()
        val id = viewModel.uiState.value.breaks[0].id

        viewModel.updateBreakLabel(id, "Prayer")
        viewModel.updateBreakHours(id, "1")
        viewModel.updateBreakMinutes(id, "15")

        val b = viewModel.uiState.value.breaks[0]
        assertEquals("Prayer", b.label)
        assertEquals(1L, b.hours)
        assertEquals(15L, b.minutes)
    }

    // Test 49: Break delete
    @Test
    fun test49_breakDelete() {
        viewModel.addBreak()
        val id = viewModel.uiState.value.breaks[0].id
        viewModel.deleteBreak(id)
        assertEquals(0, viewModel.uiState.value.breaks.size)
    }

    // Test 50: Rapid calculation protection
    @Test
    fun test50_rapidCalculationProtection() {
        val firstAdded = viewModel.applyBreakPreset(0, 30, "30 MIN")
        assertTrue(firstAdded)
        // Immediate second tap within 300ms should be rejected
        val secondAdded = viewModel.applyBreakPreset(0, 30, "30 MIN")
        assertFalse(secondAdded)
        assertEquals(1, viewModel.uiState.value.breaks.size)
    }
}
