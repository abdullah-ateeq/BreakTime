package com.example.breaktime.domain

object BreakTimeValidator {
    const val MAX_HOURS: Long = 1_000_000L
    const val MAX_MINUTES: Long = MAX_HOURS * 60L
    const val MAX_LABEL_LENGTH: Int = 40

    sealed interface ValidationResult {
        data class Valid(val hours: Long, val minutes: Long) : ValidationResult
        data class Invalid(val message: String) : ValidationResult
    }

    fun validateWorkInput(hoursStr: String, minutesStr: String): ValidationResult {
        val trimmedHours = hoursStr.trim()
        val trimmedMinutes = minutesStr.trim()

        if (trimmedHours.isEmpty()) {
            return ValidationResult.Invalid("Work hours cannot be empty.")
        }
        if (trimmedMinutes.isEmpty()) {
            return ValidationResult.Invalid("Work minutes cannot be empty.")
        }

        val hours = trimmedHours.toLongOrNull()
            ?: return ValidationResult.Invalid("Work hours must be a whole non-negative number.")
        if (hours < 0) {
            return ValidationResult.Invalid("Work hours cannot be negative.")
        }
        if (hours > MAX_HOURS) {
            return ValidationResult.Invalid("Duration is too large.")
        }

        val minutes = trimmedMinutes.toLongOrNull()
            ?: return ValidationResult.Invalid("Work minutes must be a whole non-negative number.")
        if (minutes < 0) {
            return ValidationResult.Invalid("Work minutes cannot be negative.")
        }

        try {
            val totalMinutes = Math.addExact(Math.multiplyExact(hours, 60L), minutes)
            if (totalMinutes > MAX_MINUTES) {
                return ValidationResult.Invalid("Duration is too large.")
            }
        } catch (_: ArithmeticException) {
            return ValidationResult.Invalid("Duration is too large.")
        }

        return ValidationResult.Valid(hours, minutes)
    }

    fun validateBreakInput(hoursStr: String, minutesStr: String, label: String = ""): ValidationResult {
        val trimmedHours = hoursStr.trim()
        val trimmedMinutes = minutesStr.trim()

        if (label.length > MAX_LABEL_LENGTH) {
            return ValidationResult.Invalid("Break label cannot exceed 40 characters.")
        }

        if (trimmedHours.isEmpty()) {
            return ValidationResult.Invalid("Break hours cannot be empty.")
        }
        if (trimmedMinutes.isEmpty()) {
            return ValidationResult.Invalid("Break minutes cannot be empty.")
        }

        val hours = trimmedHours.toLongOrNull()
            ?: return ValidationResult.Invalid("Break hours must be a whole non-negative number.")
        if (hours < 0) {
            return ValidationResult.Invalid("Break hours cannot be negative.")
        }
        if (hours > MAX_HOURS) {
            return ValidationResult.Invalid("Duration is too large.")
        }

        val minutes = trimmedMinutes.toLongOrNull()
            ?: return ValidationResult.Invalid("Break minutes must be a whole non-negative number.")
        if (minutes < 0) {
            return ValidationResult.Invalid("Break minutes cannot be negative.")
        }

        try {
            val totalMinutes = Math.addExact(Math.multiplyExact(hours, 60L), minutes)
            if (totalMinutes > MAX_MINUTES) {
                return ValidationResult.Invalid("Duration is too large.")
            }
        } catch (_: ArithmeticException) {
            return ValidationResult.Invalid("Duration is too large.")
        }

        return ValidationResult.Valid(hours, minutes)
    }

    fun sanitizeLabel(rawLabel: String, defaultIndex: Int): String {
        val trimmed = rawLabel.trim()
        if (trimmed.isEmpty()) {
            return "Break $defaultIndex"
        }
        return if (trimmed.length > MAX_LABEL_LENGTH) trimmed.take(MAX_LABEL_LENGTH) else trimmed
    }
}
