package com.example.breaktime.domain

import com.example.breaktime.model.BreakCalculation
import com.example.breaktime.model.BreakDuration
import com.example.breaktime.model.BreakTimeResult

object BreakTimeCalculator {
    const val MAX_HOURS: Long = 1_000_000L
    const val MAX_MINUTES: Long = MAX_HOURS * 60L

    fun calculate(
        workHours: Long,
        workMinutes: Long,
        breaks: List<BreakDuration>
    ): BreakTimeResult {
        if (workHours < 0 || workMinutes < 0) {
            throw IllegalArgumentException("Work hours and minutes must be non-negative.")
        }
        if (workHours > MAX_HOURS) {
            throw IllegalArgumentException("Duration is too large.")
        }

        val workTotalMinutes = try {
            val hMin = Math.multiplyExact(workHours, 60L)
            val total = Math.addExact(hMin, workMinutes)
            if (total > MAX_MINUTES) throw IllegalArgumentException("Duration is too large.")
            total
        } catch (_: ArithmeticException) {
            throw IllegalArgumentException("Duration is too large.")
        }

        var totalBreakMinutes = 0L
        val breakCalculations = ArrayList<BreakCalculation>(breaks.size)

        for ((index, b) in breaks.withIndex()) {
            if (b.hours < 0 || b.minutes < 0) {
                throw IllegalArgumentException("Break hours and minutes must be non-negative.")
            }
            if (b.hours > MAX_HOURS) {
                throw IllegalArgumentException("Duration is too large.")
            }

            val bTotalMinutes = try {
                val hMin = Math.multiplyExact(b.hours, 60L)
                val total = Math.addExact(hMin, b.minutes)
                if (total > MAX_MINUTES) throw IllegalArgumentException("Duration is too large.")
                total
            } catch (_: ArithmeticException) {
                throw IllegalArgumentException("Duration is too large.")
            }

            totalBreakMinutes = try {
                val sum = Math.addExact(totalBreakMinutes, bTotalMinutes)
                if (sum > MAX_MINUTES) throw IllegalArgumentException("Duration is too large.")
                sum
            } catch (_: ArithmeticException) {
                throw IllegalArgumentException("Duration is too large.")
            }

            val rawLabel = b.label.trim()
            val finalLabel = if (rawLabel.isEmpty()) "Break ${index + 1}" else {
                if (rawLabel.length > 40) rawLabel.take(40) else rawLabel
            }

            breakCalculations.add(
                BreakCalculation(
                    id = b.id,
                    label = finalLabel,
                    originalHours = b.hours,
                    originalMinutes = b.minutes,
                    totalMinutes = bTotalMinutes,
                    normalizedHours = bTotalMinutes / 60L,
                    normalizedMinutes = bTotalMinutes % 60L
                )
            )
        }

        if (totalBreakMinutes > workTotalMinutes) {
            throw IllegalArgumentException("Break time cannot exceed the work duration.")
        }

        val netMinutes = workTotalMinutes - totalBreakMinutes

        val normalizedWorkHours = workTotalMinutes / 60L
        val normalizedWorkMinutes = workTotalMinutes % 60L

        val netHours = netMinutes / 60L
        val netRemainingMinutes = netMinutes % 60L

        val decimalWorkHours = workTotalMinutes / 60.0
        val decimalBreakHours = totalBreakMinutes / 60.0
        val decimalNetHours = netMinutes / 60.0

        return BreakTimeResult(
            originalWorkHours = workHours,
            originalWorkMinutes = workMinutes,
            totalWorkMinutes = workTotalMinutes,
            normalizedWorkHours = normalizedWorkHours,
            normalizedWorkMinutes = normalizedWorkMinutes,
            totalBreakMinutes = totalBreakMinutes,
            breakCount = breaks.size,
            netMinutes = netMinutes,
            netHours = netHours,
            netRemainingMinutes = netRemainingMinutes,
            decimalWorkHours = decimalWorkHours,
            decimalBreakHours = decimalBreakHours,
            decimalNetHours = decimalNetHours,
            breaks = breakCalculations
        )
    }
}
