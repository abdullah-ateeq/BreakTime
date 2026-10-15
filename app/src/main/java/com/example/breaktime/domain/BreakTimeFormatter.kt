package com.example.breaktime.domain

import com.example.breaktime.model.BreakTimeResult
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object BreakTimeFormatter {

    fun formatDuration(totalMinutes: Long): String {
        if (totalMinutes <= 0L) return "0 minutes"
        val hours = totalMinutes / 60L
        val minutes = totalMinutes % 60L

        return when {
            hours > 0 && minutes > 0 -> {
                val hUnit = if (hours == 1L) "hour" else "hours"
                val mUnit = if (minutes == 1L) "minute" else "minutes"
                "$hours $hUnit $minutes $mUnit"
            }
            hours > 0 -> {
                val hUnit = if (hours == 1L) "hour" else "hours"
                "$hours $hUnit"
            }
            else -> {
                val mUnit = if (minutes == 1L) "minute" else "minutes"
                "$minutes $mUnit"
            }
        }
    }

    fun formatCompactDuration(totalMinutes: Long): String {
        if (totalMinutes <= 0L) return "0m"
        val hours = totalMinutes / 60L
        val minutes = totalMinutes % 60L

        return when {
            hours > 0 && minutes > 0 -> "${hours}h ${minutes}m"
            hours > 0 -> "${hours}h"
            else -> "${minutes}m"
        }
    }

    fun formatWorkSummaryWork(totalMinutes: Long): String {
        val hours = totalMinutes / 60L
        val minutes = totalMinutes % 60L
        return String.format(Locale.US, "%dh %02dm", hours, minutes)
    }

    fun formatDigitalDuration(totalMinutes: Long): String {
        val hours = totalMinutes / 60L
        val minutes = totalMinutes % 60L
        return String.format(Locale.US, "%02d:%02d:00", hours, minutes)
    }

    fun formatDecimalNumber(totalMinutes: Long): String {
        val decimal = totalMinutes / 60.0
        val df = DecimalFormat("0.0###", DecimalFormatSymbols(Locale.US)).apply {
            minimumFractionDigits = 1
            maximumFractionDigits = 4
        }
        return df.format(decimal)
    }

    fun formatDecimalHours(totalMinutes: Long): String {
        val number = formatDecimalNumber(totalMinutes)
        val unit = if (number == "1.0") "hour" else "hours"
        return "$number $unit"
    }

    fun calculateBreakPercentageRaw(breakMinutes: Long, workMinutes: Long): Double {
        if (workMinutes <= 0L) return 0.0
        return (breakMinutes.toDouble() / workMinutes.toDouble()) * 100.0
    }

    fun formatBreakPercentage(breakMinutes: Long, workMinutes: Long): String {
        if (workMinutes <= 0L) return "0%"
        val pct = calculateBreakPercentageRaw(breakMinutes, workMinutes)
        val df = DecimalFormat("0.#", DecimalFormatSymbols(Locale.US))
        return "${df.format(pct)}%"
    }

    fun formatBreakCount(count: Int): String {
        val unit = if (count == 1) "BREAK" else "BREAKS"
        return "$count $unit"
    }

    fun formatCopyText(result: BreakTimeResult): String {
        val builder = StringBuilder()
        builder.appendLine("BreakTime Result")
        builder.appendLine()
        builder.appendLine("Work duration: ${formatDuration(result.totalWorkMinutes)}")
        builder.appendLine("Total breaks: ${formatDuration(result.totalBreakMinutes)}")
        builder.appendLine("Net work time: ${formatDuration(result.netMinutes)}")
        builder.appendLine()
        builder.appendLine("Digital duration: ${formatDigitalDuration(result.netMinutes)}")
        builder.appendLine("Total net minutes: ${result.netMinutes}")
        builder.appendLine("Decimal net hours: ${formatDecimalNumber(result.netMinutes)}")
        builder.appendLine()
        builder.appendLine("Breaks:")
        if (result.breaks.isEmpty()) {
            builder.append("No breaks added")
        } else {
            result.breaks.forEachIndexed { index, b ->
                val line = "${b.label} — ${formatDuration(b.totalMinutes)}"
                if (index == result.breaks.lastIndex) {
                    builder.append(line)
                } else {
                    builder.appendLine(line)
                }
            }
        }
        return builder.toString()
    }

    fun formatShareText(result: BreakTimeResult): String {
        val builder = StringBuilder()
        builder.appendLine("BreakTime")
        builder.appendLine()
        builder.appendLine("Work: ${formatCompactDuration(result.totalWorkMinutes)}")
        builder.appendLine("Breaks: ${formatCompactDuration(result.totalBreakMinutes)}")
        builder.appendLine("Net: ${formatCompactDuration(result.netMinutes)}")
        builder.appendLine()
        builder.appendLine(formatDigitalDuration(result.netMinutes))
        builder.appendLine("${result.netMinutes} minutes")
        builder.append(formatDecimalHours(result.netMinutes))
        return builder.toString()
    }
}
