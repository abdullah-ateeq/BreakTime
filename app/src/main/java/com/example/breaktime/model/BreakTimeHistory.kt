package com.example.breaktime.model

data class BreakTimeHistory(
    val id: String,
    val workHours: Long,
    val workMinutes: Long,
    val breaks: List<BreakDuration>,
    val totalWorkMinutes: Long,
    val totalBreakMinutes: Long,
    val netMinutes: Long,
    val calculatedAt: Long
)
