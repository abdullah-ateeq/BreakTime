package com.example.breaktime.model

data class BreakTimeUiState(
    val workHoursInput: String = "8",
    val workMinutesInput: String = "0",
    val breaks: List<BreakDuration> = emptyList(),
    val result: BreakTimeResult? = null,
    val history: List<BreakTimeHistory> = emptyList(),
    val errorMessage: String? = null,
    val isCalculating: Boolean = false,
    val calculationState: CalculationState = CalculationState.Idle,
    val settings: AppSettings = AppSettings()
)

sealed interface CalculationState {
    data object Idle : CalculationState
    data object Calculated : CalculationState
    data class Error(val message: String) : CalculationState
}
