package com.example.breaktime.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.breaktime.model.BreakTimeUiState
import com.example.breaktime.ui.viewmodel.BreakTimeViewModel
import com.example.breaktime.util.HapticUtils
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: BreakTimeViewModel,
    uiState: BreakTimeUiState,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    val hapticEnabled = uiState.settings.hapticFeedback
    val animationsEnabled = uiState.settings.animationsEnabled

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // App Header
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "BreakTime",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
            Text(
                text = "Subtract breaks from a work duration.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }

        // Work Duration Card
        WorkDurationCard(
            hoursInput = uiState.workHoursInput,
            minutesInput = uiState.workMinutesInput,
            onHoursChange = {
                viewModel.onWorkHoursChange(it)
            },
            onMinutesChange = {
                viewModel.onWorkMinutesChange(it)
            },
            onPresetSelected = { h, m ->
                HapticUtils.performClick(haptic, hapticEnabled)
                viewModel.applyWorkPreset(h, m)
            }
        )

        // Breaks Section
        BreakListSection(
            breaks = uiState.breaks,
            onAddBreak = {
                HapticUtils.performClick(haptic, hapticEnabled)
                viewModel.addBreak()
            },
            onBreakPresetSelected = { h, m, label ->
                HapticUtils.performClick(haptic, hapticEnabled)
                viewModel.applyBreakPreset(h, m, label)
            },
            onUpdateLabel = { id, label ->
                viewModel.updateBreakLabel(id, label)
            },
            onUpdateHours = { id, hoursStr ->
                viewModel.updateBreakHours(id, hoursStr)
            },
            onUpdateMinutes = { id, minsStr ->
                viewModel.updateBreakMinutes(id, minsStr)
            },
            onDeleteBreak = { id ->
                HapticUtils.performClick(haptic, hapticEnabled)
                viewModel.deleteBreak(id)
            },
            onClearBreaks = {
                HapticUtils.performClick(haptic, hapticEnabled)
                viewModel.clearBreaks()
            },
            confirmDeleteEnabled = uiState.settings.confirmHistoryDelete
        )

        // Error message banner
        if (uiState.errorMessage != null) {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ErrorOutline,
                        contentDescription = "Error",
                        tint = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = uiState.errorMessage,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    )
                }
            }
        }

        // Primary Action: Calculate Net Time & Reset
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    HapticUtils.performSuccess(haptic, hapticEnabled)
                    viewModel.calculateNetTime()
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .weight(2f)
                    .height(52.dp)
                    .semantics { contentDescription = "Calculate net work time." }
            ) {
                Icon(
                    imageVector = Icons.Default.Calculate,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "CALCULATE NET TIME",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                )
            }

            OutlinedButton(
                onClick = {
                    HapticUtils.performClick(haptic, hapticEnabled)
                    viewModel.reset()
                },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .semantics { contentDescription = "Reset calculation" }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = "RESET",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }

        // Result Card (shown when calculated)
        AnimatedVisibility(
            visible = uiState.result != null,
            enter = if (animationsEnabled) fadeIn() + expandVertically() else fadeIn(),
            exit = if (animationsEnabled) fadeOut() + shrinkVertically() else fadeOut()
        ) {
            uiState.result?.let { result ->
                ResultCard(
                    result = result,
                    animationsEnabled = animationsEnabled,
                    onCopied = {
                        scope.launch {
                            snackbarHostState.showSnackbar("Result copied to clipboard")
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
