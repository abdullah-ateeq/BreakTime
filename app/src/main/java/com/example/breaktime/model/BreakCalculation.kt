package com.example.breaktime.model

data class BreakCalculation(
    val id: String,
    val label: String,
    val originalHours: Long,
    val originalMinutes: Long,
    val totalMinutes: Long,
    val normalizedHours: Long,
    val normalizedMinutes: Long
)
