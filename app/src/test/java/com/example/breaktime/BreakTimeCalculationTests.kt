package com.example.breaktime

import com.example.breaktime.domain.BreakTimeCalculator
import com.example.breaktime.domain.BreakTimeFormatter
import com.example.breaktime.domain.BreakTimeValidator
import com.example.breaktime.model.BreakDuration
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class BreakTimeCalculationTests {

    // Test 1: No breaks
    @Test
    fun test01_noBreaks() {
        val result = BreakTimeCalculator.calculate(8, 0, emptyList())
        assertEquals(480L, result.totalWorkMinutes)
        assertEquals(0L, result.totalBreakMinutes)
        assertEquals(480L, result.netMinutes)
        assertEquals(8L, result.netHours)
        assertEquals(0L, result.netRemainingMinutes)
        assertEquals("8 hours", BreakTimeFormatter.formatDuration(result.netMinutes))
    }

    // Test 2: One break
    @Test
    fun test02_oneBreak() {
        val breaks = listOf(BreakDuration(UUID.randomUUID().toString(), "Break 1", 0, 30))
        val result = BreakTimeCalculator.calculate(8, 0, breaks)
        assertEquals(450L, result.netMinutes)
        assertEquals(7L, result.netHours)
        assertEquals(30L, result.netRemainingMinutes)
        assertEquals("7 hours 30 minutes", BreakTimeFormatter.formatDuration(result.netMinutes))
    }

    // Test 3: Two breaks
    @Test
    fun test03_twoBreaks() {
        val breaks = listOf(
            BreakDuration(UUID.randomUUID().toString(), "Lunch", 0, 30),
            BreakDuration(UUID.randomUUID().toString(), "Tea", 0, 15)
        )
        val result = BreakTimeCalculator.calculate(8, 0, breaks)
        assertEquals(435L, result.netMinutes)
        assertEquals(7L, result.netHours)
        assertEquals(15L, result.netRemainingMinutes)
        assertEquals("7 hours 15 minutes", BreakTimeFormatter.formatDuration(result.netMinutes))
    }

    // Test 4: Multiple breaks (Three breaks)
    @Test
    fun test04_multipleBreaks() {
        val breaks = listOf(
            BreakDuration(UUID.randomUUID().toString(), "Lunch", 0, 30),
            BreakDuration(UUID.randomUUID().toString(), "Tea", 0, 15),
            BreakDuration(UUID.randomUUID().toString(), "Prayer", 0, 15)
        )
        val result = BreakTimeCalculator.calculate(8, 0, breaks)
        assertEquals(420L, result.netMinutes)
        assertEquals(7L, result.netHours)
        assertEquals(0L, result.netRemainingMinutes)
        assertEquals("7 hours", BreakTimeFormatter.formatDuration(result.netMinutes))
    }

    // Test 5: Twenty breaks
    @Test
    fun test05_twentyBreaks() {
        val breaks = (1..20).map { i ->
            BreakDuration(UUID.randomUUID().toString(), "Break $i", 0, 15)
        }
        // 20 breaks of 15m = 300 minutes (5 hours)
        val result = BreakTimeCalculator.calculate(20, 0, breaks)
        assertEquals(1200L, result.totalWorkMinutes)
        assertEquals(300L, result.totalBreakMinutes)
        assertEquals(900L, result.netMinutes)
        assertEquals(15L, result.netHours)
        assertEquals(0L, result.netRemainingMinutes)
        assertEquals(20, result.breakCount)
    }

    // Test 6: Zero work duration
    @Test
    fun test06_zeroWorkDuration() {
        val result = BreakTimeCalculator.calculate(0, 0, emptyList())
        assertEquals(0L, result.totalWorkMinutes)
        assertEquals(0L, result.netMinutes)
        assertEquals("0 minutes", BreakTimeFormatter.formatDuration(result.netMinutes))
    }

    // Test 7: Zero breaks
    @Test
    fun test07_zeroBreaks() {
        val result = BreakTimeCalculator.calculate(8, 30, emptyList())
        assertEquals(510L, result.totalWorkMinutes)
        assertEquals(510L, result.netMinutes)
        assertEquals(0, result.breakCount)
    }

    // Test 8: Zero-minute break
    @Test
    fun test08_zeroMinuteBreak() {
        val breaks = listOf(BreakDuration(UUID.randomUUID().toString(), "Zero", 0, 0))
        val result = BreakTimeCalculator.calculate(8, 0, breaks)
        assertEquals(480L, result.netMinutes)
        assertEquals(0L, result.totalBreakMinutes)
    }

    // Test 9: One-minute break
    @Test
    fun test09_oneMinuteBreak() {
        val breaks = listOf(BreakDuration(UUID.randomUUID().toString(), "Quick", 0, 1))
        val result = BreakTimeCalculator.calculate(8, 0, breaks)
        assertEquals(479L, result.netMinutes)
        assertEquals(7L, result.netHours)
        assertEquals(59L, result.netRemainingMinutes)
        assertEquals("7 hours 59 minutes", BreakTimeFormatter.formatDuration(result.netMinutes))
    }

    // Test 10: Thirty-minute break
    @Test
    fun test10_thirtyMinuteBreak() {
        val breaks = listOf(BreakDuration(UUID.randomUUID().toString(), "Lunch", 0, 30))
        val result = BreakTimeCalculator.calculate(8, 0, breaks)
        assertEquals(450L, result.netMinutes)
        assertEquals("7 hours 30 minutes", BreakTimeFormatter.formatDuration(result.netMinutes))
    }

    // Test 11: One-hour break
    @Test
    fun test11_oneHourBreak() {
        val breaks = listOf(BreakDuration(UUID.randomUUID().toString(), "Hour", 1, 0))
        val result = BreakTimeCalculator.calculate(8, 0, breaks)
        assertEquals(420L, result.netMinutes)
        assertEquals("7 hours", BreakTimeFormatter.formatDuration(result.netMinutes))
    }

    // Test 12: Multiple-hour break
    @Test
    fun test12_multipleHourBreak() {
        val breaks = listOf(BreakDuration(UUID.randomUUID().toString(), "Extended", 2, 30))
        val result = BreakTimeCalculator.calculate(8, 0, breaks)
        assertEquals(330L, result.netMinutes)
        assertEquals(5L, result.netHours)
        assertEquals(30L, result.netRemainingMinutes)
        assertEquals("5 hours 30 minutes", BreakTimeFormatter.formatDuration(result.netMinutes))
    }

    // Test 13: Minute normalization
    @Test
    fun test13_minuteNormalization() {
        // Work: 7 hours 90 minutes -> 510 minutes = 8 hours 30 minutes
        val result = BreakTimeCalculator.calculate(7, 90, emptyList())
        assertEquals(510L, result.totalWorkMinutes)
        assertEquals(8L, result.normalizedWorkHours)
        assertEquals(30L, result.normalizedWorkMinutes)
    }

    // Test 14: 60 minutes break
    @Test
    fun test14_sixtyMinutesBreak() {
        val breaks = listOf(BreakDuration(UUID.randomUUID().toString(), "Break", 0, 60))
        val result = BreakTimeCalculator.calculate(8, 0, breaks)
        assertEquals(420L, result.netMinutes)
        assertEquals(7L, result.netHours)
        assertEquals(0L, result.netRemainingMinutes)
    }

    // Test 15: 75 minutes break
    @Test
    fun test15_seventyFiveMinutesBreak() {
        // 1 hour 75 minutes -> 135 minutes = 2 hours 15 minutes
        val breaks = listOf(BreakDuration(UUID.randomUUID().toString(), "Break", 1, 75))
        val result = BreakTimeCalculator.calculate(8, 0, breaks)
        assertEquals(135L, result.totalBreakMinutes)
        assertEquals(2L, result.breaks[0].normalizedHours)
        assertEquals(15L, result.breaks[0].normalizedMinutes)
        assertEquals(345L, result.netMinutes) // 480 - 135 = 345 (5h 45m)
    }

    // Test 16: 90 minutes break
    @Test
    fun test16_ninetyMinutesBreak() {
        val breaks = listOf(BreakDuration(UUID.randomUUID().toString(), "Break", 0, 90))
        val result = BreakTimeCalculator.calculate(8, 0, breaks)
        assertEquals(390L, result.netMinutes)
        assertEquals(6L, result.netHours)
        assertEquals(30L, result.netRemainingMinutes)
        assertEquals("6 hours 30 minutes", BreakTimeFormatter.formatDuration(result.netMinutes))
    }

    // Test 17: Work 8h − 30m
    @Test
    fun test17_work8hMinus30m() {
        val breaks = listOf(BreakDuration(UUID.randomUUID().toString(), "", 0, 30))
        val result = BreakTimeCalculator.calculate(8, 0, breaks)
        assertEquals(450L, result.netMinutes)
        assertEquals("7 hours 30 minutes", BreakTimeFormatter.formatDuration(result.netMinutes))
    }

    // Test 18: Work 8h − 45m
    @Test
    fun test18_work8hMinus45m() {
        val breaks = listOf(BreakDuration(UUID.randomUUID().toString(), "", 0, 45))
        val result = BreakTimeCalculator.calculate(8, 0, breaks)
        assertEquals(435L, result.netMinutes)
        assertEquals("7 hours 15 minutes", BreakTimeFormatter.formatDuration(result.netMinutes))
    }

    // Test 19: Work 8h − 1h
    @Test
    fun test19_work8hMinus1h() {
        val breaks = listOf(BreakDuration(UUID.randomUUID().toString(), "", 1, 0))
        val result = BreakTimeCalculator.calculate(8, 0, breaks)
        assertEquals(420L, result.netMinutes)
        assertEquals("7 hours", BreakTimeFormatter.formatDuration(result.netMinutes))
    }

    // Test 20: Work 8h − multiple breaks
    @Test
    fun test20_work8hMinusMultipleBreaks() {
        val breaks = listOf(
            BreakDuration(UUID.randomUUID().toString(), "Lunch", 0, 30),
            BreakDuration(UUID.randomUUID().toString(), "Tea", 0, 15)
        )
        val result = BreakTimeCalculator.calculate(8, 0, breaks)
        assertEquals(435L, result.netMinutes)
        assertEquals(2, result.breakCount)
    }

    // Test 21: Breaks equal work
    @Test
    fun test21_breaksEqualWork() {
        val breaks = listOf(BreakDuration(UUID.randomUUID().toString(), "All day", 8, 0))
        val result = BreakTimeCalculator.calculate(8, 0, breaks)
        assertEquals(0L, result.netMinutes)
        assertEquals(0L, result.netHours)
        assertEquals(0L, result.netRemainingMinutes)
    }

    // Test 22: Breaks greater than work
    @Test
    fun test22_breaksGreaterThanWork() {
        val breaks = listOf(BreakDuration(UUID.randomUUID().toString(), "Long break", 8, 30))
        val ex = assertThrows(IllegalArgumentException::class.java) {
            BreakTimeCalculator.calculate(8, 0, breaks)
        }
        assertEquals("Break time cannot exceed the work duration.", ex.message)
    }

    // Test 23: Large durations
    @Test
    fun test23_largeDurations() {
        val breaks = listOf(BreakDuration(UUID.randomUUID().toString(), "5h break", 5, 0))
        val result = BreakTimeCalculator.calculate(100, 0, breaks)
        assertEquals(6000L, result.totalWorkMinutes)
        assertEquals(300L, result.totalBreakMinutes)
        assertEquals(5700L, result.netMinutes)
        assertEquals(95L, result.netHours)
        assertEquals(0L, result.netRemainingMinutes)
        assertEquals("95:00:00", BreakTimeFormatter.formatDigitalDuration(result.netMinutes))
    }

    // Test 24: Overflow protection
    @Test
    fun test24_overflowProtection() {
        val ex = assertThrows(IllegalArgumentException::class.java) {
            BreakTimeCalculator.calculate(1_000_001L, 0, emptyList())
        }
        assertEquals("Duration is too large.", ex.message)
    }

    // Test 25: Decimal hours
    @Test
    fun test25_decimalHours() {
        val breaks = listOf(BreakDuration(UUID.randomUUID().toString(), "", 0, 45))
        val result = BreakTimeCalculator.calculate(8, 0, breaks)
        assertEquals(7.25, result.decimalNetHours, 0.001)
        assertEquals("7.25 hours", BreakTimeFormatter.formatDecimalHours(result.netMinutes))
        assertEquals("7.25", BreakTimeFormatter.formatDecimalNumber(result.netMinutes))
    }

    // Test 26: Digital duration
    @Test
    fun test26_digitalDuration() {
        assertEquals("00:30:00", BreakTimeFormatter.formatDigitalDuration(30L))
        assertEquals("01:00:00", BreakTimeFormatter.formatDigitalDuration(60L))
        assertEquals("07:15:00", BreakTimeFormatter.formatDigitalDuration(435L))
        assertEquals("23:30:00", BreakTimeFormatter.formatDigitalDuration(1410L))
        assertEquals("24:00:00", BreakTimeFormatter.formatDigitalDuration(1440L))
        assertEquals("25:00:00", BreakTimeFormatter.formatDigitalDuration(1500L))
        assertEquals("100:00:00", BreakTimeFormatter.formatDigitalDuration(6000L))
    }

    // Test 27: Total minutes
    @Test
    fun test27_totalMinutes() {
        val breaks = listOf(BreakDuration(UUID.randomUUID().toString(), "", 0, 45))
        val result = BreakTimeCalculator.calculate(8, 0, breaks)
        assertEquals(435L, result.netMinutes)
        assertEquals(480L, result.totalWorkMinutes)
        assertEquals(45L, result.totalBreakMinutes)
    }

    // Test 28: Break percentage
    @Test
    fun test28_breakPercentage() {
        val raw = BreakTimeFormatter.calculateBreakPercentageRaw(45L, 480L)
        assertEquals(9.375, raw, 0.001)
        val formatted = BreakTimeFormatter.formatBreakPercentage(45L, 480L)
        assertEquals("9.4%", formatted)
    }

    // Test 29: Singular / plural formatting
    @Test
    fun test29_singularPluralFormatting() {
        assertEquals("0 minutes", BreakTimeFormatter.formatDuration(0L))
        assertEquals("1 minute", BreakTimeFormatter.formatDuration(1L))
        assertEquals("2 minutes", BreakTimeFormatter.formatDuration(2L))
        assertEquals("1 hour", BreakTimeFormatter.formatDuration(60L))
        assertEquals("2 hours", BreakTimeFormatter.formatDuration(120L))
        assertEquals("1 hour 1 minute", BreakTimeFormatter.formatDuration(61L))
        assertEquals("2 hours 15 minutes", BreakTimeFormatter.formatDuration(135L))
    }

    // Test 30: Label handling
    @Test
    fun test30_labelHandling() {
        val sanitizedEmpty = BreakTimeValidator.sanitizeLabel("", 1)
        assertEquals("Break 1", sanitizedEmpty)

        val longLabel = "A".repeat(50)
        val sanitizedLong = BreakTimeValidator.sanitizeLabel(longLabel, 1)
        assertEquals(40, sanitizedLong.length)

        val breaks = listOf(
            BreakDuration(UUID.randomUUID().toString(), "", 0, 15),
            BreakDuration(UUID.randomUUID().toString(), "Lunch", 0, 30)
        )
        val result = BreakTimeCalculator.calculate(8, 0, breaks)
        assertEquals("Break 1", result.breaks[0].label)
        assertEquals("Lunch", result.breaks[1].label)
    }
}
