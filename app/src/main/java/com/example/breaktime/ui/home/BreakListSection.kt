package com.example.breaktime.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.ClearAll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.breaktime.domain.BreakTimeFormatter
import com.example.breaktime.model.BreakDuration

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BreakListSection(
    breaks: List<BreakDuration>,
    onAddBreak: () -> Unit,
    onBreakPresetSelected: (Long, Long, String) -> Unit,
    onUpdateLabel: (String, String) -> Unit,
    onUpdateHours: (String, String) -> Unit,
    onUpdateMinutes: (String, String) -> Unit,
    onDeleteBreak: (String) -> Unit,
    onClearBreaks: () -> Unit,
    confirmDeleteEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    var showClearConfirmDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "BREAKS",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = BreakTimeFormatter.formatBreakCount(breaks.size),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            if (breaks.isNotEmpty()) {
                OutlinedButton(
                    onClick = {
                        if (confirmDeleteEnabled) {
                            showClearConfirmDialog = true
                        } else {
                            onClearBreaks()
                        }
                    },
                    modifier = Modifier.semantics { contentDescription = "Clear breaks" }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ClearAll,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text("CLEAR BREAKS")
                }
            }
        }

        // Quick Break Presets
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "QUICK BREAK PRESETS",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val presets = listOf(
                    Triple("15 MIN", 0L, 15L),
                    Triple("30 MIN", 0L, 30L),
                    Triple("45 MIN", 0L, 45L),
                    Triple("1 HOUR", 1L, 0L)
                )

                presets.forEach { (label, h, m) ->
                    SuggestionChip(
                        onClick = { onBreakPresetSelected(h, m, label) },
                        label = { Text("+ $label", style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }
        }

        // Empty state or Break Rows
        if (breaks.isEmpty()) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "No breaks added",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Text(
                        text = "Add lunch, tea, or rest durations to subtract from your work time.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    )
                }
            }
        } else {
            breaks.forEachIndexed { index, b ->
                BreakRowItem(
                    index = index,
                    breakItem = b,
                    onLabelChange = { newLabel -> onUpdateLabel(b.id, newLabel) },
                    onHoursChange = { newHours -> onUpdateHours(b.id, newHours) },
                    onMinutesChange = { newMins -> onUpdateMinutes(b.id, newMins) },
                    onDelete = { onDeleteBreak(b.id) }
                )
            }
        }

        // Add Break Button
        FilledTonalButton(
            onClick = onAddBreak,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Add break." }
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null)
            Text(
                text = "+ ADD BREAK",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }

    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = { Text("Clear All Breaks?") },
            text = { Text("Are you sure you want to remove all breaks? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showClearConfirmDialog = false
                        onClearBreaks()
                    }
                ) {
                    Text("Clear", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
