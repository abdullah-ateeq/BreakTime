package com.example.breaktime.model

data class BreakTimeResult(
    val originalWorkHours: Long,
    val originalWorkMinutes: Long,
    val totalWorkMinutes: Long,
    val normalizedWorkHours: Long,
    val normalizedWorkMinutes: Long,
    val totalBreakMinutes: Long,
    val breakCount: Int,
    val netMinutes: Long,
    val netHours: Long,
    val netRemainingMinutes: Long,
    val decimalWorkHours: Double,
    val decimalBreakHours: Double,
    val decimalNetHours: Double,
    val breaks: List<BreakCalculation>
)
